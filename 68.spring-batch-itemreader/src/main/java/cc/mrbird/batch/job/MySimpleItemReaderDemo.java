package cc.mrbird.batch.job;

import cc.mrbird.batch.reader.MySimpleIteamReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author MrBird
 */
@Component
public class MySimpleItemReaderDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job mySimpleItemReaderJob() {
        return jobBuilderFactory.get("mySimpleItemReaderJob")
                .start(step())
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get("step")
                .<String, String>chunk(2)
                .reader(mySimpleItemReader())
                .writer(list -> list.forEach(System.out::println))  // 简单输出，后面再详细介绍writer
                .build();
    }

    private ItemReader<String> mySimpleItemReader() {
        List<String> data = Arrays.asList("java", "c++", "javascript", "python");
        return new MySimpleIteamReader(data);
    }
}
