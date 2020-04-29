package cc.mrbird.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author MrBird
 */
@Component
public class SplitJobDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job splitJob() {
        return jobBuilderFactory.get("splitJob")
                .start(flow1())
                .split(new SimpleAsyncTaskExecutor()).add(flow2())
                .end()
                .build();

    }

    private Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("执行步骤一操作。。。");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    private Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("执行步骤二操作。。。");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    private Step step3() {
        return stepBuilderFactory.get("step3")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("执行步骤三操作。。。");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    private Flow flow1() {
        return new FlowBuilder<Flow>("flow1")
                .start(step1())
                .next(step2())
                .build();
    }

    private Flow flow2() {
        return new FlowBuilder<Flow>("flow2")
                .start(step3())
                .build();
    }
}
