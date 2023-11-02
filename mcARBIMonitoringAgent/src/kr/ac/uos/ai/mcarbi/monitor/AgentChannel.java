package kr.ac.uos.ai.mcarbi.monitor;

import java.util.concurrent.BlockingQueue;

import kr.ac.uos.ai.agentCommunicationFramework.BrokerType;
import kr.ac.uos.ai.agentCommunicationFramework.agent.Agent;
import kr.ac.uos.ai.agentCommunicationFramework.agent.communication.Channel;
import kr.ac.uos.ai.mcarbi.monitor.message.ReceivedMessage;

public class AgentChannel extends Channel{

	private BlockingQueue<ReceivedMessage> messageQueue;
	
	public AgentChannel(String channelName, Agent agent, BrokerType brokerType, BlockingQueue<ReceivedMessage> queue) {
		super(channelName, agent, brokerType);
		this.messageQueue = queue;
	}
	
	@Override
	public void onData(String sender, String data) {
//		System.out.println("ondata? : " + data);
		ReceivedMessage msg = new ReceivedMessage(sender, "channel", data);
		messageQueue.add(msg);	
	}

	@Override
	public String onRequest(String sender, String request) {
		ReceivedMessage msg = new ReceivedMessage(sender, "channel", request);
		messageQueue.add(msg);	
		return "(ok)";
	}

	@Override
	public String onQuery(String sender, String query) {
		ReceivedMessage msg = new ReceivedMessage(sender, "channel", query);
		messageQueue.add(msg);	
		return null;
	}

	@Override
	public String onSubscribe(String sender, String subscribe) {
		ReceivedMessage msg = new ReceivedMessage(sender, "channel", subscribe);
		messageQueue.add(msg);	
		return null;
	}

	@Override
	public void onUnsubscribe(String sender, String unsubscribe) {
		ReceivedMessage msg = new ReceivedMessage(sender, "channel", unsubscribe);
		messageQueue.add(msg);	
	}

	@Override
	public void onNotify(String sender, String data) {
		ReceivedMessage msg = new ReceivedMessage(sender,"channel",  data);
		messageQueue.add(msg);	
	}

	@Override
	public void onSystem(String sender, String data) {
		ReceivedMessage msg = new ReceivedMessage(sender,"channel", data);
		messageQueue.add(msg);	
		
	}

}
