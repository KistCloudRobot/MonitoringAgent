package kr.ac.uos.ai.mcarbi.monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.uos.ai.agentCommunicationFramework.agent.AgentExecutor;
import kr.ac.uos.ai.arbi.BrokerType;
import kr.ac.uos.ai.arbi.agent.ArbiAgent;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;
import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import kr.ac.uos.ai.mcarbi.monitor.adaptor.ActiveMQAdaptor;
import kr.ac.uos.ai.mcarbi.monitor.adaptor.Adaptor;
import kr.ac.uos.ai.mcarbi.monitor.adaptor.SocketAdaptor;
import kr.ac.uos.ai.mcarbi.monitor.adaptor.ZeroMQAdaptor;
import kr.ac.uos.ai.mcarbi.monitor.log.MonitorLogger;
import kr.ac.uos.ai.mcarbi.monitor.message.GeneralizedListHandler;
import kr.ac.uos.ai.mcarbi.monitor.message.ReceivedMessage;
import kr.ac.uos.ai.mcarbi.monitor.utility.JAMPlanLoader;
import kr.ac.uos.ai.mcarbi.monitor.utility.JarLoader;
import kr.ac.uos.ai.mcarbi.monitor.utility.WorldModelHandler;
import kr.ac.uos.ai.mcarbi.monitor.utility.demo.AgentWorkflow;
import kr.ac.uos.ai.mcarbi.monitor.utility.demo.TaskTracker;
import uos.ai.jam.Interpreter;
import uos.ai.jam.JAM;

public class MonitoringAgent extends ArbiAgent {

	private Interpreter 										interpreter;
	private BlockingQueue<ReceivedMessage> 						messageQueue;
	private WorldModelHandler									worldModelHandler;
	private GeneralizedListHandler								glHandler;
	private MonitorLogger 										logger;
	private JAMPlanLoader										planLoader;
	private JarLoader											jarLoader;
	private MonitoringDataSource								dataSource;
	private MultiAgentCommunicator 								agentCommunicator;
	
	
	private TaskTracker											taskTracker;
	private Map<String, AgentWorkflow>							workflowMap;
	
	private String 												brokerAddress;
	private int 												brokerPort;
	private BrokerType brokerType =								BrokerType.ACTIVEMQ;

	private final String MY_AGENT_ID = 							"agent://www.mcarbi.com/MonitoringManager";
	private final String SEMANTICMAP_ID = 						"agent://www.mcarbi.com/SemanticMapManager";
	private final String MY_ADDRESS = 							"agent://www.arbi.com/TaskManager";	
	private final String MY_DATASOURCE_PREFIX = 				"ds://www.arbi.com/TaskManager";


	public MonitoringAgent(String env, String brokerAddress, int brokerPort) {
		this.brokerAddress = brokerAddress;
		this.brokerPort = brokerPort;
		
		messageQueue = new LinkedBlockingQueue<ReceivedMessage>();
		dataSource = new MonitoringDataSource(this);
		taskTracker = new TaskTracker();
		jarLoader = new JarLoader();
		
		interpreter = JAM.parse(new String[] { "./plan/boot.jam" });
		planLoader = new JAMPlanLoader(interpreter);
		worldModelHandler = new WorldModelHandler(interpreter);
		glHandler = new GeneralizedListHandler();
		
		//for Demo
		generateWorkflowMap();
		
		init(env);
	}
	
	private void generateWorkflowMap() {
		workflowMap = new HashMap<>();
		workflowMap.put("AMR_LIFT1", new AgentWorkflow("AMR_LIFT1"));
		workflowMap.put("AMR_LIFT2", new AgentWorkflow("AMR_LIFT2"));
		workflowMap.put("AMR_LIFT3", new AgentWorkflow("AMR_LIFT3"));
		workflowMap.put("AMR_LIFT4", new AgentWorkflow("AMR_LIFT4"));
		workflowMap.put("AMR_LIFT5", new AgentWorkflow("AMR_LIFT5"));
		workflowMap.put("AMR_LIFT6", new AgentWorkflow("AMR_LIFT6"));
		workflowMap.put("AMR_LIFT7", new AgentWorkflow("AMR_LIFT7"));
		workflowMap.put("Epson", new AgentWorkflow("Epson"));
		workflowMap.put("UR", new AgentWorkflow("UR"));
	}
	
