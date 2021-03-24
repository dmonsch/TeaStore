package tools.descartes.teastore.registryclient.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cipm.consistency.bridge.monitoring.controller.ServiceParameters;
import cipm.consistency.bridge.monitoring.controller.ThreadMonitoringController;
import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.teastore.registryclient.util.NotFoundException;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.monitoring.TeastoreMonitoringMetadata;

/**
 * Container class for the static calls to the Store service.
 * 
 * @author Simon
 *
 */
public final class LoadBalancedRecommenderOperations {

	private LoadBalancedRecommenderOperations() {
	}

	/**
	 * Gets recommendations.
	 * 
	 * @param order list of order items
	 * @param uid   userId
	 * @throws NotFoundException            If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code and on
	 *                                      repeated load balancer socket timeouts.
	 * @return List of recommended order ids
	 */
	public static List<Long> getRecommendations(List<OrderItem> order, Long uid)
			throws NotFoundException, LoadBalancerTimeoutException {

		ServiceParameters parameters = new ServiceParameters();
		parameters.addValue("items.VALUE", order.size());
		ThreadMonitoringController.getInstance().enterService(
				TeastoreMonitoringMetadata.SERVICE_REGISTRY_RECOMMEND_PRODUCT, LoadBalancedRecommenderOperations.class,
				parameters, ServiceLoadBalancer.getRegistryHostID(), ServiceLoadBalancer.getRegistryHostName());

		try {
			Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.RECOMMENDER, "recommend", Category.class,
					(id, client) -> ResponseWrapper.wrap(HttpWrapper
							.wrap(client.getEndpointTarget().queryParam("uid", uid)
									.queryParam(
											"monitoringTraceId",
											ThreadMonitoringController.getInstance().getCurrentTraceId())
									.queryParam("startTime", System.currentTimeMillis())
									.queryParam("monitoringExternalId",
											TeastoreMonitoringMetadata.EXTERNAL_CALL_LOADBALANCER_RECOMMENDER_GET_RECOMMENDATIONS))
							.post(Entity.entity(order, MediaType.APPLICATION_JSON))));

			ThreadMonitoringController.getInstance().enterInternalAction(
					TeastoreMonitoringMetadata.INTERNAL_ACTION_REGISTRY_RECOMMEND_BUFFERING,
					TeastoreMonitoringMetadata.RESOURCE_CPU);
			if (r != null) {
				if (r.getStatus() < 400) {
					return r.readEntity(new GenericType<List<Long>>() {
					});
				} else {
					r.bufferEntity();
				}
			}

			return new ArrayList<>();
		} finally {
			ThreadMonitoringController.getInstance().exitInternalAction(
					TeastoreMonitoringMetadata.INTERNAL_ACTION_REGISTRY_RECOMMEND_BUFFERING,
					TeastoreMonitoringMetadata.RESOURCE_CPU);

			ThreadMonitoringController.getInstance()
					.exitService(TeastoreMonitoringMetadata.SERVICE_REGISTRY_RECOMMEND_PRODUCT);
		}
	}

	public static void trainRecommender(long noOrders, long noItems) {
		ServiceParameters parameters = new ServiceParameters();
		parameters.addValue("orders", noOrders);
		parameters.addValue("orderItems", noItems);

		ThreadMonitoringController.getInstance().enterService(
				TeastoreMonitoringMetadata.SERVICE_REGISTRY_TRAIN_RECOMMENDER, LoadBalancedRecommenderOperations.class,
				parameters, ServiceLoadBalancer.getRegistryHostID(), ServiceLoadBalancer.getRegistryHostName());

		try {
			ServiceLoadBalancer.loadBalanceRESTOperation(Service.RECOMMENDER, "train", String.class,
					(id, client) -> ResponseWrapper.wrap(HttpWrapper
							.wrap(client.getEndpointTarget()
									.queryParam("monitoringTraceId",
											ThreadMonitoringController.getInstance().getCurrentTraceId())
									.queryParam("orders", noOrders).queryParam("orderItems", noItems)
									.queryParam("monitoringExternalId",
											TeastoreMonitoringMetadata.EXTERNAL_CALL_LOADBALANCER_RECOMMENDER_TRAIN))
							.get()));
		} finally {
			ThreadMonitoringController.getInstance()
					.exitService(TeastoreMonitoringMetadata.SERVICE_REGISTRY_TRAIN_RECOMMENDER);
		}
	}
}
