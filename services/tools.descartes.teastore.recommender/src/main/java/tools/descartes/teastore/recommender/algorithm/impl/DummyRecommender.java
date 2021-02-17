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
package tools.descartes.teastore.recommender.algorithm.impl;

import java.util.ArrayList;
import java.util.List;

import dmodel.designtime.monitoring.controller.ServiceParameters;
import dmodel.designtime.monitoring.controller.ThreadMonitoringController;
import tools.descartes.teastore.monitoring.TeastoreMonitoringMetadata;
import tools.descartes.teastore.recommender.algorithm.AbstractRecommender;

/**
 * Temporary class to be replaced by something actually useful.
 * 
 * @author Johannes Grohmann
 *
 */
public class DummyRecommender extends AbstractRecommender {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.teastore.recommender.algorithm.AbstractRecommender#execute(
	 * java.util.List)
	 */
	@Override
	protected List<Long> execute(Long userid, List<Long> currentItems) {
		ThreadMonitoringController.getInstance()
				.enterService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_DUMMY_RECOMMEND, this);
		ThreadMonitoringController.getInstance().enterInternalAction(
				TeastoreMonitoringMetadata.INTERNAL_ACTION_RECOMMENDER_DUMMY_RECOMMEND,
				TeastoreMonitoringMetadata.RESOURCE_CPU);

		List<Long> recommended = new ArrayList<Long>();
		recommended.add(-1L);
		
		ThreadMonitoringController.getInstance().exitInternalAction(
				TeastoreMonitoringMetadata.INTERNAL_ACTION_RECOMMENDER_DUMMY_RECOMMEND,
				TeastoreMonitoringMetadata.RESOURCE_CPU);
		
		ThreadMonitoringController.getInstance()
				.exitService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_DUMMY_RECOMMEND);
		return recommended;
	}
	
	@Override
	protected void executePreprocessing(long orders, long orderItems, boolean monitor) {
		ServiceParameters parameters = new ServiceParameters();
		parameters.addValue("orders.VALUE", orders);
		parameters.addValue("orderItems.VALUE", orderItems);
		ThreadMonitoringController.getInstance()
				.enterService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_DUMMY_TRAIN, this, parameters);
		ThreadMonitoringController.getInstance()
				.exitService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_DUMMY_TRAIN);
	}

}
