package cc.mrbird.provider.service;

import cc.mrbird.common.api.HelloService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.cluster.loadbalance.RoundRobinLoadBalance;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Service(
        interfaceClass = HelloService.class,
        weight = 100,
        loadbalance = RoundRobinLoadBalance.NAME)
@Component
public class HelloServiceImpl implements HelloService {

    @Override
    @HystrixCommand(fallbackMethod = "defaultHello")
    public String hello(String message) {
        System.out.println("调用 cc.mrbird.provider.service.HelloServiceImpl#hello");
        // try {
        //     TimeUnit.SECONDS.sleep(2);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        String a = null;
        a.toString();
        return "hello," + message;
    }

    public String defaultHello(String message) {
        return "hello anonymous";
    }
}
