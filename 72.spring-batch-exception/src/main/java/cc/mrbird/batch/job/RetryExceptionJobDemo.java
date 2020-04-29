package cc.mrbird.batch.job;

import cc.mrbird.batch.exception.MyJobExecutionException;
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
public class RetryExceptionJobDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job retryExceptionJob() {
        return jobBuilderFactory.get("retryExceptionJob")
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
                .retry(MyJobExecutionException.class) // 配置重试的异常类型
                .retryLimit(3) // 重试3次，三次过后还是异常的话，则任务会结束，
                // 异常的次数为reader，processor和writer中的总数，这里仅在processor里演示异常重试
                .build();
    }

    private ListItemReader<String> listItemReader() {
        ArrayList<String> datas = new ArrayList<>();
        IntStream.range(0, 5).forEach(i -> datas.add(String.valueOf(i)));
        return new ListItemReader<>(datas);
    }

    private ItemProcessor<String, String> myProcessor() {
        return new ItemProcessor<String, String>() {
            private int count;
            @Override
            public String process(String item) throws Exception {
                System.out.println("当前处理的数据：" + item);
                if (count >= 2) {
                    return item;
                } else {
                    count++;
                    throw new MyJobExecutionException("任务处理出错");
                }
            }
        };
    }
}
