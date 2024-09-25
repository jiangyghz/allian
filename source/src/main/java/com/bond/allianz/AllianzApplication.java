package com.bond.allianz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@MapperScan(value = {"com.bond.allianz.mapper"})
@SpringBootApplication
@EnableScheduling
public class AllianzApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllianzApplication.class, args);
    }

}
