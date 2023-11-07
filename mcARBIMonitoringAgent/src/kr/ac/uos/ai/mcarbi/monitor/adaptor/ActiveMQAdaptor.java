package kr.ac.uos.ai.mcarbi.monitor.adaptor;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.apache.activemq.transport.stomp.StompConnection;
import org.apache.activemq.transport.stomp.StompFrame;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import kr.ac.uos.ai.mcarbi.monitor.MonitoringAgent;
import kr.ac.uos.ai.mcarbi.monitor.message.ReceivedMessage;


public class ActiveMQAdaptor implements Adaptor{
	private MonitoringAgent				agent;
	private String agentID;
	private String monitorID = "activeMQMonitor";
	private String brokerAddress;
	private int brokerPort;
	private StompConnection connection;
	private MessageRecvTask messageRecvTask;
	private String receiver = "agent://www.arbi.com/interactionManager/message";
	
	public ActiveMQAdaptor(MonitoringAgent agent, String mcARBIagentName, String brokerAddress, int brokerPort) {
		connection = new StompConnection();
		this.brokerAddress = brokerAddress;
		this.brokerPort = brokerPort;
		this.agent = agent;
		this.agentID = mcARBIagentName;
	}

	@Override
	public void start() {
		try {
			connection.open(brokerAddress, brokerPort);
			connection.connect("monitor", "test");
			connection.subscribe(monitorID);
			messageRecvTask = new MessageRecvTask();
			messageRecvTask.start();
			
			activateLog();
			System.out.println("acriveMQ Monitor connected");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class MessageRecvTask extends Thread {
		@Override
		public void run() {
			while (true) {
				String message = "";
				try {
					StompFrame messageFrame;
					messageFrame = connection.receive();
					if(messageFrame != null) message = messageFrame.getBody();
					else continue;
				}
				catch(SocketTimeoutException e) {
					continue;
				}
				catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				if(message.isEmpty()) continue;
				onMessage(message);
			} 
		}
	}
	
	private void onMessage(String message) {
		System.out.println("[ACTIVEMQ MONITOR] received message : " + message);

		JSONParser jsonParser = new JSONParser();
		JSONObject messageObject;
		try {
			messageObject = (JSONObject) jsonParser.parse(message);
			String content = messageObject.get("Content").toString();
			
			Decoder decoder = Base64.getDecoder();
			String afterContent = new String(decoder.decode(content.getBytes()));
//			System.out.println("after content : " + afterContent);
			
			ReceivedMessage receivedMessage = new ReceivedMessage(agentID, messageObject.get("Action").toString(), afterContent);
	    	agent.enqueueMessage(receivedMessage);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void send(String msg) {
		try {
			connection.send(receiver, msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void activateLog() {
		JSONObject createMonitorMessage = new JSONObject();
		createMonitorMessage.put("Action", "create monitor");
		createMonitorMessage.put("ID", monitorID);
		createMonitorMessage.put("Protocol", "ActiveMQ");

		JSONArray filterArray = new JSONArray();
		
		JSONObject filter2 = new JSONObject();
		filter2.put("LogType", "MessageLog");
		filter2.put("Action", "AssertFact");
		filter2.put("Flag", true);
		filterArray.add(filter2);
		
		JSONObject filter4 = new JSONObject();
		filter4.put("LogType", "MessageLog");
		filter4.put("Action", "UpdateFact");
		filter4.put("Flag", true);
		filterArray.add(filter4);
		
		createMonitorMessage.put("Filter", filterArray);
		this.send(createMonitorMessage.toJSONString());
	}
	
	public static void main(String[] args) {
		ActiveMQAdaptor monitor = new ActiveMQAdaptor(null, "test", "127.0.0.1", 8888);
		monitor.start();
	}

}
