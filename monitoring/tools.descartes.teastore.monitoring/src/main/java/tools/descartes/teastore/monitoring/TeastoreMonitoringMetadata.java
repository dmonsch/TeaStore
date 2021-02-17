package tools.descartes.teastore.monitoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TeastoreMonitoringMetadata {

	/*
	 * --------------------------- Component: Web UI ---------------------------
	 */

	// Service WebUI.confirmOrder
	String SERVICE_WEBUI_CONFIRM_ORDER = "_xliLoDVXEeqPG_FgW3bi6Q";

	String EXTERNAL_CALL_WEBUI_LOADBALANCER_PLACE_ORDER = "__VQD0DbfEeq5L_FI-wfNWQ";

	// Service WebUI.updateOrder
	String SERVICE_WEBUI_UPDATE_ORDER = "_xlpgYDVXEeqPG_FgW3bi6Q";

	String EXTERNAL_CALL_WEBUI_LOADBALANCER_UPDATE_ORDER = "_CgFBEDbgEeq5L_FI-wfNWQ";
	String EXTERNAL_CALL_WEBUI_LOADBALANCER_TRAIN_RECOMMENDER = "_QQ_9gK10EeqcvaJ1A892dQ";
	String LOOP_WEBUI_UPDATE_ORDER_ITEM_ITER = "_EbUxQKpZEeqHXcsU55mirw";

	// Service WebUI.getRecommendations
	String SERVICE_WEBUI_GET_RECOMMENDATIONS = "_ybDeYDVXEeqPG_FgW3bi6Q";
	String EXTERNAL_CALL_WEBUI_LOADBALANCER_GET_RECOMMENDATIONS = "_6n8xwDbgEeq5L_FI-wfNWQ";

	/*
	 * --------------------------- Component: Registry ---------------------------
	 */

	// Service Registry.placeOrder
	String SERVICE_REGISTRY_PLACE_ORDER = "_jGYIgDVZEeqPG_FgW3bi6Q";
	String EXTERNAL_CALL_LOADBALANCER_AUTH_PLACE_ORDER = "_-iMAkDVdEeqPG_FgW3bi6Q";

	// Service Registry.updateOrder
	String SERVICE_REGISTRY_UPDATE_ORDER = "_jGZ9sDVZEeqPG_FgW3bi6Q";
	String EXTERNAL_CALL_LOADBALANCER_AUTH_UPDATE_ORDER = "_3PQJADVeEeqPG_FgW3bi6Q";

	// Service Registry.persistOrder
	String SERVICE_REGISTRY_PERSIST_ORDER = "_5a0osDVeEeqPG_FgW3bi6Q";
	String EXTERNAL_CALL_LOADBALANCER_PERSISTENCE_PERSIST_ORDER = "_8TRcgDVeEeqPG_FgW3bi6Q";

	// Service Registry.persistOrderItem
	String SERVICE_REGISTRY_PERSIST_ORDER_ITEM = "_6KaSkDVeEeqPG_FgW3bi6Q";
	String EXTERNAL_CALL_LOADBALANCER_PERSISTENCE_PERSIST_ORDER_ITEM = "_-xVaYDVeEeqPG_FgW3bi6Q";

	// Service Registry.recommendProducts
	String SERVICE_REGISTRY_RECOMMEND_PRODUCT = "_NV690DViEeqPG_FgW3bi6Q";
	String EXTERNAL_CALL_LOADBALANCER_RECOMMENDER_GET_RECOMMENDATIONS = "_P7sGMDViEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_REGISTRY_RECOMMEND_BUFFERING = "_lTsV0LRnEeqBG67dKda_bQ";

	// Service Registry.trainRecommender
	String SERVICE_REGISTRY_TRAIN_RECOMMENDER = "_mIuJEDViEeqPG_FgW3bi6Q";
	String EXTERNAL_CALL_LOADBALANCER_RECOMMENDER_TRAIN = "_noeroDViEeqPG_FgW3bi6Q";

	/*
	 * --------------------------- Component: Auth ---------------------------
	 */

	// Service Auth.placeOrder
	String SERVICE_AUTH_PLACE_ORDER = "_CetV8TVdEeqPG_FgW3bi6Q";

	String INTERNAL_ACTION_PREPROCESS_ORDER = "_EanpADVdEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_FINALIZE_ORDER = "_F5_KADVdEeqPG_FgW3bi6Q";

	String EXTERNAL_CALL_AUTH_LOADBALANCER_PERSIST_ORDER = "_N_TEQDVdEeqPG_FgW3bi6Q";
	String EXTERNAL_CALL_AUTH_LOADBALANCER_PERSIST_ORDER_ITEM = "_2dMPYDVdEeqPG_FgW3bi6Q";

	String LOOP_ORDER_ITEMS = "_P_ZTwDVdEeqPG_FgW3bi6Q";

	// Service Auth.updateOrder
	String SERVICE_AUTH_UPDATE_ORDER = "_Cet9AjVdEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_UPDATE_ORDER = "_fmO34DbgEeq5L_FI-wfNWQ";

	/*
	 * --------------------------- Component: Persistence
	 * ---------------------------
	 */

	// Service Persistence.createOrder
	String SERVICE_PERSISTENCE_CREATE_ORDER_ENTITY = "_Q7JfcDVfEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_PERSIST_ORDER = "_lYNwgDbTEeq5L_FI-wfNWQ";

	// Service Persistence.createOrderItem
	String SERVICE_PERSISTENCE_CREATE_ORDER_ITEM_ENTITY = "_Q7KtkDVfEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_PERSIST_ORDER_ITEM = "_tBq1MDbTEeq5L_FI-wfNWQ";

	/*
	 * --------------------------- Component: Recommender
	 * ---------------------------
	 */

	// Service Recommender.recommend
	String SERVICE_RECOMMENDER_RECOMMEND_PRODUCTS = "_nI0JkDVhEeqPG_FgW3bi6Q";
	String EXTERNAL_CALL_RECOMMENDER_STRATEGY_RECOMMEND = "_IuHq8DbQEeq5L_FI-wfNWQ";

	String SERVICE_RECOMMENDER_DUMMY_RECOMMEND = "_lSA1cjVgEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_RECOMMENDER_DUMMY_RECOMMEND = "_uWLeIDbgEeq5L_FI-wfNWQ";
	String SERVICE_RECOMMENDER_DUMMY_TRAIN = "_qMytYDVgEeqPG_FgW3bi6Q";

	String SERVICE_RECOMMENDER_ORDER_BASED_RECOMMEND = "_l7sEkzVgEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_RECOMMENDER_ORDER_BASED_RECOMMEND = "_rq4oIK1mEeqcvaJ1A892dQ";

	String SERVICE_RECOMMENDER_SLOPE_ONE_RECOMMEND = "_lSA1cjVgEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_RECOMMENDER_SLOPE_ONE_RECOMMEND = "_I-vuUK1oEeqcvaJ1A892dQ";

	String SERVICE_RECOMMENDER_SLOPE_ONE_PREPROC_RECOMMEND = "_kob6gapwEeqHXcsU55mirw";
	String INTERNAL_ACTION_RECOMMENDER_SLOPE_ONE_PREPROC_RECOMMEND = "_e9gEoK1oEeqcvaJ1A892dQ";

	String SERVICE_RECOMMENDER_POPULARITY_RECOMMEND = "_vHbbYDVgEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_RECOMMENDER_POPULARITY_RECOMMEND = "__RGH0K1oEeqcvaJ1A892dQ";

	// Service Recommender.train
	String SERVICE_RECOMMENDER_TRAIN_RECOMMENDER = "_QrkiUDbTEeq5L_FI-wfNWQ";

	String INTERNAL_ACTION_RECOMMENDER_PREPARE_TRAIN = "_npGbYPdtEeqejst3nueXaA";

	String INTERNAL_ACTION_RECOMMENDER_TRAIN_GET_ORDERS = "_Gq_WcK19EeqcvaJ1A892dQ";
	String INTERNAL_ACTION_RECOMMENDER_TRAIN_GET_ORDERITEMS = "_aE-B0Dl1EeuUKoA9UkUjrQ";

	String EXTERNAL_CALL_RECOMMENDER_STRATEGY_TRAIN = "_SffXADbTEeq5L_FI-wfNWQ";

	String SERVICE_RECOMMENDER_POPULARITY_TRAIN = "_vHaNQDVgEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_RECOMMENDER_POPULARITY_TRAIN = "_axPdsGMREeuCi-18uA4cnA";

	String SERVICE_RECOMMENDER_ORDER_BASED_TRAIN = "_l7sEkDVgEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_RECOMMENDER_ORDER_BASED_TRAIN = "_yqQ1EK1mEeqcvaJ1A892dQ";

	String SERVICE_RECOMMENDER_SLOPE_ONE_TRAIN = "_lR_nUDVgEeqPG_FgW3bi6Q";
	String INTERNAL_ACTION_RECOMMENDER_SLOPE_ONE_TRAIN = "_o_OcwLQBEeq7laWsYbb_3A";

	String SERVICE_RECOMMENDER_SLOPE_ONE_PREPROC_TRAIN = "_kobTcKpwEeqHXcsU55mirw";
	String INTERNAL_ACTION_RECOMMENDER_SLOPE_ONE_PREPROC_TRAIN = "_hnJlkGMREeuCi-18uA4cnA";

	/*
	 * --------------------------- REST Overhead ---------------------------
	 */

	String INTERNAL_ACTION_REGISTRY_RECOMMENDER_RECOMMEND_REST = "__EMrkLTMEeqYI7n2jTg1Ag";
	String INTERNAL_ACTION_REGISTRY_PERSISTENCE_PERSIST_ORDER_REST = "_bbWZ0LU6Eeq29rcBS3Ut9g";
	String INTERNAL_ACTION_REGISTRY_AUTH_UPDATE_ORDER_REST = "_p_VjAPhqEeqSKeH-EIO_1w";
	String INTERNAL_ACTION_REGISTRY_PERSISTENCE_PERSIST_ORDER_ITEM_REST = "_BxGmIEFIEeuG-5cAK6lylg";

	/*
	 * --------------------------- Additionals ---------------------------
	 */
	// Global Resources
	String RESOURCE_CPU = "_oro4gG3fEdy4YaaT-RYrLQ";

	/*
	 * --------------------------- Replication Mappings ---------------------------
	 */
	public Map<String, String[][]> authReplicationMappings = new HashMap<String, String[][]>() {
		/**
		 * Generated UID.
		 */
		private static final long serialVersionUID = 7102006032038274173L;

		{
			put("_3PQJADVeEeqPG_FgW3bi6Q", new String[][] { new String[] { "_wps-zmMREeu1YpggLYVT6g", null, null },
					new String[] { "_wptl12MREeu1YpggLYVT6g", "_wptl3GMREeu1YpggLYVT6g", null },
					new String[] { "_wptl5mMREeu1YpggLYVT6g", "_wptl62MREeu1YpggLYVT6g", "_wptl8GMREeu1YpggLYVT6g" } });
			put("_-iMAkDVdEeqPG_FgW3bi6Q", new String[][] { new String[] { "_wpsXsmMREeu1YpggLYVT6g", null, null },
					new String[] { "_wpsXvGMREeu1YpggLYVT6g", "_wpsXwWMREeu1YpggLYVT6g", null },
					new String[] { "_wpsXy2MREeu1YpggLYVT6g", "_wpsX0GMREeu1YpggLYVT6g", "_wps-w2MREeu1YpggLYVT6g" } });
		}
	};

	public Map<String, String[][]> persistenceReplicationMapping = new HashMap<String, String[][]>() {
		/**
		 * Generated UID.
		 */
		private static final long serialVersionUID = -8318539321595331605L;

		{
			put("_-xVaYDVeEeqPG_FgW3bi6Q", new String[][] { new String[] { "_wp6aKGMREeu1YpggLYVT6g", null, null },
					new String[] { "_wp7oRGMREeu1YpggLYVT6g", "_wp7oSWMREeu1YpggLYVT6g", null },
					new String[] { "_wp7oU2MREeu1YpggLYVT6g", "_wp7oWGMREeu1YpggLYVT6g", "_wp7oXWMREeu1YpggLYVT6g" } });
			put("_8TRcgDVeEeqPG_FgW3bi6Q", new String[][] { new String[] { "_wp5zGmMREeu1YpggLYVT6g", null, null },
					new String[] { "_wp5zJGMREeu1YpggLYVT6g", "_wp5zKWMREeu1YpggLYVT6g", null },
					new String[] { "_wp5zM2MREeu1YpggLYVT6g", "_wp5zOGMREeu1YpggLYVT6g", "_wp5zPWMREeu1YpggLYVT6g" } });
		}
	};

	/*
	 * --------------------------- Help Methods ---------------------------
	 */
	public static String selectCorrespondingExternalId(Map<String, String[][]> replicationMappings,
			String externalActionID, int maxReplicas, int instanceId) {
		if (replicationMappings.containsKey(externalActionID)) {
			String result = replicationMappings.get(externalActionID)[maxReplicas - 1][instanceId];
			if (result != null) {
				return result;
			}
		}
		System.out.println("FAILED TO RESOLVE EXTERNAL ACTION ID BASED ON REPLICAS!");
		return null;
	}

}
