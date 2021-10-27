package com.dbses.open.casslog.autoconfigure;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import com.dbses.open.casslog.core.CassLogInitializeException;
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
public class CassLogStarterBanner implements Banner {

    public static void print() {
        Logger logger = LoggerFactory.getLogger(CassLogStarterBanner.class);

        new CassLogStarterBanner().printBanner(null, null, System.out);

        // 打印初始化过程
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);

        if (logger instanceof ch.qos.logback.classic.Logger) {
            logger.info("CassLog initialize Success.");
        } else {
            throw new CassLogInitializeException("CassLog initialize Fail: not instance of ch.qos.logback.classic.Logger");
        }
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
