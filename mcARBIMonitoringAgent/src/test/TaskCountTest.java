package test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskCountTest {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // 작업 등록 예시
        taskManager.registerTask();
        taskManager.registerTask();
        taskManager.registerTask();

        // 작업 완료 예시
        LocalDateTime now = LocalDateTime.now();
        taskManager.getTasks().get(0).setEndTime(now.plusMinutes(10));
        taskManager.getTasks().get(1).setEndTime(now.plusMinutes(20));
        taskManager.getTasks().get(2).setEndTime(now.plusMinutes(30));

        // 최근 1시간 내에 등록된 작업 개수 조회 예시
        int recentTasksCount = taskManager.getRecentTasksCount();
        System.out.println("최근 1시간 내에 등록된 작업 개수: " + recentTasksCount);

        // 아직 완료되지 않은 작업 개수 조회 예시
        int pendingTasksCount = taskManager.getPendingTasksCount();
        System.out.println("아직 완료되지 않은 작업 개수: " + pendingTasksCount);

        // 최근 1시간 이내에 완료된 작업 개수 조회 예시
        int completedTasksCount = taskManager.getCompletedTasksCount();
        System.out.println("최근 1시간 이내에 완료된 작업 개수: " + completedTasksCount);

        // 작업 평균 속도 조회 예시
        double averageSpeed = taskManager.getAverageSpeed();
        System.out.println("작업 평균 속도: " + averageSpeed);

        // 작업 누적 속도 조회 예시
        double cumulativeSpeed = taskManager.getCumulativeSpeed();
        System.out.println("작업 누적 속도: " + cumulativeSpeed);
        
        System.out.println("local time " + LocalDateTime.now());
        System.currentTimeMillis();
    }
}