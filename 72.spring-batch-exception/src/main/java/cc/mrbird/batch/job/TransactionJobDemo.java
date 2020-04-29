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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * @author MrBird
 */
@Component
public class TransactionJobDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job transactionJob() {
        return jobBuilderFactory.get("transactionJob")
                .start(step())
                .build();
    }

    private Step step() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setPropagationBehavior(Propagation.REQUIRED.value());
        attribute.setIsolationLevel(Isolation.DEFAULT.value());
        attribute.setTimeout(30);

        return stepBuilderFactory.get("step")
                .<String, String>chunk(2)
                .reader(listItemReader())
                .writer(list -> list.forEach(System.out::println))
                .readerIsTransactionalQueue()
                .transactionAttribute(attribute)
                .build();
    }

    private ListItemReader<String> listItemReader() {
        ArrayList<String> datas = new ArrayList<>();
        IntStream.range(0, 5).forEach(i -> datas.add(String.valueOf(i)));
        return new ListItemReader<>(datas);
    }
}
