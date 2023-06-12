package kr.ac.uos.ai.mcarbi.monitor.utility;

import java.time.LocalDateTime;

public class AgentTask {
	private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public AgentTask(int id) {
        this.id = id;
        this.startTime = LocalDateTime.now();
    }
    
    public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
    
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public boolean isCompleted() {
        return endTime != null;
    }
}
