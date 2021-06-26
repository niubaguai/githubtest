package com.pan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pan.dao")
public class MysecondsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MysecondsystemApplication.class, args);
    }

}
