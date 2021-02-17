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
package tools.descartes.teastore.recommender.algorithm.impl.cf;

import java.util.HashMap;
import java.util.Map;

import dmodel.designtime.monitoring.controller.ServiceParameters;
import dmodel.designtime.monitoring.controller.ThreadMonitoringController;
import tools.descartes.teastore.monitoring.TeastoreMonitoringMetadata;

/**
 * Recommender based on item-based collaborative filtering with the slope one
 * algorithm.
 * 
 * @author Johannes Grohmann
 *
 */
public class PreprocessedSlopeOneRecommender extends SlopeOneRecommender {

	/**
	 * Represents a matrix, assigning each user a calculated score for each item.
	 * This score can be used to recommend items.
	 */
	private Map<Long, Map<Long, Double>> predictedRatings;

	/**
	 * @return the predictedRatings
	 */
	public Map<Long, Map<Long, Double>> getPredictedRatings() {
		return predictedRatings;
	}

	/**
	 * @param predictedRatings the predictedRatings to set
	 */
	public void setPredictedRatings(Map<Long, Map<Long, Double>> predictedRatings) {
		this.predictedRatings = predictedRatings;
	}

	@Override
	protected Map<Long, Double> getUserVector(Long userid) {
		// improve performance by preprocessing and storing userids
		return predictedRatings.get(userid);
	}

	@Override
	protected void executePreprocessing(long orders, long orderItems, boolean monitor) {
		ServiceParameters parameters = new ServiceParameters();
		parameters.addValue("orders.VALUE", orders);
		parameters.addValue("orderItems.VALUE", orderItems);
		ThreadMonitoringController.getInstance()
				.enterService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_SLOPE_ONE_PREPROC_TRAIN, this, parameters);
		// The buying matrix is considered to be the rating
		// i.e. the more buys, the higher the rating
		ThreadMonitoringController.getInstance().enterInternalAction(
				TeastoreMonitoringMetadata.INTERNAL_ACTION_RECOMMENDER_SLOPE_ONE_PREPROC_TRAIN,
				TeastoreMonitoringMetadata.RESOURCE_CPU);
		
		super.executePreprocessing(orders, orderItems, false);
		
		predictedRatings = new HashMap<>();
		// Moving the matrix calculation to the preprocessing to optimize runtime
		// behavior
		for (Long userid : getUserBuyingMatrix().keySet()) {
			// for all known users
			Map<Long, Double> pred = super.getUserVector(userid);
			predictedRatings.put(userid, pred);
		}
		
		ThreadMonitoringController.getInstance().exitInternalAction(
				TeastoreMonitoringMetadata.INTERNAL_ACTION_RECOMMENDER_SLOPE_ONE_PREPROC_TRAIN,
				TeastoreMonitoringMetadata.RESOURCE_CPU);
		ThreadMonitoringController.getInstance()
				.exitService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_SLOPE_ONE_PREPROC_TRAIN);
	}

	@Override
	protected String getServiceId() {
		return TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_SLOPE_ONE_PREPROC_RECOMMEND;
	}

	@Override
	protected String getInternalActionId() {
		return TeastoreMonitoringMetadata.INTERNAL_ACTION_RECOMMENDER_SLOPE_ONE_PREPROC_RECOMMEND;
	}
}
