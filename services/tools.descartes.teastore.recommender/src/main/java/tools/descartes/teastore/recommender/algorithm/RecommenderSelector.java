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
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.cocome.tradingsystem.inventory.application.store.monitoring.MonitoringMetadata;
import org.cocome.tradingsystem.inventory.application.store.monitoring.ServiceParameters;
import org.cocome.tradingsystem.inventory.application.store.monitoring.ThreadMonitoringController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Lists;

import tools.descartes.teastore.recommender.algorithm.impl.UseFallBackException;
import tools.descartes.teastore.recommender.algorithm.impl.cf.PreprocessedSlopeOneRecommender;
import tools.descartes.teastore.recommender.algorithm.impl.cf.SlopeOneRecommender;
import tools.descartes.teastore.recommender.algorithm.impl.orderbased.OrderBasedRecommender;
import tools.descartes.teastore.recommender.algorithm.impl.pop.PopularityBasedRecommender;
import tools.descartes.teastore.recommender.servlet.TrainingSynchronizer;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;

/**
 * A strategy selector for the Recommender functionality.
 * 
 * @author Johannes Grohmann
 *
 */
public final class RecommenderSelector implements IRecommender {
	
	private int EVOLUTION_SCENARIO = 1;

	/**
	 * This map lists all currently available recommending approaches and assigns
	 * them their "name" for the environment variable.
	 */
	private static Map<RecommenderEnum, Class<? extends IRecommender>> recommenders = new HashMap<>();

	static {
		recommenders = new HashMap<RecommenderEnum, Class<? extends IRecommender>>();
		recommenders.put(RecommenderEnum.POPULARITY, PopularityBasedRecommender.class);
		recommenders.put(RecommenderEnum.SLOPE_ONE, SlopeOneRecommender.class);
		recommenders.put(RecommenderEnum.PREPROC_SLOPE_ONE, PreprocessedSlopeOneRecommender.class);
		recommenders.put(RecommenderEnum.ORDER_BASED, OrderBasedRecommender.class);
	}

	private static final Logger LOG = LoggerFactory.getLogger(RecommenderSelector.class);

	private static RecommenderSelector instance;

	private Map<RecommenderEnum, IRecommender> recommenderInstances;

	/**
	 * Private Constructor.
	 */
	private RecommenderSelector() {
		this.recommenderInstances = new HashMap<>();

		for (RecommenderEnum val : RecommenderEnum.values()) {
			try {
				this.recommenderInstances.put(val, recommenders.get(val).newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LOG.warn("Failed to create a recommender instance.");
			}
		}
	}

	@Override
	public List<Long> recommendProducts(Long userid, List<OrderItem> currentItems, RecommenderEnum recommender)
			throws UnsupportedOperationException {
		
		if (currentItems.size() == 1) {
			TrainingSynchronizer.getInstance().retrieveDataAndRetrain();
		}
		
		long start = System.currentTimeMillis();
		try {
			IRecommender resolved = this.recommenderInstances.get(recommender);
			return resolved.recommendProducts(userid, currentItems, recommender);
		} catch (UseFallBackException e) {
			return Lists.newArrayList();
		} finally {
			// java.lang.System.out.println("Recommending needed " + (System.currentTimeMillis() - start) + "ms.");
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
		// train all
		if (EVOLUTION_SCENARIO == 1) {
			// only train one
			recommenderInstances.get(RecommenderEnum.POPULARITY).train(orderItems, orders);
		} else if (EVOLUTION_SCENARIO == 2) {
			for (Entry<RecommenderEnum, IRecommender> entry : recommenderInstances.entrySet()) {
				entry.getValue().train(orderItems, orders);
			}
		}
	}

}
