package com.dbses.open.casslog.async;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AsyncApplication {

    public static void main(String[] args) {
        System.setProperty("logging.config", "classpath:com/dbses/open/casslog/async/performance_async.xml");
        SpringApplication.run(AsyncApplication.class, args);
    }
}
