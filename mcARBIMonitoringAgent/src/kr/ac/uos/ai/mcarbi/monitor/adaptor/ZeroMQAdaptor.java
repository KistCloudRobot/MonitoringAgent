package kr.ac.uos.ai.mcarbi.monitor.adaptor;

import java.util.Base64;
import java.util.Base64.Decoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import kr.ac.uos.ai.mcarbi.monitor.MonitoringAgent;
import test.interactionManager.TestZeroMQMonitor; 

public class ZeroMQAdaptor implements Adaptor{
	private Context 					zmqContext;
	private Socket 						zmqSocket;
	private MonitoringAgent				agent;
	private MessageRecvTask 			messageRecvTask;
	private String						mcARBIAgentName;
	
	public ZeroMQAdaptor(MonitoringAgent agent, String mcARBIagentName, String ip, int port) {
		zmqContext = ZMQ.context(1);
		zmqSocket = zmqContext.socket(SocketType.DEALER);
		String brokerURL = "tcp://" + ip + ":" + port;
		zmqSocket.connect(brokerURL);
//		zmqSocket.setIdentity(agent.getAgentID().getBytes());
		zmqSocket.setIdentity("test".getBytes());
		messageRecvTask = new MessageRecvTask();
		
		this.mcARBIAgentName = mcARBIagentName;
		this.agent = agent;

		activateLog();
	}
	
	private class MessageRecvTask extends Thread {
		@Override
		public void run() {
			while (true) {
//				System.out.println("message receiving");
				String message = "";
				while(message.length() == 0) {
					message = zmqSocket.recvStr();
				}
				System.out.println("on message : " + message);

				try {
					JSONParser jsonParser = new JSONParser();
					JSONObject messageObject = (JSONObject) jsonParser.parse(message);
					String content = messageObject.get("Content").toString();
//					System.out.println("before content : " + content);
					
					Decoder decoder = Base64.getDecoder();
					String afterContent = new String(decoder.decode(content.getBytes()));
					System.out.println("after content : " + afterContent);
					
                	System.out.println("Message: " + content);

                	agent.onData(mcARBIAgentName, afterContent);
				
//				try {
//					JSONParser jsonParser = new JSONParser();
//					JSONObject messageObject = (JSONObject) jsonParser.parse(message);
//					String content = messageObject.get("Content").toString();
//					System.out.println("before content : " + content);
//					
//					Decoder decoder = Base64.getDecoder();
//					content = new String(decoder.decode(content.getBytes()));
//					System.out.println("after content : " + content);
//					
//					
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("parsing error");
				}
			} 
		}
		
	}
	
	private void activateLog() {
		JSONObject createMonitorMessage = new JSONObject();
		createMonitorMessage.put("Action", "create monitor");
//		String id = agent.getAgentID();
//		System.out.println(id);
//		createMonitorMessage.put("ID", id);
		createMonitorMessage.put("ID", "test");
		createMonitorMessage.put("Protocol", "ZeroMQ");

		JSONArray filterArray = new JSONArray();
		
//		JSONObject filter1 = new JSONObject();
//		filter1.put("LogType", "MessageLog");
//		filter1.put("Action", "Inform");
//		filter1.put("Flag", true);
//		filterArray.add(filter1);
//		
//		JSONObject filter2 = new JSONObject();
//		filter2.put("LogType", "MessageLog");
//		filter2.put("Action", "AssertFact");
//		filter2.put("Flag", true);
//		filterArray.add(filter2);
		
		JSONObject filter3 = new JSONObject();
		filter3.put("LogType", "SystemLog");
		filter3.put("Actor", "agent://www.arbi.com/TaskManager");
		filter3.put("Action", "IntendGoal");
		filter3.put("Flag", true);
		filterArray.add(filter3);
		
		JSONObject filter4 = new JSONObject();
		filter4.put("LogType", "MessageLog");
		filter4.put("Action", "UpdateFact");
		filter4.put("Flag", true);
		filterArray.add(filter4);
		
		
		JSONObject filter5 = new JSONObject();
		filter5.put("LogType", "SystemLog");
		filter5.put("Actor", "agent://www.arbi.com/TaskManager");
		filter5.put("Action", "UnpostGoal");
		filter5.put("Flag", true);
		filterArray.add(filter5);
		createMonitorMessage.put("Filter", filterArray);
		
		zmqSocket.sendMore("");
		zmqSocket.send(createMonitorMessage.toJSONString());
	}

	public void send(String message) {
		zmqSocket.sendMore("");
		zmqSocket.send(message);
	}
	public static void main(String[] args) {
		ZeroMQAdaptor monitor = new ZeroMQAdaptor(null, "test", "172.16.165.185", 51116);
		monitor.start();
	}
	@Override
	public void start() {
//		System.out.println("start monitoring");
		messageRecvTask.start();
//		System.out.println("started monitoring");
	}
	
}
