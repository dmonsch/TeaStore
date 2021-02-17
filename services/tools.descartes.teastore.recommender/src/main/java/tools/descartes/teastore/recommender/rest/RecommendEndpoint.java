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
package tools.descartes.teastore.recommender.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import dmodel.designtime.monitoring.controller.ServiceParameters;
import dmodel.designtime.monitoring.controller.ThreadMonitoringController;
import tools.descartes.teastore.recommender.algorithm.RecommenderSelector;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.User;
import tools.descartes.teastore.monitoring.TeastoreMonitoringMetadata;

/**
 * Recommender REST endpoint.
 * 
 * @author Johannes Grohmann
 *
 */
@Path("recommend")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class RecommendEndpoint {

	/**
	 * Return a list of all {@link Product}s, that are recommended for the given
	 * {@link User} buying the given list of {@link OrderItem}s. <br>
	 * 
	 * The returning list does not contain any {@link Product} that is already part
	 * of the given list of {@link OrderItem}s. It might be empty, however.
	 * 
	 * @param currentItems A list, containing all {@link OrderItem}s in the current
	 *                     cart. Might be empty.
	 * @param uid          The id of the {@link User} to recommend for. May be null.
	 * @return List of {@link Long} objects, containing all {@link Product} IDs that
	 *         are recommended to add to the cart, or an INTERNALSERVERERROR, if the
	 *         recommendation failed.
	 */
	@POST
	public Response recommend(List<OrderItem> currentItems, @QueryParam("uid") final Long uid,
			@QueryParam("startTime") long start, @QueryParam("monitoringTraceId") String monitoringTraceId,
			@QueryParam("monitoringExternalId") String monitoringExternalId) {
		ThreadMonitoringController.getInstance().continueFromRemote(monitoringTraceId, monitoringExternalId);
		ThreadMonitoringController.getInstance().logInternalAction(
				TeastoreMonitoringMetadata.INTERNAL_ACTION_REGISTRY_RECOMMENDER_RECOMMEND_REST,
				TeastoreMonitoringMetadata.RESOURCE_CPU, start);

		ServiceParameters parameters = new ServiceParameters();
		parameters.addValue("items", currentItems.size());
		ThreadMonitoringController.getInstance()
				.enterService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_RECOMMEND_PRODUCTS, this, parameters);

		try {
			ThreadMonitoringController.getInstance()
					.setExternalCallId(TeastoreMonitoringMetadata.EXTERNAL_CALL_RECOMMENDER_STRATEGY_RECOMMEND);
			List<Long> recommended = RecommenderSelector.getInstance().recommendProducts(uid, currentItems);

			return Response.ok().entity(recommended).build();
		} finally {
			ThreadMonitoringController.getInstance()
					.exitService(TeastoreMonitoringMetadata.SERVICE_RECOMMENDER_RECOMMEND_PRODUCTS);
			ThreadMonitoringController.getInstance().detachFromRemote();
		}
	}

	@GET
	@Path("changeRecommender")
	public Response changeRecommender(@QueryParam("id") final int id) {
		RecommenderSelector.getInstance().changeRecommender(id);

		return Response.ok().build();
	}
}
