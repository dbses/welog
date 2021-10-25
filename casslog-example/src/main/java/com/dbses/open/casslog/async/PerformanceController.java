package com.dbses.open.casslog.async;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yanglulu
 * @date 2021/10/19
 */
@Slf4j
@RestController
public class PerformanceController {

    @GetMapping("performance")
    public void performance(@RequestParam(name = "count", defaultValue = "1000") int count) {
        long begin = System.currentTimeMillis();
        Marker timeMarker = MarkerFactory.getMarker("performance");
        String payload = IntStream.rangeClosed(1, 1000000)
                .mapToObj(i -> "a")
                .collect(Collectors.joining("")) + UUID.randomUUID().toString();
        IntStream.rangeClosed(1, count).forEach(i -> log.info(timeMarker, "{} {}", i, payload));
        log.info("took {} ms", System.currentTimeMillis() - begin);
    }

}
