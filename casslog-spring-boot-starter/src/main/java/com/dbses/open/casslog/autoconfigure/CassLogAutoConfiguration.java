package com.dbses.open.casslog.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanglulu
 * @date 2021/10/15
 */
@Configuration
public class CassLogAutoConfiguration {

    @Bean
    public CassLogStarterBanner casslogStarterBanner() {
        return new CassLogStarterBanner();
    }

}
