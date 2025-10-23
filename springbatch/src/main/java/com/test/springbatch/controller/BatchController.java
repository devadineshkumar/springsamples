package com.test.springbatch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @GetMapping("/startJob")
    public String startJob() {
        try {
            JobParameters parameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(job, parameters);
            return "Batch job started. Job ID: " + jobExecution.getJobId();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to start job: " + e.getMessage();
        }
    }

    @GetMapping("/startJobByName")
    public String startJobByName() {
        try {
            // Assuming 'job' is the job you want to trigger.
            // In a real scenario, you might fetch the job from a JobRegistry or similar.
            // For this example, we'll use the already autowired 'job'.

            // Calculate start time for two minutes from now
            long startAt = System.currentTimeMillis() + (2 * 60 * 1000);

            JobParameters parameters = new JobParametersBuilder()
                    .addLong("startAt", startAt)
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, parameters);
            return "Batch job '" + job.getName() + "' started with a delay. Job ID: " + jobExecution.getJobId();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to start job by name: " + e.getMessage();
        }
    }

    // New: schedule a job (by bean name) to run after a delay (seconds)
    @GetMapping("/scheduleJob")
    public String scheduleJob(@RequestParam(name = "jobName", defaultValue = "customerJob") String jobName,
                              @RequestParam(name = "delaySeconds", defaultValue = "120") long delaySeconds) {
        try {
            // Try to obtain the Job bean by name from the context
            Job jobToRun = applicationContext.getBean(jobName, Job.class);

            long scheduledTimeMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delaySeconds);

            // Build parameters with a unique timestamp so the job can be re-run
            JobParameters params = new JobParametersBuilder()
                    .addLong("scheduledTime", scheduledTimeMillis)
                    .addLong("requestTimestamp", System.currentTimeMillis())
                    .toJobParameters();

            // Schedule the launch in the future
            scheduler.schedule(() -> {
                try {
                    jobLauncher.run(jobToRun, params);
                } catch (Exception ex) {
                    // Log the exception to stderr for now
                    ex.printStackTrace();
                }
            }, delaySeconds, TimeUnit.SECONDS);

            Date runAt = new Date(scheduledTimeMillis);
            return "Scheduled job '" + jobName + "' to run at: " + runAt + " (in " + delaySeconds + " seconds)";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to schedule job '" + jobName + "': " + e.getMessage();
        }
    }

    // New: schedule a job using JobRepository to demonstrate repository interaction
    @GetMapping("/scheduleJobWithRepo")
    public String scheduleJobWithRepo(@RequestParam(name = "jobName", defaultValue = "customerJob") String jobName,
                                      @RequestParam(name = "delaySeconds", defaultValue = "120") long delaySeconds) {
        try {
            Job jobToRun = applicationContext.getBean(jobName, Job.class);

            long scheduledTimeMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delaySeconds);

            JobParameters params = new JobParametersBuilder()
                    .addLong("scheduledTime", scheduledTimeMillis)
                    .addLong("requestTimestamp", System.currentTimeMillis())
                    .toJobParameters();

            // Create a job execution record in the repository so it appears in job tables immediately.
            // Note: jobLauncher.run will also interact with the repository when actually running.
            JobExecution placeholderExecution = null;
            try {
                placeholderExecution = jobRepository.createJobExecution(jobName, params);
            } catch (Exception ex) {
                // Some JobRepository implementations may not allow creating executions directly or may
                // throw if parameters conflict; we just log and continue to schedule the job launch.
                ex.printStackTrace();
            }

            final JobParameters scheduledParams = params;
            scheduler.schedule(() -> {
                try {
                    jobLauncher.run(jobToRun, scheduledParams);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, delaySeconds, TimeUnit.SECONDS);

            Date runAt = new Date(scheduledTimeMillis);
            String response = "Scheduled job '" + jobName + "' to run at: " + runAt + " (in " + delaySeconds + " seconds)";
            if (placeholderExecution != null) {
                response += " - placeholderExecutionId=" + placeholderExecution.getId();
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to schedule job with repository for '" + jobName + "': " + e.getMessage();
        }
    }

    // New: get last execution details for a job
    @GetMapping("/jobStatus")
    public String jobStatus(@RequestParam(name = "jobName", defaultValue = "customerJob") String jobName) {
        try {
            List<JobInstance> instances = jobExplorer.getJobInstances(jobName, 0, 1);
            if (instances == null || instances.isEmpty()) {
                return "No job instances found for job: " + jobName;
            }
            JobInstance latestInstance = instances.get(0);
            List<JobExecution> executions = jobExplorer.getJobExecutions(latestInstance);
            if (executions == null || executions.isEmpty()) {
                return "No executions found for latest instance of job: " + jobName;
            }
            JobExecution latestExecution = executions.get(0);
            return "Job: " + jobName + " lastExecutionId=" + latestExecution.getId() +
                    " status=" + latestExecution.getStatus() +
                    " startTime=" + latestExecution.getStartTime() +
                    " endTime=" + latestExecution.getEndTime();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to get job status for '" + jobName + "': " + e.getMessage();
        }
    }

}
