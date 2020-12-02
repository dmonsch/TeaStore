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

package tools.descartes.teastore.auth.rest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import dmodel.designtime.monitoring.controller.ServiceParameters;
import dmodel.designtime.monitoring.controller.ThreadMonitoringController;
import tools.descartes.teastore.auth.security.BCryptProvider;
import tools.descartes.teastore.auth.security.RandomSessionIdGenerator;
import tools.descartes.teastore.auth.security.ShaSecurityProvider;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.User;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.monitoring.TeastoreMonitoringMetadata;
import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.rest.LoadBalancedCRUDOperations;
import tools.descartes.teastore.registryclient.util.NotFoundException;
import tools.descartes.teastore.registryclient.util.TimeoutException;

/**
 * Rest endpoint for the store user actions.
 * 
 * @author Simon
 */
@Path("useractions")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class AuthUserActionsRest {

	/**
	 * Persists order in database.
	 * 
	 * @param blob                 SessionBlob
	 * @param totalPriceInCents    totalPrice
	 * @param addressName          address
	 * @param address1             address
	 * @param address2             address
	 * @param creditCardCompany    creditcard
	 * @param creditCardNumber     creditcard
	 * @param creditCardExpiryDate creditcard
	 * @return Response containing SessionBlob
	 */
	@POST
	@Path("placeorder")
	public Response placeOrder(SessionBlob blob, @QueryParam("totalPriceInCents") long totalPriceInCents,
			@QueryParam("addressName") String addressName, @QueryParam("address1") String address1,
			@QueryParam("address2") String address2, @QueryParam("creditCardCompany") String creditCardCompany,
			@QueryParam("creditCardNumber") String creditCardNumber,
			@QueryParam("creditCardExpiryDate") String creditCardExpiryDate,
			@QueryParam("monitoringTraceId") String monitoringTraceId,
			@QueryParam("monitoringExternalId") String monitoringExternalId,
			@QueryParam("startTime") long startTime) {
		
		ThreadMonitoringController.getInstance().continueFromRemote(monitoringTraceId, monitoringExternalId);
		ThreadMonitoringController.getInstance().logInternalAction(
				TeastoreMonitoringMetadata.INTERNAL_ACTION_REGISTRY_AUTH_UPDATE_ORDER_REST,
				TeastoreMonitoringMetadata.RESOURCE_CPU, startTime);
		
		// fix for session modification
		new ShaSecurityProvider().secure(blob);
		if (new ShaSecurityProvider().validate(blob) == null || blob.getOrderItems().isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		ThreadMonitoringController.setSessionId(blob.getSID());

		ServiceParameters parameters = new ServiceParameters();
		parameters.addValue("items.VALUE", blob.getOrderItems().size());
		ThreadMonitoringController.getInstance().enterService(TeastoreMonitoringMetadata.SERVICE_AUTH_PLACE_ORDER, this,
				parameters);

		try {
			ThreadMonitoringController.getInstance().enterInternalAction(
					TeastoreMonitoringMetadata.INTERNAL_ACTION_PREPROCESS_ORDER,
					TeastoreMonitoringMetadata.RESOURCE_CPU);
			blob.getOrder().setUserId(blob.getUID());
			blob.getOrder().setTotalPriceInCents(totalPriceInCents);
			blob.getOrder().setAddressName(addressName);
			blob.getOrder().setAddress1(address1);
			blob.getOrder().setAddress2(address2);
			blob.getOrder().setCreditCardCompany(creditCardCompany);
			blob.getOrder().setCreditCardExpiryDate(creditCardExpiryDate);
			blob.getOrder().setCreditCardNumber(creditCardNumber);
			blob.getOrder().setTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			ThreadMonitoringController.getInstance().exitInternalAction(
					TeastoreMonitoringMetadata.INTERNAL_ACTION_PREPROCESS_ORDER,
					TeastoreMonitoringMetadata.RESOURCE_CPU);

			long orderId;
			try {
				ThreadMonitoringController.getInstance().setExternalCallId(TeastoreMonitoringMetadata.EXTERNAL_CALL_AUTH_LOADBALANCER_PERSIST_ORDER);
				orderId = LoadBalancedCRUDOperations.sendEntityForCreation(Service.PERSISTENCE, "orders", Order.class,
						blob.getOrder());
			} catch (LoadBalancerTimeoutException e) {
				return Response.status(408).build();
			} catch (NotFoundException e) {
				return Response.status(404).build();
			}
			
			long its = 0;
			for (OrderItem item : blob.getOrderItems()) {
				its++;
				try {
					item.setOrderId(orderId);
					ThreadMonitoringController.getInstance().setExternalCallId(TeastoreMonitoringMetadata.EXTERNAL_CALL_AUTH_LOADBALANCER_PERSIST_ORDER_ITEM);
					LoadBalancedCRUDOperations.sendEntityForCreation(Service.PERSISTENCE, "orderitems", OrderItem.class,
							item);
				} catch (TimeoutException e) {
					return Response.status(408).build();
				} catch (NotFoundException e) {
					return Response.status(404).build();
				}
			}
			ThreadMonitoringController.getInstance().exitLoop(TeastoreMonitoringMetadata.LOOP_ORDER_ITEMS, its);

			ThreadMonitoringController.getInstance().enterInternalAction(
					TeastoreMonitoringMetadata.INTERNAL_ACTION_FINALIZE_ORDER, TeastoreMonitoringMetadata.RESOURCE_CPU);
			blob.setOrder(new Order());
			blob.getOrderItems().clear();
			blob = new ShaSecurityProvider().secure(blob);
			ThreadMonitoringController.getInstance().exitInternalAction(
					TeastoreMonitoringMetadata.INTERNAL_ACTION_FINALIZE_ORDER, TeastoreMonitoringMetadata.RESOURCE_CPU);

			return Response.status(Response.Status.OK).entity(blob).build();
		} finally {
			ThreadMonitoringController.getInstance().exitService(TeastoreMonitoringMetadata.SERVICE_AUTH_PLACE_ORDER);
			ThreadMonitoringController.getInstance().detachFromRemote();
		}
	}

	/**
	 * User login.
	 * 
	 * @param blob     SessionBlob
	 * @param name     Username
	 * @param password password
	 * @return Response with SessionBlob containing login information.
	 */
	@POST
	@Path("login")
	public Response login(SessionBlob blob, @QueryParam("name") String name, @QueryParam("password") String password) {
		User user;
		try {
			user = LoadBalancedCRUDOperations.getEntityWithProperties(Service.PERSISTENCE, "users", User.class, "name",
					name);
		} catch (TimeoutException e) {
			return Response.status(408).build();
		} catch (NotFoundException e) {
			return Response.status(Response.Status.OK).entity(blob).build();
		}

		if (user != null && BCryptProvider.checkPassword(password, user.getPassword())) {
			blob.setUID(user.getId());
			blob.setSID(new RandomSessionIdGenerator().getSessionId());
			blob = new ShaSecurityProvider().secure(blob);
			return Response.status(Response.Status.OK).entity(blob).build();
		}
		return Response.status(Response.Status.OK).entity(blob).build();
	}

	/**
	 * User logout.
	 * 
	 * @param blob SessionBlob
	 * @return Response with SessionBlob
	 */
	@POST
	@Path("logout")
	public Response logout(SessionBlob blob) {
		blob.setUID(null);
		blob.setSID(null);
		blob.setOrder(new Order());
		blob.getOrderItems().clear();
		return Response.status(Response.Status.OK).entity(blob).build();
	}

	/**
	 * Checks if user is logged in.
	 * 
	 * @param blob Sessionblob
	 * @return Response with true if logged in
	 */
	@POST
	@Path("isloggedin")
	public Response isLoggedIn(SessionBlob blob) {
		return Response.status(Response.Status.OK).entity(new ShaSecurityProvider().validate(blob)).build();
	}

}
