package kr.ac.uos.ai.mcarbi.monitor.utility.demo;

import java.util.ArrayList;
import java.util.List;

public class AgentWorkflow {
	
	private final String 			agentID;
	private List<String>			workflow;
	private int						progress;
	
	public AgentWorkflow(String agentID) {
		this.agentID = agentID;
		workflow = new ArrayList<>();
		progress = 0;
		
		if (agentID.contains("LIFT")) {
			workflow.add("MoveToLocation");
			workflow.add("ObjectGrabbed");
			workflow.add("MoveToLocation");
			workflow.add("ObjectPlaced");
		} else if (agentID.contains("Epson")) {
			workflow.add("PCBPrepared");
			workflow.add("HousingPrepared");
			workflow.add("HousingAssembled");
			workflow.add("PCBAssembled");
		} else if (agentID.contains("UR")) {
			workflow.add("FrontPrepared");
			workflow.add("LensPrepared");
			workflow.add("LensAssembled");
			workflow.add("ForntAssembled");
		}
		
	}
	public void progressWorkflow(String goal) {
		if(progress >= workflow.size()) {
			progress = 0;
		}
		
		if(workflow.get(progress).equals(goal)) {
			progress = progress + 1;
		}
	}
	
	public int getProgress() {
		return progress;
	}
	
	public static void main(String[] args) {
		AgentWorkflow test = new AgentWorkflow("AMR_LIFT1");
		
		test.progressWorkflow("MoveToLocation");
		System.out.println(test.getProgress());
		test.progressWorkflow("ObjectGrabbed");
		System.out.println(test.getProgress());
		test.progressWorkflow("MoveToLocation");
		System.out.println(test.getProgress());
		test.progressWorkflow("ObjectPlaced");
		System.out.println(test.getProgress());

		test.progressWorkflow("MoveToLocation");
		System.out.println(test.getProgress());
		test.progressWorkflow("ObjectGrabbed");
		System.out.println(test.getProgress());
		test.progressWorkflow("MoveToLocation");
		System.out.println(test.getProgress());
		test.progressWorkflow("ObjectPlaced");
		System.out.println(test.getProgress());
		

		test.progressWorkflow("MoveToLocation");
		System.out.println(test.getProgress());
		test.progressWorkflow("ObjectGrabbed");
		System.out.println(test.getProgress());
		test.progressWorkflow("MoveToLocation");
		System.out.println(test.getProgress());
		test.progressWorkflow("ObjectPlaced");
		System.out.println(test.getProgress());
		

		test.progressWorkflow("MoveToLocation");
		System.out.println(test.getProgress());
		test.progressWorkflow("ObjectGrabbed");
		System.out.println(test.getProgress());
		test.progressWorkflow("MoveToLocation");
		System.out.println(test.getProgress());
		test.progressWorkflow("ObjectPlaced");
		System.out.println(test.getProgress());
	}
}
