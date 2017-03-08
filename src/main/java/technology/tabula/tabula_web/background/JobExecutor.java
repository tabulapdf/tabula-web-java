package technology.tabula.tabula_web.background;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import technology.tabula.tabula_web.background.job.Job;


public class JobExecutor extends ThreadPoolExecutor {
	
	@SuppressWarnings("Convert2Diamond")
	private Map<String, Job> jobs = new ConcurrentHashMap<String, Job>();
	@SuppressWarnings("Convert2Diamond")
	private Map<Future<String>, Job> futureJobs = new ConcurrentHashMap<Future<String>, Job>();
	private static final Object mutex = new Object();
	
	final static Logger logger = LoggerFactory.getLogger(JobExecutor.class);
	
	private static class SingletonHolder {
        private static final JobExecutor INSTANCE = new JobExecutor();
    }

    public static JobExecutor getInstance() {
    	synchronized (mutex) {
    		return SingletonHolder.INSTANCE;	
    	}
    }
    
	@SuppressWarnings("Convert2Diamond")
	private JobExecutor() {
		super(3, 10, 300, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		logger.info("Starting JobExecutor");
	}

	@Override
	protected void afterExecute(Runnable runnable, Throwable throwable) {
		super.afterExecute(runnable, throwable);
		FutureTask<?> ft = (FutureTask<?>) runnable;
		if (throwable == null && runnable instanceof FutureTask<?>) {
			try {
				if (ft.isDone()) ft.get();
			}
			catch (CancellationException e) {
				throwable = e.getCause();
			}
			catch (ExecutionException e) {
				throwable = e;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			if (throwable == null) {
				// task finished ok
				//this.futureJobs.get(key)
			}
			else {
				// TODO/ improve this
				throwable.printStackTrace(System.out);
			}
		}
	}
	
	public Future<String> submitJob(Job task) {
		jobs.put(task.getUUID(), task);
		Future<String> f = this.submit(task);
		futureJobs.put(f, task);
		return f;
	}

	public void submitJobs(Job... jobs) {
		for (Job j: jobs) {
			submitJob(j);
		}
	}
	
	public Job getJob(String uuid) {
		return jobs.get(uuid);
	}
	
	public List<Job> getByBatch(String uuid) {
		return jobs.values()
				.stream()
				.filter(j -> j.getBatch().equals(uuid))
                .collect(Collectors.toList());
	}
}
