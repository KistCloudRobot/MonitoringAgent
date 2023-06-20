package test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class AgentTask {
    private UUID id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public AgentTask() {
        this.id = UUID.randomUUID();
        this.startTime = LocalDateTime.now();
        this.endTime = null;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isCompleted() {
        return endTime != null;
    }

    public double getTaskAverageSpeed() {
        if (isCompleted()) {
            Duration duration = Duration.between(startTime, endTime);
            long minutes = duration.toMinutes();
            if (minutes == 0) {
                return 0.0;
            }
            return id.hashCode() / (double) minutes;
        }
        return 0.0;
    }
}