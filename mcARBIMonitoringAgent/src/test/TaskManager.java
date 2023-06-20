package test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<AgentTask> tasks;

    public TaskManager() {
        tasks = new ArrayList<>();
    }

    public void registerTask() {
        AgentTask task = new AgentTask();
        tasks.add(task);
    }
    
    public List<AgentTask> getTasks() {
    	return tasks;
    }

    public int getRecentTasksCount() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return (int) tasks.stream()
                .filter(task -> task.getStartTime().isAfter(oneHourAgo))
                .count();
    }

    public int getPendingTasksCount() {
        return (int) tasks.stream()
                .filter(task -> !task.isCompleted())
                .count();
    }

    public int getCompletedTasksCount() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return (int) tasks.stream()
                .filter(task -> task.isCompleted() && task.getEndTime().isAfter(oneHourAgo))
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
        LocalDateTime currentEndTime = null;
        for (AgentTask task : tasks) {
            if (task.isCompleted()) {
                LocalDateTime endTime = task.getEndTime();
                if (currentEndTime != null && endTime.isAfter(currentEndTime)) {
                    Duration duration = Duration.between(currentEndTime, endTime);
                    long minutes = duration.toMinutes();
                    cumulativeSpeed += task.getId().hashCode() / (double) minutes;
                }
                currentEndTime = endTime;
            }
        }
        return cumulativeSpeed;
    }
}