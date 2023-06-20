package kr.ac.uos.ai.mcarbi.monitor.message;

public class ReceivedMessage {
	private final String		sender;
	private final String		message;
	private final String		action;
	
	public ReceivedMessage(String sender, String action ,String message) {
		this.sender = sender;
		this.message = message;
		this.action = action;
	}
	public String getSender() {
		return sender;
	}
	public String getMessage() {
		return message;
	}
	
	public String getAction() {
		return action;
	}
	@Override
	public String toString() {
		
		return sender + " " +message;
	}
}
