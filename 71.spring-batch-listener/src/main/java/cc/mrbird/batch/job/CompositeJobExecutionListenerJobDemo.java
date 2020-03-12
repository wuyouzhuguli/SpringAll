package cc.mrbird.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.CompositeJobExecutionListener;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author MrBird
 */
@Component
public class CompositeJobExecutionListenerJobDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job compositeJobExecutionListenerJob() {
        return jobBuilderFactory.get("compositeJobExecutionListenerJob")
                .start(step())
                .listener(compositeJobExecutionListener())
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get("step")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("执行步骤....");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    private CompositeJobExecutionListener compositeJobExecutionListener() {
        CompositeJobExecutionListener listener = new CompositeJobExecutionListener();

        // 任务监听器1
        JobExecutionListener jobExecutionListenerOne = new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                System.out.println("任务监听器One，before job execute: " + jobExecution.getJobInstance().getJobName());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                System.out.println("任务监听器One，after job execute: " + jobExecution.getJobInstance().getJobName());
            }
        };
        // 任务监听器2
        JobExecutionListener jobExecutionListenerTwo = new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                System.out.println("任务监听器Two，before job execute: " + jobExecution.getJobInstance().getJobName());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                System.out.println("任务监听器Two，after job execute: " + jobExecution.getJobInstance().getJobName());
            }
        };
        // 聚合
        listener.setListeners(Arrays.asList(jobExecutionListenerOne, jobExecutionListenerTwo));
        return listener;
    }
}
