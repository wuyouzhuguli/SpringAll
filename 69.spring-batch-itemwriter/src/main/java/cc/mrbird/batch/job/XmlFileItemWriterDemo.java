package cc.mrbird.batch.job;

import cc.mrbird.batch.entity.TestData;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MrBird
 */
@Component
public class XmlFileItemWriterDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private ListItemReader<TestData> simpleReader;

    @Bean
    public Job xmlFileItemWriterJob() throws Exception {
        return jobBuilderFactory.get("xmlFileItemWriterJob")
                .start(step())
                .build();
    }

    private Step step() throws Exception {
        return stepBuilderFactory.get("step")
                .<TestData, TestData>chunk(2)
                .reader(simpleReader)
                .writer(xmlFileItemWriter())
                .build();
    }

    private StaxEventItemWriter<TestData> xmlFileItemWriter() throws IOException {
        StaxEventItemWriter<TestData> writer = new StaxEventItemWriter<>();

        // 通过XStreamMarshaller将TestData转换为xml
        XStreamMarshaller marshaller = new XStreamMarshaller();

        Map<String,Class<TestData>> map = new HashMap<>(1);
        map.put("test", TestData.class);

        marshaller.setAliases(map); // 设置xml标签

        writer.setRootTagName("tests"); // 设置根标签
        writer.setMarshaller(marshaller);

        FileSystemResource file = new FileSystemResource("/Users/mrbird/Desktop/file.xml");
        Path path = Paths.get(file.getPath());
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        writer.setResource(file); // 设置目标文件路径
        return writer;
    }
}
