package kr.ac.uos.ai.mcarbi.monitor.utility;

import java.util.ArrayList;
import java.util.List;

public class TaskTracker {
    private List<AgentTask> tasks;

    public TaskTracker() {
        tasks = new ArrayList<>();
    }

    public void registeTask(String id) {
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
        return (int) tasks.stream()
                .filter(task -> !task.isCompleted() && task.isStarted())
                .count();
    }
    
    public int getPendingTasksCount() {
        return (int) tasks.stream()
                .filter(task -> !task.isCompleted())
                .count();
    }

    public int getCompletedTasksCount() {
        long oneMinuteAgo = System.currentTimeMillis() - (60 * 1000);
        return (int) tasks.stream()
                .filter(task -> task.isCompleted() && task.getEndTime() > oneMinuteAgo)
                .count();
    }

    public int getNewTasksCount() {
    	long oneMinuteAgo = System.currentTimeMillis() - (60 * 1000);
        return (int) tasks.stream()
                .filter(task -> task.getStartTime() > oneMinuteAgo)
                .count();
    }
    
    public double getAverageSpeed() {
        double sumSpeed = tasks.stream()
                .filter(AgentTask::isCompleted)
                .mapToDouble(AgentTask::getTaskAverageSpeed)
                .sum();
        int completedTasks = (int) tasks.stream().filter(AgentTask::isCompleted).count();
        if (completedTasks == 0) {
            return 0.0;
        }
        return sumSpeed / completedTasks;
    }

    public double getCumulativeSpeed() {
        double cumulativeSpeed = 0.0;
        
        long currentEndTime = 0;
        AgentTask task = tasks.get(0);
        long duration = System.currentTimeMillis() - task.getStartTime();
        return duration / tasks.size();
    }
    
    public int getAllTaskCount() {
    	return tasks.size();
    }
}
