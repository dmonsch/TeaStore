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
package tools.descartes.teastore.recommender.algorithm.impl.pop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dmodel.designtime.monitoring.controller.ServiceParameters;
import dmodel.designtime.monitoring.controller.ThreadMonitoringController;
import tools.descartes.teastore.monitoring.TeastoreMonitoringMetadata;
import tools.descartes.teastore.recommender.algorithm.AbstractRecommender;

/**
 * A simple Recommender that makes recommendations based on general popularity.
 * 
 * @author Johannes Grohmann
 *
 */
public class PopularityBasedRecommender extends AbstractRecommender {

	/**
	 * Map with all product IDs and their corresponding total purchase counts.
	 */
	private HashMap<Long, Double> counts;

	/*
	 * (non-Javadoc)
	 * 
	 * @see tools.descartes.teastore.recommender.algorithm.AbstractRecommender#
	 * execute( java.util.List)
	 */
	@Override
	protected List<Long> execute(Long userid, List<Long> currentItems) {
		ServiceParameters parameters = new ServiceParameters();
		parameters.addValue("items.VALUE", currentItems.size());
		ThreadMonitoringController.getInstance()
				.enterService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_POPULARITY_RECOMMEND, this, parameters);
		ThreadMonitoringController.getInstance().enterInternalAction(
				TeastoreMonitoringMetadata.INTERNAL_ACTION_RECOMMENDER_POPULARITY_RECOMMEND,
				TeastoreMonitoringMetadata.RESOURCE_CPU);

		try {
			return filterRecommendations(counts, currentItems);
		} finally {
			ThreadMonitoringController.getInstance().exitInternalAction(
					TeastoreMonitoringMetadata.INTERNAL_ACTION_RECOMMENDER_POPULARITY_RECOMMEND,
					TeastoreMonitoringMetadata.RESOURCE_CPU);
			ThreadMonitoringController.getInstance()
					.exitService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_POPULARITY_RECOMMEND);
		}
	}

	@Override
	protected void executePreprocessing(long orders, long orderItems) {
		ServiceParameters parameters = new ServiceParameters();
		parameters.addValue("orders.VALUE", orders);
		parameters.addValue("orderItems.VALUE", orderItems);
		
		ThreadMonitoringController.getInstance().enterService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_POPULARITY_TRAIN, this);
		// assigns each product a quantity
		counts = new HashMap<>();
		// calculate product frequencies
		for (Map<Long, Double> usermap : getUserBuyingMatrix().values()) {
			for (Entry<Long, Double> product : usermap.entrySet()) {
				if (!counts.containsKey(product.getKey())) {
					counts.put(product.getKey(), product.getValue());
				} else {
					counts.put(product.getKey(), counts.get(product.getKey()) + product.getValue());
				}
			}
		}
		
		ThreadMonitoringController.getInstance().exitService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_POPULARITY_TRAIN);

	}
}
