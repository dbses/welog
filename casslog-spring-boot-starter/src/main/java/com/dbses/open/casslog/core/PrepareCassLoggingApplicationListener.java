package com.dbses.open.casslog.core;

import org.springframework.boot.logging.LoggingApplicationListener;
import org.springframework.boot.logging.LoggingSystem;

/**
 * @author yanglulu
 * @date 2021/10/21
 */
public class PrepareCassLoggingApplicationListener extends LoggingApplicationListener {

    static {
        System.setProperty(LoggingApplicationListener.CONFIG_PROPERTY,
                "classpath:com/dbses/open/casslog/core/casslog.xml");
        System.setProperty(LoggingSystem.SYSTEM_PROPERTY, CassLoggingSystem.class.getName());
    }

}
