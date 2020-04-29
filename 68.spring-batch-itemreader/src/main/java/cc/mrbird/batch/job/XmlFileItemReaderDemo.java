package cc.mrbird.batch.job;

import cc.mrbird.batch.entity.TestData;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MrBird
 */
@Component
public class XmlFileItemReaderDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job xmlFileItemReaderJob() {
        return jobBuilderFactory.get("xmlFileItemReaderJob")
                .start(step())
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get("step")
                .<TestData, TestData>chunk(2)
                .reader(xmlFileItemReader())
                .writer(list -> list.forEach(System.out::println))
                .build();
    }

    private ItemReader<TestData> xmlFileItemReader() {
        StaxEventItemReader<TestData> reader = new StaxEventItemReader<>();
        reader.setResource(new ClassPathResource("file.xml")); // 设置xml文件源
        reader.setFragmentRootElementName("test"); // 指定xml文件的根标签
        // 将xml数据转换为TestData对象
        XStreamMarshaller marshaller = new XStreamMarshaller();
        // 指定需要转换的目标数据类型
        Map<String, Class<TestData>> map = new HashMap<>(1);
        map.put("test", TestData.class);
        marshaller.setAliases(map);

        reader.setUnmarshaller(marshaller);
        return reader;
    }
}
