package com.dbses.open.casslog.core;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.action.NOPAction;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.core.env.Environment;

/**
 * @author yanglulu
 * @date 2021/10/22
 */
public class CassLogJoranConfigurator extends JoranConfigurator {

    private LoggingInitializationContext initializationContext;

    CassLogJoranConfigurator(LoggingInitializationContext initializationContext) {
        this.initializationContext = initializationContext;
    }

    @Override
    public void addInstanceRules(RuleStore rs) {
        super.addInstanceRules(rs);
        Environment environment = this.initializationContext.getEnvironment();
        rs.addRule(new ElementSelector("configuration/springProperty"),
                new CassLogPropertyAction(environment));
        rs.addRule(new ElementSelector("*/springProfile"),
                new CassLogPropertyAction(this.initializationContext.getEnvironment()));
        rs.addRule(new ElementSelector("*/springProfile/*"), new NOPAction());
    }

}
