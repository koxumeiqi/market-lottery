package com.ly;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
@Configurable
//@EnableDubbo
public class RaffleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaffleApplication.class);
    }

}
