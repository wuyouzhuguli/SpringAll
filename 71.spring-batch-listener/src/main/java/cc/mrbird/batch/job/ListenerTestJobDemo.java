package cc.mrbird.batch.job;

import cc.mrbird.batch.listener.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author MrBird
 */
@Component
public class ListenerTestJobDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private MyJobExecutionListener myJobExecutionListener;
    @Autowired
    private MyStepExecutionListener myStepExecutionListener;
    @Autowired
    private MyChunkListener myChunkListener;
    @Autowired
    private MyItemReaderListener myItemReaderListener;
    @Autowired
    private MyItemProcessListener myItemProcessListener;
    @Autowired
    private MyItemWriterListener myItemWriterListener;

    @Bean
    public Job listenerTestJob() {
        return jobBuilderFactory.get("listenerTestJob")
                .start(step())
                .listener(myJobExecutionListener)
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get("step")
                .listener(myStepExecutionListener)
                .<String, String>chunk(2)
                .faultTolerant()
                .listener(myChunkListener)
                .reader(reader())
                .listener(myItemReaderListener)
                .processor(processor())
                .listener(myItemProcessListener)
                .writer(list -> list.forEach(System.out::println))
                .listener(myItemWriterListener)
                .build();
    }

    private ItemReader<String> reader() {
        List<String> data = Arrays.asList("java", "c++", "javascript", "python");
        return new simpleReader(data);
    }

    private ItemProcessor<String, String> processor() {
        return item -> item + " language";
    }
}

class simpleReader implements ItemReader<String> {
    private Iterator<String> iterator;

    public simpleReader(List<String> data) {
        this.iterator = data.iterator();
    }

    @Override
    public String read() {
        return iterator.hasNext() ? iterator.next() : null;
    }
}
