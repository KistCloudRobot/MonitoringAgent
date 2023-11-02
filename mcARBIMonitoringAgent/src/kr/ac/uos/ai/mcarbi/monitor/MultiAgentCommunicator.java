package kr.ac.uos.ai.mcarbi.monitor;

import java.util.concurrent.BlockingQueue;

import kr.ac.uos.ai.agentCommunicationFramework.agent.ChannelFactory;
import kr.ac.uos.ai.agentCommunicationFramework.BrokerType;
import kr.ac.uos.ai.agentCommunicationFramework.agent.Agent;
import kr.ac.uos.ai.agentCommunicationFramework.agent.communication.Channel;
import kr.ac.uos.ai.mcarbi.monitor.message.ReceivedMessage;

public class MultiAgentCommunicator extends Agent{
	private BlockingQueue<ReceivedMessage> messageQueue;

	private boolean isTriggered = false;

	public MultiAgentCommunicator(BlockingQueue<ReceivedMessage> queue) {
		messageQueue = queue;
	}

	public String getConversationID() {
		return this.getConversationID();
	}
	public Channel createAgentChannel(String channelName) {
		Channel channel = new AgentChannel(channelName, this, BrokerType.ACTIVEMQ, messageQueue);
		ChannelFactory.createChannel(channel);
		return channel;
	}
	
	public Channel connectAgentChannel(String channelName) {
		Channel channel = new AgentChannel(channelName, this, BrokerType.ACTIVEMQ, messageQueue);
		ChannelFactory.connectChannel(channel);
		channel.send("agent://www.mcarbi.com/SemanticMapManager", "(hi)");
		return channel;
	}
	
	@Override
	public void onNotify(String sender, String notification) {
//		System.out.println("recieved Notify from " + sender + " : " + notification);
//		aplViewer.msgReceived(notification, sender);
		ReceivedMessage msg = new ReceivedMessage(sender, "channel", notification);
		messageQueue.add(msg);	
	}

	@Override
	public void onStart() {
	}


	@Override
	public String onQuery(String sender, String query) {
		//System.out.println("recieved query from " + sender + " : " + query);

		ReceivedMessage msg = new ReceivedMessage(sender,"channel", query);
		messageQueue.add(msg);	

		return "(result \"ok\")";
	}


	@Override
	public void onData(String sender, String data) {
		//System.out.println("recieved data from " + sender + " : " + data);
		ReceivedMessage msg = new ReceivedMessage(sender, "channel", data);
		messageQueue.add(msg);
	}

	@Override
	public synchronized String onRequest(String sender, String request) {
		//System.out.println("received data from " + sender + " : " + request);
		ReceivedMessage msg = new ReceivedMessage(sender, "channel", request);
		messageQueue.add(msg);

		return "(ok)";
	}
}