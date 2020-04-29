package cc.mrbird.batch.job;

import cc.mrbird.batch.entity.TestData;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @author MrBird
 */
@Component
public class MultiFileItemWriteDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private ListItemReader<TestData> simpleReader;
    @Autowired
    private ItemStreamWriter<TestData> fileItemWriter;
    @Autowired
    private ItemStreamWriter<TestData> xmlFileItemWriter;

    @Bean
    public Job multiFileItemWriterJob() {
        return jobBuilderFactory.get("multiFileItemWriterJob6")
                .start(step())
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get("step")
                .<TestData, TestData>chunk(2)
                .reader(simpleReader)
                .writer(multiFileItemWriter())
                // .stream(fileItemWriter)
                // .stream(xmlFileItemWriter)
                .build();
    }

    // 输出数据到多个文件
    private CompositeItemWriter<TestData> multiFileItemWriter() {
        // 使用CompositeItemWriter代理
        CompositeItemWriter<TestData> writer = new CompositeItemWriter<>();
        // 设置具体写代理
        writer.setDelegates(Arrays.asList(fileItemWriter, xmlFileItemWriter));
        return writer;
    }

    // 将数据分类，然后分别输出到对应的文件(此时需要将writer注册到ioc容器，否则报
    // WriterNotOpenException: Writer must be open before it can be written to)
    private ClassifierCompositeItemWriter<TestData> classifierMultiFileItemWriter() {
        ClassifierCompositeItemWriter<TestData> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier((Classifier<TestData, ItemWriter<? super TestData>>) testData -> {
            try {
                // id能被2整除则输出到普通文本，否则输出到xml文本
                return testData.getId() % 2 == 0 ? fileItemWriter : xmlFileItemWriter;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        return writer;
    }
}
