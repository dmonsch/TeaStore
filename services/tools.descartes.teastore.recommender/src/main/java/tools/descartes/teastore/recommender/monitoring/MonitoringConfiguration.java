package tools.descartes.teastore.recommender.monitoring;

/**
 * Configuration class for the monitoring.
 */
public class MonitoringConfiguration {
	/**
	 * 1 = single recommender implementation 2 = all recommender implementations 3 =
	 * evolution scenario
	 */
	public static int EVOLUTION_SCENARIO = 3;
	public static String OUTPATH = "/Users/David/monitoring-teastore-evolution/";
	public static boolean FINE_GRANULAR_INIT = false;
	
	public static boolean EVOLUTION_RECOGNIZED = EVOLUTION_SCENARIO == 3;
	public static boolean EVOLUTION_CLEAR_DB_AFTER_COMMIT = true;

}
