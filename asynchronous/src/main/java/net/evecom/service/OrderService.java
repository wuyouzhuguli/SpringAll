package net.evecom.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
@Async
public class OrderService {
//    @Async("taskExecutor")
    public Future<List<String>> getList(){
        List<String> list = new ArrayList<>();
        try {
            System.out.println("开始处理任务1");
            for(int i=0; i < 100000;i++){
                list.add(i+"");
            }
            //让线程睡2秒
            Thread.sleep(1500);
            System.out.println("任务1处理完成");
        } catch (Exception e) {

        }
        return new AsyncResult<>(list);
    }

    /**
     * 异步任务2，返回处理结果
     * @return
     */
//    @Async("taskExecutor")
    public Future<List<String>> getList2(){
        List<String> list = new ArrayList<>();
        try {
            System.out.println("开始处理任务2");
            for(int i=0; i < 100000;i++){
                list.add(i+"");
            }
            //让线程睡2秒
            Thread.sleep(1500);
            System.out.println("任务2处理完成");
        } catch (Exception e) {

        }
        return new AsyncResult<>(list);
    }
}
