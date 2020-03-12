package cc.mrbird.batch.listener;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

/**
 * @author MrBird
 */
@Component
public class MyItemReaderListener implements ItemReadListener<String> {
    @Override
    public void beforeRead() {
        System.out.println("before read");
    }

    @Override
    public void afterRead(String item) {
        System.out.println("after read: " + item);
    }

    @Override
    public void onReadError(Exception ex) {
        System.out.println("on read error: " + ex.getMessage());
    }
}
