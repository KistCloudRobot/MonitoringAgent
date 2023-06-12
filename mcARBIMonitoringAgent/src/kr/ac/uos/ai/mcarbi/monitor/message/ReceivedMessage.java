package kr.ac.uos.ai.mcarbi.monitor.message;

public class ReceivedMessage {
	private final String		sender;
	private final String		message;
	
	public ReceivedMessage(String sender, String message) {
		this.sender = sender;
		this.message = message;
	}
	public String getSender() {
		return sender;
	}
	public String getMessage() {
		return message;
	}
	@Override
	public String toString() {
		
		return sender + " " +message;
	}
}
