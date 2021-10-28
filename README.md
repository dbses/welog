# Casslog

# 一、背景

为了使业务团队专注于业务迭代，减少对日志系统配置的感知。需将业务服务的日志配置统一抽取，做到统一配置、统一管理、统一升级，达到业务团队0配置接入使用日志系统的目的。

`casslog`是基于`logback`日志框架封装的一款日志系统，意在集中解决业务团队在使用日志系统过程中遇到的配置、规范、变更等问题。

# 二、架构

![img](https://gitee.com/yanglu_u/ImgRepository/raw/master/%E4%BC%81%E4%B8%9A%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_16352361437314.png)

logback.xml 配置集成 spring cloud config。

# 三、使用

> 使用要求：logback 版本 1.1.7 以上。
>
> logback 版本 1.1.7 存在已知缺陷（https://jira.qos.ch/browse/LOGBACK-1164 ），导致应用启动报错。

### 引入依赖

`pom.xml`引入依赖：

```xml
<dependency>
    <groupId>com.dbses.open</groupId>
    <artifactId>casslog-spring-boot-starter</artifactId>
    <version>最新稳定版</version>
</dependency>

```
### 应用配置

`bootstrap.yml`配置：

```yaml
spring:
  application:
    name: casslog-example
  cloud:
    config:
      uri: http://localhost:8080/conf
      label: alpha
```

### 启动应用

```java
@SpringBootApplication
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
```

启动后效果如下图所示：

![image-20211026163738586](https://gitee.com/yanglu_u/ImgRepository/raw/master/image-20211026163738586.png)

在目录`{projectRoot}/logs`下可看到以工程名称（取自`${spring.application.name}`）命名的日志文件。

### 打印日志

```java
@RestController
@Slf4j
public class ExampleController {
    // Same as @Slf4j
//    private static final Logger log = LoggerFactory.getLogger(ExampleController.class);
    
    private static final String SUCCESS = "success";
    
    @GetMapping("/log")
    public String log(@RequestParam(value = "param", defaultValue = "I_AM_PARAM") String param) {
        if (log.isDebugEnabled()) {
            log.debug("This is a debug message. 中文");
            log.debug("This is a debug message. {}", param);
        }

        log.info("This is an info message. 中文");
        log.info("This is an info message. {}", param);

        log.warn("This is a warn message. 中文");
        log.warn("This is a warn message. {}", param);

        log.error("This is an error message. 中文");
        log.error("This is an error message. {}", param);

        return SUCCESS;
    }
}
```

日志打印效果如下：

![image-20211026165820628](https://gitee.com/yanglu_u/ImgRepository/raw/master/image-20211026165820628.png)

# 四、功能

## 4.1 Casslog 配置说明

Spring Cloud Config 配置中心`commons/__common_log_.yml`内容如下：

```yml
casslog:
  # logback-spring_.xml
  appender:
    console:
      # 控制台输出模板
      pattern: "[%d{HH:mm:ss.SSS}] [%thread] [%-5level] [%logger{40}:%-4.4line] [%tid] - %.-500msg%n"
    rollingFile:
      # 日志文件
      fileName: logs/${spring.application.name}.log
      # 日志文件输出模板
      pattern: "[%d{HH:mm:ss.SSS}] [%thread] [%-5level] [%logger{40}:%-4.4line] [%tid] - %.-500msg%n"
      # 文件rolling归档
      fileNamePattern: logs/%d/${spring.application.name}.%i.log.zip
      # 文件rolling触发大小
      fileMaxSize: "128MB"
      # 文件rolling保留天数
      maxHistory: 30
  loggers:
    root:
      # 日志打印级别
      level: INFO
    # 特殊包打印级别定义
    logger:
      name: com.dbses.open.casslog
      level: DEBUG
```

- `casslog.loggers.root.level`

  此配置项配置的日志打印级别是全局的。

- `casslog.loggers.logger.name`和`casslog.loggers.logger.level`

  这两项配置通常成对出现，可对某个包下面的日志级别进行局部配置。

## 4.2 日志长度限制

通过配置中心可配置日志内容打印的长度：

```yaml
casslog:
  appender:
    console:
      pattern: "[%d{HH:mm:ss.SSS}] [%thread] [%-5level] [%logger{40}:%-4.4line] [%tid] - %.-500msg%n"
    rollingFile:
      pattern: "[%d{HH:mm:ss.SSS}] [%thread] [%-5level] [%logger{40}:%-4.4line] [%tid] - %.-500msg%n"
```

`%.-500msg`表示最多打印 500 个字符，超出 500 的部分不显示。

## 4.3 异步日志打印

对于大文本日志内容，同步打印耗时很长。

测试代码如下：

```java
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
```

当`count`分别为 1000、10000 时，同步打印耗时如下：

```
[17:54:51.412] [http-nio-30001-exec-1] [INFO ] [c.c.o.c.async.PerformanceController:30] [TID: N/A] - took 6547 ms
[17:56:05.623] [http-nio-30001-exec-2] [INFO ] [c.c.o.c.async.PerformanceController:30] [TID: N/A] - took 68966 ms
```

使用 Casslog 异步打印（异步队列大小配置为 1000），打印耗时如下：

```
[17:58:15.787] [http-nio-30001-exec-1] [INFO ] [c.c.o.c.async.PerformanceController:30] [TID: N/A] - took 1628 ms
[17:58:33.934] [http-nio-30001-exec-2] [INFO ] [c.c.o.c.async.PerformanceController:30] [TID: N/A] - took 12726 ms
```

可见性能有较大幅度的提升。

## 4.4 打印 Skywalking traceid

```yaml
casslog:
  appender:
    console:
      pattern: "[%d{HH:mm:ss.SSS}] [%thread] [%-5level] [%logger{40}:%-4.4line] [%tid] - %.-500msg%n"
    rollingFile:
      pattern: "[%d{HH:mm:ss.SSS}] [%thread] [%-5level] [%logger{40}:%-4.4line] [%tid] - %.-500msg%n"
```

`%tid`表示 Skywalking 的 traceid。

