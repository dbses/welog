package com.dbses.open.casslog.asyncright;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AsyncrightApplication {

    public static void main(String[] args) {
        System.setProperty("logging.config", "classpath:com/dbses/open/casslog/asyncright/asyncright.xml");
        SpringApplication.run(AsyncrightApplication.class, args);
    }
}
