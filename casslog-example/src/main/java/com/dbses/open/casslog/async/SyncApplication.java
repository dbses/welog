package com.dbses.open.casslog.async;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SyncApplication {

    public static void main(String[] args) {
        System.setProperty("logging.config", "classpath:com/dbses/open/casslog/async/performance_sync.xml");
        SpringApplication.run(SyncApplication.class, args);
    }
}
