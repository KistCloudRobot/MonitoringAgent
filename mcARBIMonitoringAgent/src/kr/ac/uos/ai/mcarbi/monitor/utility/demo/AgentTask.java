package kr.ac.uos.ai.mcarbi.monitor.utility.demo;

import java.time.LocalDateTime;

public class AgentTask {
	private String id;
	private long createTime;
    private long startTime;
    private long endTime;

    public AgentTask(String id) {
        this.id = id;
        this.createTime = System.currentTimeMillis();
        this.startTime = 0;
        this.endTime = 0;
    }
    
    public void setStartTime(long startTime) {
    	if (this.startTime == 0) this.startTime = startTime;
	}
    public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
    
    public long getCreateTime() {
		return createTime;
	}
    
    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public boolean isStarted() {
    	return startTime != 0;
    }
    public boolean isCompleted() {
        return endTime != 0;
    }
    
    public String getID() {
    	return id;
    }
    
    public double getTaskAverageSpeed() {
        if (isCompleted()) {
            long duration = endTime - startTime;
            if (duration == 0) {
                return 0.0;
            }
            return Double.parseDouble(id) / (duration / (1000.0 * 60));
        }
        return 0.0;
    }
}
