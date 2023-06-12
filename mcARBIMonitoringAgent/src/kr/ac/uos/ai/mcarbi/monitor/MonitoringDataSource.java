package kr.ac.uos.ai.mcarbi.monitor;

import kr.ac.uos.ai.arbi.ltm.DataSource;

public class MonitoringDataSource extends DataSource{

	
	private MonitoringAgent						agent;
	public MonitoringDataSource(MonitoringAgent monitorAgent) {
		this.agent = monitorAgent;
	}

	
	@Override
	public void onNotify(String content) {
		agent.onNotify("LTM", content);
	}
}
