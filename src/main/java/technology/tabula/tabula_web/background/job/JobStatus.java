package technology.tabula.tabula_web.background.job;

import java.time.LocalDateTime;

class JobStatus {
	public enum STATUS { QUEUED, WORKING, COMPLETED, FAILED, KILLED };
	
	STATUS status;
	LocalDateTime startedOn;
	int total = 0;
	int num = 0;
	String message;
	
}