# JDK 21 与 Spring Boot 4 升级说明

## 升级目标

本次升级的目标是把项目从历史的 Spring Boot 2.x / Spring Framework 5.x 技术栈，升级到当前稳定可运行的现代版本：

- JDK 21
- Spring Boot 4.0.6
- Spring Framework 7.0.7

升级范围覆盖根 POM、核心模块 `mountain-webflux`、示例模块 `mountain-test` 以及少量 Spring API 兼容代码。

## 升级结果

升级完成后，项目已经具备以下状态：

- 使用 JDK 21 编译与测试
- 根项目 `mvn test` 全量通过
- 示例工程在 Spring Boot 4 测试上下文中启动成功
- 核心 `WebClient` 路径在 Spring Framework 7 下兼容

建议使用以下命令验证：

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) mvn test
```

## 主要改动

### 1. 构建链升级

根项目 `pom.xml` 做了以下调整：

- `java.version` 升级到 `21`
- 使用 `maven.compiler.release=21`
- Spring Boot BOM 升级到 `4.0.6`
- 统一收敛 Maven 插件版本
- 升级 Lombok 到兼容 JDK 21 的版本

### 2. 依赖管理清理

为减少与 Spring Boot 4 BOM 冲突，做了以下整理：

- 移除对旧版 `spring-webflux`、`spring-context` 的显式版本钉死
- 移除旧版 `logback` 直连依赖，改为使用 `slf4j-api`
- 保持核心模块依赖尽量交给 Spring Boot / Spring BOM 管理

### 3. Spring API 兼容调整

由于 Spring 6/7 对部分 API 做了调整，代码侧做了最小兼容修改：

- `HttpStatus` 相关逻辑改为 `HttpStatusCode`
- `HttpStatus::isError` 风格判断改为 `HttpStatusCode` 新接口
- `clientResponse.statusCode()` 的异常构造逻辑同步调整
- `HttpMethod.resolve(...)` 改为 `HttpMethod.valueOf(...)`

### 4. 示例模块兼容调整

`mountain-test` 中原本使用了 Boot 旧路径下的 `RestTemplateBuilder`。在 Boot 4 中，这部分不再按原位置工作，因此调整为：

- 使用纯 Spring `RestTemplate`
- 通过 `DefaultUriBuilderFactory` 设置 `baseUrl`

这样可以避免绑定旧的 Boot 专属客户端包路径，同时保留原有 `rootUri` 语义。

## 涉及文件

本次升级主要涉及以下文件：

- `pom.xml`
- `mountain-webflux/pom.xml`
- `mountain-test/pom.xml`
- `mountain-webflux/src/main/java/cn/ivan/mountain/client/impl/WebClientImpl.java`
- `mountain-webflux/src/main/java/cn/ivan/mountain/exception/MountainHttpException.java`
- `mountain-webflux/src/main/java/cn/ivan/mountain/proxy/MethodMetadataBuilder.java`
- `mountain-test/src/main/java/cn/ivan/mountain/config/RestClient.java`

## 验证结果

已完成的验证包括：

- `mountain-webflux` 编译通过
- `mountain-test` 编译通过
- `MountainTestApplicationTests` 在 Spring Boot 4.0.6 下执行通过
- 根项目 Reactor 构建通过

## 当前遗留项

### Mockito 动态 agent 警告

测试在 JDK 21 下可以通过，但会出现 Mockito 的动态 agent 警告。当前这不是阻塞问题，不过未来 JDK 版本可能需要把 Mockito agent 显式配置到测试启动参数中。

### 示例接口的外部依赖

示例模块中的部分接口依赖外部公开站点：

- `https://www.baidu.com`
- `http://yqyfw.online:8080`

因此联调结果会受外部网络和目标服务可用性影响。

## 升级策略说明

这次升级实际采用了“先到 Spring Boot 3，再到 Spring Boot 4”的路径：

1. 先把老版本项目迁移到 JDK 21 可编译的中间状态
2. 再完成 Spring Boot 4.0.6 的最终升级

这样做的原因是旧测试栈和旧 Spring 版本无法直接稳定支持 Java 21 class file version。

## 后续建议

- 为测试补充 Mockito agent 配置，提前规避未来 JDK 限制
- 把示例中的外部服务替换为本地 mock 或 stub，提升可重复性
- 增加对请求头、鉴权、超时、重试等能力的集成测试
- 进一步评估 `fastjson 1.x` 是否要升级到更现代、维护状态更好的 JSON 方案
