package kr.ac.uos.ai.mcarbi.monitor.utility.demo;

import java.util.ArrayList;
import java.util.List;

public class TaskTracker {
    private List<AgentTask> realTasks;
    private List<AgentTask> simulationTasks;
    private long createdTime;
    
    public TaskTracker() {
    	createdTime = System.currentTimeMillis();
        realTasks = new ArrayList<>();
        simulationTasks = new ArrayList<AgentTask>();
    }

    public void registTask(String env, String id) {
    	System.out.println("new Task! " +id + " at " + env);
    	AgentTask task = new AgentTask(id);
    	if (env.equals("real")) {
    		realTasks.add(task);
    	} else if (env.equals("simulation")) {
    		simulationTasks.add(task);
    	}
    }
    
    public void startTask(String env, String id) {
    	if (env.equals("real")) {
        	for(AgentTask task : realTasks) {
        		if(task.getID().equals(id)) {

        	    	System.out.println("Task start! " + id);
        			task.setStartTime(System.currentTimeMillis());
        			break;
        		}
        	}
    	} else if (env.equals("simulation")) {
        	for(AgentTask task : simulationTasks) {
        		if(task.getID().equals(id)) {

        	    	System.out.println("Task start! " + id);
        			task.setStartTime(System.currentTimeMillis());
        			break;
        		}
        	}
    	}
    }
    
    public void completeTask(String env, String id) {
    	if (env.equals("real")) {
    		for(AgentTask t : realTasks) {
        		if(t.getID().equals(id)) {
        	    	System.out.println("Task complete! " + id);
        			t.setEndTime(System.currentTimeMillis());
        			break;
        		}
        	}
    	} else if (env.equals("simulation")) {
    		for(AgentTask t : simulationTasks) {
        		if(t.getID().equals(id)) {
        	    	System.out.println("Task complete! " + id);
        			t.setEndTime(System.currentTimeMillis());
        			break;
        		}
        	}
    	}
    }
    
    public int getOngoingTasksCount(String env) {
    	int result = 0;
    	if (env.equals("real")) {
    		for (AgentTask task : realTasks) {
        		if (!task.isCompleted() && task.isStarted()) {
        			result += 1;
        		}
        	}
    	} else if (env.equals("simulation")) {
    		for (AgentTask task : simulationTasks) {
        		if (!task.isCompleted() && task.isStarted()) {
        			result += 1;
        		}
        	}
    	}
    	return result;

    }
    
    public int getPendingTasksCount(String env) {
    	
    	int result = 0;
    	
    	if (env.equals("real")) {
    		for (AgentTask task : realTasks) {
        		if (!task.isCompleted()) {
        			result += 1;
        		}
        	}
    	} else if (env.equals("simulation")) {
    		for (AgentTask task : simulationTasks) {
        		if (!task.isCompleted()) {
        			result += 1;
        		}
        	}
    	}
    	
    	return result;

    }

    public int getCompletedTasksCount(String env) {
    	long tenMinuteAgo = System.currentTimeMillis() - (600 * 100);
    	int result = 0;
    	if (env.equals("real")) {
    		for (AgentTask task : realTasks) {
        		if (task.isCompleted()) {
        			result += 1;
        		}
        	}
    	} else if (env.equals("simulation")) {
    		for (AgentTask task : simulationTasks) {
        		if (task.isCompleted()) {
        			result += 1;
        		}
        	}
    	}
    	return result;

    }

    public int getNewTasksCount(String env) {
    	int result = 0;
    	long tenMinuteAgo = System.currentTimeMillis() - (600 * 100);
    	
    	if (env.equals("real")) {
        	for (AgentTask task : realTasks) {
        		if (task.getStartTime() > tenMinuteAgo) {
        			result += 1;
        		}
        	}
    	} else if (env.equals("simulation")) {
        	for (AgentTask task : simulationTasks) {
        		if (task.getStartTime() > tenMinuteAgo) {
        			result += 1;
        		}
        	}
    	}
    	return result;
    }
    
    public double getAverageSpeed(String env) {
    	
    	double totalTime = 0;
    	int completedTasks = 0;
    	
    	if (env.equals("real")) {
    		for (AgentTask task : realTasks) {
        		if (task.isCompleted()) {
        			completedTasks += 1;
        			totalTime = totalTime + (task.getEndTime() - task.getStartTime());
        		}
        	}
        	
    	} else if (env.equals("simulation")) {
    		for (AgentTask task : simulationTasks) {
        		if (task.isCompleted()) {
        			completedTasks += 1;
        			totalTime = totalTime + (task.getEndTime() - task.getStartTime());
        		}
        	}
    	}
    	
    	if (completedTasks == 0) return 0;
    	else return totalTime / (completedTasks * 1000);
    }

    public double getCumulativeSpeed(String env) {
    	
    	if (env.equals("real")) {
    		if (realTasks.size() == 0 ) return 0;
        	else return (System.currentTimeMillis() - createdTime) / (realTasks.size() * 1000) ;
    	} else if (env.equals("simulation")) {
    		if (simulationTasks.size() == 0 ) return 0;
        	else return (System.currentTimeMillis() - createdTime) / (simulationTasks.size() * 1000) ;
    	}
    	return 0;
    }
    
    public int getTotalTasksCount(String env) {
    	if (env.equals("real")) {
    		return realTasks.size();
    	} else if (env.equals("simulation")) {
    		return simulationTasks.size();
    	}
    	return 0;
    }
}
