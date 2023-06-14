package kr.ac.uos.ai.mcarbi.monitor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.ac.uos.ai.arbi.BrokerType;
import kr.ac.uos.ai.arbi.agent.ArbiAgent;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;
import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import kr.ac.uos.ai.mcarbi.monitor.adaptor.Adaptor;
import kr.ac.uos.ai.mcarbi.monitor.adaptor.ZeroMQAdaptor;
import kr.ac.uos.ai.mcarbi.monitor.log.MonitorLogger;
import kr.ac.uos.ai.mcarbi.monitor.message.MessageHandler;
import kr.ac.uos.ai.mcarbi.monitor.message.ReceivedMessage;
import kr.ac.uos.ai.mcarbi.monitor.utility.JAMPlanLoader;
import kr.ac.uos.ai.mcarbi.monitor.utility.TaskTracker;
import uos.ai.jam.Interpreter;
import uos.ai.jam.JAM;

public class MonitoringAgent extends ArbiAgent {

	private Interpreter 										interpreter;
	private BlockingQueue<ReceivedMessage> 						messageQueue;
	private MessageHandler										messageHandler;
	private MonitorLogger 										logger;
	private JAMPlanLoader										planLoader;
	private MonitoringDataSource								dataSource;
	private TaskTracker											taskTracker;
	
	private String 												brokerAddress;
	private int 												brokerPort;
	private BrokerType brokerType =								BrokerType.ACTIVEMQ;

	private final String MY_AGENT_ID = 							"agent://www.mcarbi.com/MonitoringManager";
	private final String MY_ADDRESS = 							"agent://www.arbi.com/TaskManager";	
	private final String MY_DATASOURCE_PREFIX = 				"ds://www.arbi.com/TaskManager";


	public MonitoringAgent(String brokerAddress, int brokerPort) {
		this.brokerAddress = brokerAddress;
		this.brokerPort = brokerPort;
		
		messageQueue = new LinkedBlockingQueue<ReceivedMessage>();
		dataSource = new MonitoringDataSource(this);
		
		interpreter = JAM.parse(new String[] { "./plan/boot.jam" });
		planLoader = new JAMPlanLoader(interpreter);
		messageHandler = new MessageHandler(interpreter);
		
		init();
	}
	
	
	
	private void init() {
		System.out.println("before execute");
		ArbiAgentExecutor.execute(brokerAddress, brokerPort, MY_ADDRESS, this, BrokerType.ACTIVEMQ);
		System.out.println("after execute");
		dataSource.connect(brokerAddress, brokerPort, MY_DATASOURCE_PREFIX, BrokerType.ACTIVEMQ);
		
		messageHandler.assertFact("MessageHandler", messageHandler);
		messageHandler.assertFact("PlanLoader", planLoader);
		messageHandler.assertFact("TaskTracker", taskTracker);
		messageHandler.assertFact("TaskManager", this);
		
		logger = new MonitorLogger(this,interpreter);

		Thread t = new Thread() {
			public void run() {
				interpreter.run();
			}
		};
		
		t.start();
	}
	
	public boolean dequeueMessage() {
		if (messageQueue.isEmpty())
			return false;
		else {
			try {
				ReceivedMessage message = messageQueue.take();
				System.out.println("[SENDER]  : " + message.getSender());
				System.out.println("[MESSAGE] : " + message.getMessage());
				
				
//				if(message.getMessage().startsWith("(")) {
//					GeneralizedList gl = null;
//					String data = message.getMessage();
//					String sender = message.getSender();
//
//					gl = GLFactory.newGLFromGLString(data);
//
//					messageHandler.assertGL(data);
//
//				} else if (message.getMessage().startsWith("{")) {
//					JSONObject json = null;
//					String data = message.getMessage();
//					String sender = message.getSender();
//					json = (JSONObject) new JSONParser().parse(data);
//					
//					messageHandler.assertJSON(json);
//				}
//
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			catch (ParseException | org.json.simple.parser.ParseException e) {
//				e.printStackTrace();
//			}

			return true;
		}
	}
	
	public void connectToMcARBIAgent(String agentName, String ip, int port, String brokerType) {
		Adaptor adaptor = null;
		if (brokerType.equals("ZeroMQ")) {
			adaptor = new ZeroMQAdaptor(this, agentName, ip, brokerPort);
			adaptor.start();
			
			activateLog(adaptor);
			messageHandler.assertFact("AgentAdaptor", agentName ,adaptor);
		}
	}
	
	private void activateLog(Adaptor adaptor) {
		JSONObject createFilterMessage = new JSONObject();
		createFilterMessage.put("Action", "create monitor");
		createFilterMessage.put("ID", MY_AGENT_ID);
		createFilterMessage.put("Protocol", "ZeroMQ");

		JSONArray filterArray = new JSONArray();
		
		JSONObject filter5 = new JSONObject();
		filter5.put("LogType", "SystemLog");
		filter5.put("Action", "UnpostGoal");
		filter5.put("Flag", true);
		filterArray.add(filter5);		
		
		JSONObject filter6 = new JSONObject();
		filter6.put("LogType", "SystemLog");
		filter6.put("Action", "IntendGoal");
		filter6.put("Flag", true);
		filterArray.add(filter6);		
		
		JSONObject filter7 = new JSONObject();
		filter7.put("LogType", "MessageLog");
		filter7.put("Action", "UpdateFact");
		filter7.put("Flag", true);
		filterArray.add(filter7);	
		
		createFilterMessage.put("Filter", filterArray);
		adaptor.send(createFilterMessage.toJSONString());
	}
	
	public String getAgentID() {
		return MY_ADDRESS;
	}
}
