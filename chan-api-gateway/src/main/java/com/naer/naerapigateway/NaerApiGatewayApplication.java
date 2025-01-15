package com.naer.naerapigateway;

import com.naer.project.provider.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@Service
@EnableDubbo
public class NaerApiGatewayApplication {

    @DubboReference
    private DemoService demoService;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(NaerApiGatewayApplication.class, args);
        NaerApiGatewayApplication application = context.getBean(NaerApiGatewayApplication.class);
        String result = application.doSayHello("world");
        String result2 = application.doSayHello2("naer");
        System.out.println("result: " + result);
        System.out.println("result: " + result2);
    }

    public String doSayHello(String name) {
        return demoService.sayHello(name);
    }

    public String doSayHello2(String name) {
        return demoService.sayHello(name);
    }

//    @Bean
//    public RouteLocator customRouterLocater(RouteLocatorBuilder builder){
//        return builder.routes()
//                .route("tobaidu", r -> r.path("/baidu")
//                        .uri("https://www.baidu.com"))
//                .route("tonaer", r -> r.path("/naergithub")
//                        .uri("https://github.com/NaerDD?tab=repositories&q=&type=&language=&sort="))
//                .build();
//    }
}
