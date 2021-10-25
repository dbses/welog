package com.dbses.open.casslog.asyncright;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

@Slf4j
@RequestMapping("logging")
@RestController
public class LoggingController {

    @GetMapping("manylog")
    public void manylog(@RequestParam(name = "count", defaultValue = "1000") int count) {
        long begin = System.currentTimeMillis();
        IntStream.rangeClosed(1, count).forEach(i -> log.info("log-{}", i));
        System.out.println("took " + (System.currentTimeMillis() - begin) + " ms");
    }
}
