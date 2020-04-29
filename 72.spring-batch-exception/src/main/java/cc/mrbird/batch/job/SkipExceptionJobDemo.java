package cc.mrbird.batch.job;

import cc.mrbird.batch.exception.MyJobExecutionException;
import cc.mrbird.batch.listener.MySkipListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * @author MrBird
 */
@Component
public class SkipExceptionJobDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private MySkipListener mySkipListener;

    @Bean
    public Job skipExceptionJob() {
        return jobBuilderFactory.get("skipExceptionJob")
                .start(step())
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get("step")
                .<String, String>chunk(2)
                .reader(listItemReader())
                .processor(myProcessor())
                .writer(list -> list.forEach(System.out::println))
                .faultTolerant() // 配置错误容忍
                .skip(MyJobExecutionException.class) // 配置跳过的异常类型
                .skipLimit(1) // 最多跳过1次，1次过后还是异常的话，则任务会结束，
                // 异常的次数为reader，processor和writer中的总数，这里仅在processor里演示异常跳过
                .listener(mySkipListener)
                .build();
    }

    private ListItemReader<String> listItemReader() {
        ArrayList<String> datas = new ArrayList<>();
        IntStream.range(0, 5).forEach(i -> datas.add(String.valueOf(i)));
        return new ListItemReader<>(datas);
    }

    private ItemProcessor<String, String> myProcessor() {
        return item -> {
            System.out.println("当前处理的数据：" + item);
            if ("2".equals(item)) {
                throw new MyJobExecutionException("任务处理出错");
            } else {
                return item;
            }
        };
    }
}
