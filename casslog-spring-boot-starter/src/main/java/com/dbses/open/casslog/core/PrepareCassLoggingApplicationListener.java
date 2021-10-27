package com.dbses.open.casslog.core;

import com.dbses.open.casslog.autoconfigure.CassLogStarterBanner;
import lombok.Setter;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.logging.LoggingApplicationListener;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationEvent;

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

    @Setter
    private static boolean bannerPrinted = false;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        super.onApplicationEvent(event);

        if (event instanceof ApplicationPreparedEvent && !bannerPrinted) {
            CassLogStarterBanner.print();
            setBannerPrinted(true);
        }
    }

}