	private void init(String env) {
		if(env.equals("Logistic")) {
			worldModelHandler.assertGL("(Logistic)");
		} else if(env.equals("Assembly")) {
			worldModelHandler.assertGL("(Assembly)");
			
			agentCommunicator = new MultiAgentCommunicator(messageQueue);

			AgentExecutor.execute(brokerHost, SEMANTICMAP_ID, agentCommunicator, kr.ac.uos.ai.agentCommunicationFramework.BrokerType.ACTIVEMQ);
			worldModelHandler.assertObject("AgentCommunication", agentCommunicator);
			worldModelHandler.assertObject("Channel", "base", agentCommunicator.getBaseChannel());
		}
		ArbiAgentExecutor.execute(brokerAddress, brokerPort, MY_ADDRESS, this, brokerType);
		dataSource.connect(brokerAddress, brokerPort, MY_DATASOURCE_PREFIX, brokerType);
		
		worldModelHandler.assertObject("WorldModelHandler", worldModelHandler);
		worldModelHandler.assertObject("GLHandler", glHandler);
		worldModelHandler.assertObject("PlanLoader", planLoader);
		worldModelHandler.assertObject("TaskTracker", taskTracker);
		worldModelHandler.assertObject("TaskManager", this);
		worldModelHandler.assertObject("JarLoader", jarLoader);
		
		logger = new MonitorLogger(this,interpreter);

//		connectToMcARBIAgent("test", "172.16.165.185", 51116, "ZeroMQ");
		Thread t = new Thread() {
			public void run() {
				interpreter.run();
			}
		};
		
		t.start();
	}
	
	public void connectToMcARBIAgent(String agentName, String ip, int port, String brokerType) {
		Adaptor adaptor = null;
		if (brokerType.equals("ZeroMQ")) {
			adaptor = new ZeroMQAdaptor(this, agentName, ip, port);
		} else if (brokerType.equals("Socket")) {
			adaptor = new SocketAdaptor(this, agentName, ip, port);
		} else if (brokerType.equals("ActiveMQ")) {
			adaptor = new ActiveMQAdaptor(this, agentName, ip, port);
		}
		
		adaptor.start();
		worldModelHandler.assertObject("AgentAdaptor", agentName ,adaptor);
	}
	
	public void createFilter(Adaptor adaptor, String action) {
		//TODO
	}
	
	@Override
	public void onData(String sender, String data) {
		ReceivedMessage message = new ReceivedMessage(sender, "onData",data);
		messageQueue.add(message);
	}
	
	public void enqueueMessage(ReceivedMessage message) {
		messageQueue.add(message);
	}
	
	
	public boolean dequeueMessage() {
		if (messageQueue.isEmpty())
			return false;
		else {
			try {
				ReceivedMessage message = messageQueue.take();
//				System.out.println("[SENDER]  : " + message.getSender());
				
				worldModelHandler.assertObject("MessageReceived", message);
//				if(message.getMessage().startsWith("(")) {
//					GeneralizedList gl = null;
////					System.out.println("[MESSAGE] : " + message.getMessage());
//					String data = message.getMessage();
////					String sender = message.getSender();
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

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			catch (ParseException | org.json.simple.parser.ParseException e) {
//				e.printStackTrace();
//			}

			return true;
		}
	}

	
	public String getAgentID() {
		return MY_AGENT_ID;
	}
	

	public void assertFact(String context) {
		dataSource.assertFact(context);
	}
	
	public void retractFact(String context) {
		dataSource.retractFact(context);
	}
	
	public void updateFact(String fact) {
		// Demo Code
		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(fact);
			String str = gl.getExpression(1).toString();
			dataSource.assertFact(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		dataSource.updateFact(fact);
	}
	
	public String getTaskProgress(String agentID, String goal) {
//		System.out.println("agent ID : " + agentID);
//		System.out.println("goal : " + goal);
		AgentWorkflow workflow = workflowMap.get(agentID);
		workflow.progressWorkflow(goal);
		return workflow.getProgress() + "/4";
	}
	
	
	public void sleepAwhile(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		MonitoringAgent agent = new MonitoringAgent("Logistic", "172.16.165.77", 41314);
	}
}
