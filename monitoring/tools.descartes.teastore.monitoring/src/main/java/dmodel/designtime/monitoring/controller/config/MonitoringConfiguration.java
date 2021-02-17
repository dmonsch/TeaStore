package dmodel.designtime.monitoring.controller.config;

public interface MonitoringConfiguration {

	String SERVER_HOSTNAME = "dmodel";
	int SERVER_REST_PORT = 8080;
	String SERVER_REST_INM_URL = "/runtime/pipeline/imm";
	String SERVER_REST_OVERHEAD_URL = "/runtime/pipeline/overhead";

	boolean LOGARITHMIC_SCALING = false;
	long LOGARITHMIC_SCALING_INTERVAL = 1000; // recovery interval in ms
	long OVERHEAD_REPORT_INTERVAL = 1;

}
