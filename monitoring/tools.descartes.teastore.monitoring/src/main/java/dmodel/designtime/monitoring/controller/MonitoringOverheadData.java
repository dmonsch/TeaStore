package dmodel.designtime.monitoring.controller;

import java.util.HashMap;
import java.util.Map;

public class MonitoringOverheadData {
	private Map<String, Long> nanoOverheadMap;
	private long timestamp;
	private long endTimestamp;
	
	public MonitoringOverheadData() {
		this.nanoOverheadMap = new HashMap<>();
		this.timestamp = System.currentTimeMillis();
	}

	public Map<String, Long> getNanoOverheadMap() {
		return nanoOverheadMap;
	}

	public void setNanoOverheadMap(Map<String, Long> nanoOverheadMap) {
		this.nanoOverheadMap = nanoOverheadMap;
	}
	
	public void registerOverhead(String service, long overhead) {
		if (this.nanoOverheadMap.containsKey(service)) {
			this.nanoOverheadMap.put(service, this.nanoOverheadMap.get(service) + overhead);
		} else {
			this.nanoOverheadMap.put(service, overhead);
		}
	}
	
	public void clear() {
		this.nanoOverheadMap.clear();
		this.timestamp = System.currentTimeMillis();
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}
}
