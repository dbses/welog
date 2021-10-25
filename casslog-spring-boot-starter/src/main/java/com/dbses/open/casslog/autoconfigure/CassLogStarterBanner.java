package com.dbses.open.casslog.autoconfigure;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.util.Optional;

/**
 * @author yanglulu
 * @date 2021/10/15
 */
public class CassLogStarterBanner implements Banner, ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassLogStarterBanner.class);

    @Override
    public void run(ApplicationArguments args) {
        printBanner(null, null, System.out);

        // 打印内部的状态
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);

        if (LOGGER instanceof ch.qos.logback.classic.Logger) {
            LOGGER.info("CassLog initialize Success.");
        } else {
            fail();
        }
    }

    private void fail() {
        LOGGER.info("CassLog initialize Fail.");
    }

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {

        String version = Optional.ofNullable(getClass().getPackage()).map(Package::getImplementationVersion).map("v"::concat).orElse("");

        String banner = "  ____              _                \n" +
                " / ___|__ _ ___ ___| |    ___   __ _ \n" +
                "| |   / _` / __/ __| |   / _ \\ / _` |\n" +
                "| |__| (_| \\__ \\__ \\ |__| (_) | (_| |\n" +
                " \\____\\__,_|___/___/_____\\___/ \\__, |\n" +
                "                               |___/  \t" + version + "\n";

        out.println(banner);
    }

}
