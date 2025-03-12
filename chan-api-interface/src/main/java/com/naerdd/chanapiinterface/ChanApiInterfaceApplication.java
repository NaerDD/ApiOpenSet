package com.naerdd.chanapiinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class ChanApiInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChanApiInterfaceApplication.class, args);
    }

}
