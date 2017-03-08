package technology.tabula.tabula_web.background.job;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import technology.tabula.tabula_web.background.JobExecutor;

public abstract class Job implements Callable<String> {
	
	private UUID uuid;
	private UUID batch;
	private JobStatus status;
	
	final static Logger logger = LoggerFactory.getLogger(JobExecutor.class);

	public Job(UUID batch) {
		this.uuid = UUID.randomUUID();
		this.batch = batch;
		this.status = new JobStatus();
	}
	
	public String getUUID() {
		return this.uuid.toString();
	}
	
	public String getBatch() {
		return this.batch.toString();
	}
	
	@Override
	public String call() throws Exception {
		this.status.status = JobStatus.STATUS.WORKING;
		this.status.startedOn = LocalDateTime.now();
				
		logger.info("Starting job {} {}", this.getUUID(), this.getClass().getName());
		perform();
		return this.getUUID();
	}
	
	public int percentComplete() {
		switch (this.status.status) {
			case COMPLETED:
				return 100;
			case QUEUED:
				return 0;
			default:
				int t = status.total == 0 ? 1 : status.total;
		        return status.num / t;
		}
	}
	
	public void at(int num, int total, String message) {
		this.status.status = JobStatus.STATUS.WORKING;
		this.status.num = num;
		this.status.total = total;
		this.status.message = message;
	}

	public boolean isFailed() {
	    return this.status.status == JobStatus.STATUS.FAILED;
    }
    public boolean isWorking() {
	    return this.status.status == JobStatus.STATUS.WORKING;
    }
	
	public abstract void perform() throws Exception;
}
