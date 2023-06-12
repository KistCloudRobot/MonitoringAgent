package kr.ac.uos.ai.mcarbi.monitor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

	private final String MY_AGENT_ADDRESS = 					"agent://www.arbi.com/TaskManager";	
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
		ArbiAgentExecutor.execute(brokerAddress, brokerPort, MY_AGENT_ADDRESS, this,BrokerType.ACTIVEMQ);
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
				if(message.getMessage().startsWith("(")) {
					GeneralizedList gl = null;
					String data = message.getMessage();
					String sender = message.getSender();

					gl = GLFactory.newGLFromGLString(data);

					messageHandler.assertGL(data);

				} else if (message.getMessage().startsWith("{")) {
					JSONObject json = null;
					String data = message.getMessage();
					String sender = message.getSender();
					json = (JSONObject) new JSONParser().parse(data);
					
					messageHandler.assertJSON(json);
				}

			} catch (InterruptedException | ParseException | org.json.simple.parser.ParseException e) {
				e.printStackTrace();
			}

			return true;
		}
	}
	
	public void connectToMcARBIAgent(String agentName, String ip, int port, String brokerType) {
		Adaptor adaptor = null;
		if (brokerType.equals("ZeroMQ")) {
			adaptor = new ZeroMQAdaptor(this, agentName, ip, brokerPort);
			adaptor.start();
			messageHandler.assertFact("AgentAdaptor", agentName ,adaptor);
		}
	}
	
	
	public String getAgentID() {
		return MY_AGENT_ADDRESS;
	}
}
