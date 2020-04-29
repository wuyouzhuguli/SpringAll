package cc.mrbird.batch.processor;

import cc.mrbird.batch.entity.TestData;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author MrBird
 */
@Component
public class TestDataTransformItemPorcessor implements ItemProcessor<TestData, TestData> {
    @Override
    public TestData process(TestData item) {
        // field1值拼接 hello
        item.setField1(item.getField1() + " hello");
        return item;
    }
}
