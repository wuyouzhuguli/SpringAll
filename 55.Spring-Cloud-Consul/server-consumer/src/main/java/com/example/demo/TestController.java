package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author MrBird
 */
@RestController
public class TestController {

    private Logger loggr = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    private final RestTemplate restTemplate = new RestTemplate();

    // @Autowired
    // private RestTemplate restTemplate;

    private static final String SERVER_ID = "server-provider";

    @GetMapping("uri")
    public List<URI> getServerUris() {
        return this.discoveryClient.getInstances(SERVER_ID)
                .stream()
                .map(ServiceInstance::getUri).collect(Collectors.toList());
    }

    @GetMapping("hello")
    public String hello() {
        ServiceInstance instance = loadBalancerClient.choose(SERVER_ID);
        String url = instance.getUri().toString() + "/hello";
        loggr.info("remote server url：{}", url);
        return restTemplate.getForObject(url, String.class);
    }

    // @Bean
    // @LoadBalanced
    // public RestTemplate restTemplate() {
    //     return new RestTemplate();
    // }

    // @GetMapping("hello")
    // public String hello() {
    //     String url = "http://" + SERVER_ID + "/hello";
    //     return restTemplate.getForObject(url, String.class);
    // }

}
