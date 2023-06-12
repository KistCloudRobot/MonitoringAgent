package kr.ac.uos.ai.mcarbi.monitor.adaptor;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import kr.ac.uos.ai.mcarbi.monitor.MonitoringAgent; 

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
		zmqSocket.setIdentity(agent.getAgentID().getBytes());
		
		this.mcARBIAgentName = mcARBIagentName;
		this.agent = agent;
		messageRecvTask = new MessageRecvTask();
		
	}
	
	private class MessageRecvTask extends Thread {
		@Override
		public void run() {
			while (true) {
                byte[] requestMessage = zmqSocket.recv();
                String receivedMessage = new String(requestMessage);

//              System.out.println("Received request:");
//              System.out.println("Recipient: " + receivedRecipient);
//              System.out.println("Message: " + receivedMessage);

                agent.onData(mcARBIAgentName, receivedMessage);
				
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
//				} catch (Exception e) {
//					System.err.println("parsing error");
//				}
			} 
		}
		
	}
	
	public void send(String message) {
		zmqSocket.sendMore("");
		zmqSocket.send(message);
	}

	@Override
	public void start() {
		messageRecvTask.start();
		
	}
	
}
