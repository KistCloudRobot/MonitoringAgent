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
    // �ֱ� 1�ð� ���� ��ϵ� �۾� ����
    public int getRecentTasksCount() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return (int) tasks.stream()
                .filter(task -> task.getStartTime().isAfter(oneHourAgo))
                .count();
    }
    // ���� �Ϸ���� ���� �۾� ����
    public int getPendingTasksCount() {
        return (int) tasks.stream()
                .filter(task -> !task.isCompleted())
                .count();
    }
    // �ֱ� 1�ð� �̳��� �Ϸ�� �۾� ����
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
