package net.evecom;

import net.evecom.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AsynTest {
    @Autowired
    OrderService orderService;

    @Test
    public void test(){
        System.out.println("开始async");
        Future<List<String>> future1 = orderService.getList();
        Future<List<String>> future2 = orderService.getList2();
        //同步执行for循环
        for(int i=0; i < 10;i++){
            System.out.println("i：" + i);
        }

        //获取异步任务的处理结果，异步任务没有处理完成，会一直阻塞，可以设置超时时间，使用 get 的重载方法
        List<String> list1 = null;
        try {
            list1 = future1.get();
            System.out.println("list size1：" + list1.size());
            List<String> list2 = future1.get();
            System.out.println("list size2：" + list2.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(e.getStackTrace());
        } catch (ExecutionException e) {
            System.out.println(e.getStackTrace());
        }
    }
}
