package cc.mrbird.batch.processor;

import cc.mrbird.batch.entity.TestData;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * @author MrBird
 */
@Component
public class TestDataFilterItemProcessor implements ItemProcessor<TestData, TestData> {
    @Override
    public TestData process(TestData item) {
        // 返回null，会过滤掉这条数据
        return "".equals(item.getField3()) ? null : item;
    }
}
