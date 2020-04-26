# @Async 注解讲解
在有future使用的方法或所在类上加上该注解，自动新增一个线程跑这个任务

注意要点
- 在主类上需要添加@EnableAsync注解
- @Async 所在类需要添加spring bean 注解，即将该方法注入spring容器中
- 不能和调用方法在同一个类