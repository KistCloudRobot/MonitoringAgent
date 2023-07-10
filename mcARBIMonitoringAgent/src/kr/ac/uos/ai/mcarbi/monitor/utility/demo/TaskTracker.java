package kr.ac.uos.ai.mcarbi.monitor.utility.demo;

import java.util.ArrayList;
import java.util.List;

public class TaskTracker {
    private List<AgentTask> tasks;
    private long createdTime;
    
    public TaskTracker() {
    	createdTime = System.currentTimeMillis();
        tasks = new ArrayList<>();
    }

    public void registTask(String id) {
    	System.out.println("new Task! " + id);
    	AgentTask task = new AgentTask(id);
        tasks.add(task);
    }
    
    public void startTask(String id) {
    	for(AgentTask task : tasks) {
    		if(task.getID().equals(id)) {

    	    	System.out.println("Task start! " + id);
    			task.setStartTime(System.currentTimeMillis());
    			break;
    		}
    	}
    }
    
    public void completeTask(String id) {
    	for(AgentTask t : tasks) {
    		if(t.getID().equals(id)) {
    	    	System.out.println("Task complete! " + id);
    			t.setEndTime(System.currentTimeMillis());
    			break;
    		}
    	}
    }
    
    public int getOngoingTasksCount() {
    	int result = 0;
    	for (AgentTask task : tasks) {
    		if (!task.isCompleted() && task.isStarted()) {
    			result += 1;
    		}
    	}
    	return result;

    }
    
    public int getPendingTasksCount() {
    	int result = 0;
    	for (AgentTask task : tasks) {
    		if (!task.isCompleted()) {
    			result += 1;
    		}
    	}
    	return result;

    }

    public int getCompletedTasksCount() {
    	long tenMinuteAgo = System.currentTimeMillis() - (600 * 1000);
    	int result = 0;
    	for (AgentTask task : tasks) {
    		if (task.isCompleted()) {
    			result += 1;
    		}
    	}
    	
    	return result;

    }

    public int getNewTasksCount() {
    	long tenMinuteAgo = System.currentTimeMillis() - (600 * 1000);
    	int result = 0;
    	for (AgentTask task : tasks) {
    		if (task.getStartTime() > tenMinuteAgo) {
    			result += 1;
    		}
    	}
    	return result;
    }
    
    public double getAverageSpeed() {
    	double totalTime = 0;
    	int completedTasks = 0;
    	for (AgentTask task : tasks) {
    		if (task.isCompleted()) {
    			completedTasks += 1;
    			totalTime = totalTime + (task.getEndTime() - task.getStartTime());
    		}
    	}
    	
    	if (completedTasks == 0) return 0;
    	else return totalTime / completedTasks;
    }

    public double getCumulativeSpeed() {
    	if (tasks.size() == 0 ) return 0;
    	else return (System.currentTimeMillis() - createdTime) / tasks.size() ;
    }
    
    public int getTotalTasksCount() {
    	return tasks.size();
    }
}
