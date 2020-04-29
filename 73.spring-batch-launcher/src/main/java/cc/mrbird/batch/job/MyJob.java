package cc.mrbird.batch.job;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author MrBird
 */
@Component
public class MyJob{

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(){
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }

    private Step step(){
        return stepBuilderFactory.get("step")
                .tasklet((stepContribution, chunkContext) -> {
                    StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
                    Map<String, JobParameter> parameters = stepExecution.getJobParameters().getParameters();
                    System.out.println(parameters.get("message").getValue());
                    return RepeatStatus.FINISHED;
                })
                .listener(this)
                .build();
    }
}
