package kr.ac.uos.ai.mcarbi.monitor.utility;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskTracker {
    private List<AgentTask> tasks;

    public TaskTracker() {
        tasks = new ArrayList<>();
    }

    public void registerTask(int id) {
    	AgentTask task = new AgentTask(id);
        tasks.add(task);
    }
    // 최근 1시간 내에 등록된 작업 개수
    public int getRecentTasksCount() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return (int) tasks.stream()
                .filter(task -> task.getStartTime().isAfter(oneHourAgo))
                .count();
    }
    // 아직 완료되지 않은 작업 개수
    public int getPendingTasksCount() {
        return (int) tasks.stream()
                .filter(task -> !task.isCompleted())
                .count();
    }
    // 최근 1시간 이내에 완료된 작업 개수
    public int getCompletedTasksCount() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return (int) tasks.stream()
                .filter(task -> task.isCompleted() && task.getEndTime().isAfter(oneHourAgo))
                .count();
    }
    
    public int getAllTaskCount() {
    	return tasks.size();
    }
}
