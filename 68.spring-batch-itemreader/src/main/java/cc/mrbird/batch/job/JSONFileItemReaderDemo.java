package cc.mrbird.batch.job;

import cc.mrbird.batch.entity.TestData;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * @author MrBird
 */
@Component
public class JSONFileItemReaderDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jsonFileItemReaderJob() {
        return jobBuilderFactory.get("jsonFileItemReaderJob")
                .start(step())
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get("step")
                .<TestData, TestData>chunk(2)
                .reader(jsonItemReader())
                .writer(list -> list.forEach(System.out::println))
                .build();
    }

    private ItemReader<TestData> jsonItemReader() {
        // 设置json文件地址
        ClassPathResource resource = new ClassPathResource("file.json");
        // 设置json文件转换的目标对象类型
        JacksonJsonObjectReader<TestData> jacksonJsonObjectReader = new JacksonJsonObjectReader<>(TestData.class);
        JsonItemReader<TestData> reader = new JsonItemReader<>(resource, jacksonJsonObjectReader);
        // 给reader设置一个别名
        reader.setName("testDataJsonItemReader");
        return reader;
    }
}
