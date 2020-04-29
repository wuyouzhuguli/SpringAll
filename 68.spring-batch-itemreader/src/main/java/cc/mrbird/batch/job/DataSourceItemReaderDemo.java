package cc.mrbird.batch.job;

import cc.mrbird.batch.entity.TestData;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MrBird
 */
@Component
public class DataSourceItemReaderDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    // 注入数据源
    @Autowired
    private DataSource dataSource;

    @Bean
    public Job dataSourceItemReaderJob() throws Exception {
        return jobBuilderFactory.get("dataSourceItemReaderJob")
                .start(step())
                .build();
    }

    private Step step() throws Exception {
        return stepBuilderFactory.get("step")
                .<TestData, TestData>chunk(2)
                .reader(dataSourceItemReader())
                .writer(list -> list.forEach(System.out::println))
                .build();
    }

    private ItemReader<TestData> dataSourceItemReader() throws Exception {
        JdbcPagingItemReader<TestData> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource); // 设置数据源
        reader.setFetchSize(5); // 每次取多少条记录
        reader.setPageSize(5); // 设置每页数据量

        // 指定sql查询语句 select id,field1,field2,field3 from TEST
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("id,field1,field2,field3"); //设置查询字段
        provider.setFromClause("from TEST"); // 设置从哪张表查询

        // 将读取到的数据转换为TestData对象
        reader.setRowMapper((resultSet, rowNum) -> {
            TestData data = new TestData();
            data.setId(resultSet.getInt(1));
            data.setField1(resultSet.getString(2)); // 读取第一个字段，类型为String
            data.setField2(resultSet.getString(3));
            data.setField3(resultSet.getString(4));
            return data;
        });

        Map<String, Order> sort = new HashMap<>(1);
        sort.put("id", Order.ASCENDING);
        provider.setSortKeys(sort); // 设置排序,通过id 升序

        reader.setQueryProvider(provider);

        // 设置namedParameterJdbcTemplate等属性
        reader.afterPropertiesSet();
        return reader;
    }
}
