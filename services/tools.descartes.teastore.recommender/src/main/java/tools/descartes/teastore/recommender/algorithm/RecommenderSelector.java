/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.recommender.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Lists;

import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.recommender.algorithm.impl.UseFallBackException;
import tools.descartes.teastore.recommender.algorithm.impl.cf.PreprocessedSlopeOneRecommender;
import tools.descartes.teastore.recommender.algorithm.impl.cf.SlopeOneRecommender;
import tools.descartes.teastore.recommender.algorithm.impl.orderbased.OrderBasedRecommender;
import tools.descartes.teastore.recommender.algorithm.impl.pop.PopularityBasedRecommender;
import tools.descartes.teastore.recommender.monitoring.MonitoringConfiguration;
import tools.descartes.teastore.recommender.monitoring.MonitoringMetadata;
import tools.descartes.teastore.recommender.monitoring.ThreadMonitoringController;
import tools.descartes.teastore.recommender.monitoring.util.RemoteDatabaseUtil;
import tools.descartes.teastore.recommender.servlet.TrainingSynchronizer;

/**
 * A strategy selector for the Recommender functionality.
 * 
 * @author Johannes Grohmann
 *
 */

public final class RecommenderSelector implements IRecommender {
	private static final Logger LOG = LoggerFactory.getLogger(RecommenderSelector.class);
	
	// get the current scenario
	private static final int EVOLUTION_SCENARIO = MonitoringConfiguration.EVOLUTION_SCENARIO;

	/**
	 * This map lists all currently available recommending approaches and assigns
	 * them their "name" for the environment variable.
	 */
	private static Map<RecommenderEnum, Class<? extends IRecommender>> recommenders = new HashMap<>();
	private static RecommenderSelector instance;
	private Map<RecommenderEnum, IRecommender> recommenderInstances;

	static {
		recommenders = new HashMap<RecommenderEnum, Class<? extends IRecommender>>();
		recommenders.put(RecommenderEnum.POPULARITY, PopularityBasedRecommender.class);
		recommenders.put(RecommenderEnum.SLOPE_ONE, SlopeOneRecommender.class);
		recommenders.put(RecommenderEnum.PREPROC_SLOPE_ONE, PreprocessedSlopeOneRecommender.class);
		recommenders.put(RecommenderEnum.ORDER_BASED, OrderBasedRecommender.class);
	}
	
	// for evolution scenario 3 (syncing)
	private boolean stopped = false;
	private boolean evolutionStarted = false;

	// for evolution scenario 3
	private RecommenderEnum currentlySelected = RecommenderEnum.POPULARITY; // start with
	private RecommenderEnum[] staticFollows = new RecommenderEnum[] { RecommenderEnum.SLOPE_ONE,
			RecommenderEnum.PREPROC_SLOPE_ONE, RecommenderEnum.ORDER_BASED }; // following ones
	private int currentSelectedId = -1; // current selection
	private int innerState = 0; // fine grained or coarse grained

	private RemoteDatabaseUtil databaseUtil;

	/**
	 * Private Constructor.
	 */
	private RecommenderSelector() {
		this.recommenderInstances = new HashMap<>();
		this.databaseUtil = new RemoteDatabaseUtil();

		for (RecommenderEnum val : RecommenderEnum.values()) {
			try {
				this.recommenderInstances.put(val, recommenders.get(val).newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LOG.warn("Failed to create a recommender instance.");
			}
		}

		ThreadMonitoringController.getInstance().registerCpuSampler(MonitoringMetadata.CONTAIMER_SIMPLE_SERVER,
				"<none>", MonitoringConfiguration.FINE_GRANULAR_INIT || MonitoringConfiguration.EVOLUTION_RECOGNIZED);
	}

	@Override
	public List<Long> recommendProducts(Long userid, List<OrderItem> currentItems, RecommenderEnum recommender)
			throws UnsupportedOperationException {

		if (currentItems.size() == 1) {
			TrainingSynchronizer.getInstance().retrieveDataAndRetrain();
		}

		try {
			IRecommender resolved = this.recommenderInstances.get(recommender);
			return resolved.recommendProducts(userid, currentItems, recommender);
		} catch (UseFallBackException e) {
			return Lists.newArrayList();
		}
	}

	/**
	 * Returns the instance of this Singleton or creates a new one, if this is the
	 * first call of this method.
	 * 
	 * @return The instance of this class.
	 */
	public static synchronized RecommenderSelector getInstance() {
		if (instance == null) {
			ThreadMonitoringController.getInstance().registerCpuSampler(MonitoringMetadata.CONTAIMER_SIMPLE_SERVER,
					"<none>");
			instance = new RecommenderSelector();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tools.descartes.teastore.recommender.IRecommender#train(java.util.List,
	 * java.util.List)
	 */
	@Override
	public synchronized void train(List<OrderItem> orderItems, List<Order> orders) {
		if (stopped) {
			return;
		}
		// train all
		if (EVOLUTION_SCENARIO == 1) {
			// only train one
			recommenderInstances.get(RecommenderEnum.PREPROC_SLOPE_ONE).train(orderItems, orders);
		} else if (EVOLUTION_SCENARIO == 2) {
			// train all
			for (Entry<RecommenderEnum, IRecommender> entry : recommenderInstances.entrySet()) {
				entry.getValue().train(orderItems, orders);
			}
		} else if (EVOLUTION_SCENARIO == 3) {
			// train one that is selected atm
			recommenderInstances.get(currentlySelected).train(orderItems, orders);

			// start evolution, if not yet done
			if (EVOLUTION_SCENARIO == 3 && !evolutionStarted) {
				evolutionStarted = true;
				Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> updateScenario(), 10, 10,
						TimeUnit.MINUTES);
			}
		}
	}

	private void updateScenario() {
		if (innerState == 0) {
			// separate monitoring data of iterations
			ThreadMonitoringController.getInstance().newMonitoringController(false);
			switchInnerState();
		} else {
			stopped = true;
			swapCurrentRecommenderImplementation();

			// separate monitoring data of iterations
			ThreadMonitoringController.getInstance().newMonitoringController(true);

			// clear database if it is conceptual a new iteration
			if (MonitoringConfiguration.EVOLUTION_CLEAR_DB_AFTER_COMMIT) {
				databaseUtil.clearDatabase();
			}

			switchInnerState();
			stopped = false;
		}
	}

	private void swapCurrentRecommenderImplementation() {
		int nextSelection = ++currentSelectedId;
		if (nextSelection >= staticFollows.length) {
			// random selection
			Random rand = new Random();
			int randomSelection = rand.nextInt(RecommenderEnum.values().length);
			while (RecommenderEnum.values()[randomSelection] == currentlySelected) {
				randomSelection = rand.nextInt(RecommenderEnum.values().length);
			}
			currentlySelected = RecommenderEnum.values()[randomSelection];
		} else {
			currentlySelected = staticFollows[nextSelection];
		}
	}

	private void switchInnerState() {
		MonitoringConfiguration.EVOLUTION_RECOGNIZED = !MonitoringConfiguration.EVOLUTION_RECOGNIZED;
		innerState = innerState == 0 ? 1 : 0;
	}

}
