package com.zy.myblog.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.zy.myblog.admin.mapper"})
public class BlogAdminApp {
    public static void main(String[] args) {
        SpringApplication.run(BlogAdminApp.class);
    }
}
