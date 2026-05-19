# mountain

`mountain` 是一个基于 Spring WebFlux `WebClient` 的声明式 HTTP 客户端实验项目。

它的目标和 OpenFeign 类似：把远程 HTTP 接口声明成 Java 接口，通过注解描述请求路径、请求方法和参数，运行时自动生成代理对象，并返回响应式类型 `Mono` / `Flux`。

## 当前技术栈

- JDK 21
- Maven 3.9+
- Spring Boot 4.0.6
- Spring Framework 7.0.7
- Reactor 3.8.x

升级说明见 [docs/upgrade-jdk21-springboot4.md](./docs/upgrade-jdk21-springboot4.md)。

## 项目特点

- 基于 Spring WebFlux `WebClient`
- 使用 `@MountainClient` 声明远程服务地址
- 支持 Spring MVC 风格的请求注解
- 通过 `@EnableMountainClient` 自动扫描并注册客户端接口
- 原生返回 Reactor 响应式类型 `Mono` / `Flux`
- 支持通过 Spring `Environment` 解析 `@MountainClient` 中的占位符

## 模块说明

```text
mountain
├── mountain-webflux   核心库，负责注解扫描、代理创建、元数据解析和 WebClient 调用
└── mountain-test      示例工程，演示如何在 Spring Boot 中接入和调用
```

## 工作机制

项目启动时：

1. `@EnableMountainClient` 触发 `MountainClientScannerRegistrar`
2. 扫描指定包下标注了 `@MountainClient` 的接口
3. 使用 `JdkApiProxyCreator` 默认生成接口动态代理
4. 解析方法上的 `@GetMapping` / `@PostMapping` 等注解，构建请求元数据
5. 通过 `WebClientImpl` 发起 HTTP 请求并返回 `Mono` / `Flux`

## 已支持的注解

类级别：

- `@MountainClient("http://example.com")`
- `@MountainClient("${api.test.baidu}")`

方法级别：

- `@RequestMapping`
- `@GetMapping`
- `@PostMapping`
- `@PutMapping`
- `@DeleteMapping`

参数级别：

- `@PathVariable`
- `@RequestParam`
- `@RequestBody`

## 快速开始

### 1. 环境准备

项目当前要求 JDK 21。构建时建议显式指定 `JAVA_HOME`：

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) mvn test
```

### 2. 引入依赖

如果只使用核心模块，可以引入：

```xml
<dependency>
    <groupId>com.ivan</groupId>
    <artifactId>mountain-webflux</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 3. 开启扫描

```java
import cn.ivan.mountain.facorty.EnableMountainClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMountainClient
@SpringBootApplication
public class MountainTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MountainTestApplication.class, args);
    }
}
```

默认扫描启动类所在包及其子包，也可以显式指定：

```java
@EnableMountainClient(basePackage = "cn.ivan.mountain.service")
```

### 4. 定义远程服务接口

```java
import cn.ivan.mountain.annotation.MountainClient;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@MountainClient("http://yqyfw.online:8080")
public interface BasicService {

    @GetMapping("/api/rector/say")
    Mono<String> say();

    @GetMapping("/api/rector/flux")
    Flux<String> sayFluxs();

    @GetMapping("/api/rector/stream/flux")
    Flux<String> sayFluxsSteam();
}
```

如果服务地址需要从配置中读取，也可以写成：

```java
@MountainClient("${api.test.baidu}")
public interface BaiduService {

    @GetMapping("")
    Mono<String> index();
}
```

对应配置示例：

```properties
api.test.baidu=https://www.baidu.com
```

### 5. 在业务代码中直接注入

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ApiController {

    private final BasicService basicService;

    public ApiController(BasicService basicService) {
        this.basicService = basicService;
    }

    @GetMapping("/api/say")
    public Mono<String> say() {
        return basicService.say();
    }
}
```

## 运行示例工程

在项目根目录执行测试：

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) mvn test
```

启动示例应用：

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) mvn -pl mountain-test spring-boot:run
```

可参考的示例接口：

- `GET /api/say`
- `GET /api/say/flux`
- `GET /api/say/response`
- `GET /api/say/flux/stream`
- `GET /api/baidu/index`

相关代码位于：

- [BasicService.java](./mountain-test/src/main/java/cn/ivan/mountain/service/BasicService.java)
- [BaiduService.java](./mountain-test/src/main/java/cn/ivan/mountain/service/BaiduService.java)
- [ApiController.java](./mountain-test/src/main/java/cn/ivan/mountain/api/ApiController.java)

## 自定义扩展点

默认情况下，框架使用 `JdkApiProxyCreator` 生成代理，也可以在 `@EnableMountainClient` 中切换：

```java
@EnableMountainClient(proxyClass = JdkApiProxyCreator.class)
```

项目中也提供了 `CglibApiProxyCreator` 实现，可以按需替换。

默认底层客户端为 `WebClientImpl`。如果要替换，可以定义自定义 `MountainClient`，或者通过 `MountainClientBuilder` 指定实现。示例入口见 [ApiConfig.java](./mountain-test/src/main/java/cn/ivan/mountain/config/ApiConfig.java)。

## 升级后验证状态

- 已完成 `JDK 21 + Spring Boot 4.0.6` 升级
- 根项目 `mvn test` 已验证通过
- 示例工程可在 Spring Boot 4 下成功启动测试上下文

## 已知限制

当前版本更适合作为学习和实验项目，主要限制包括：

- 返回值按当前实现应使用 `Mono<T>` 或 `Flux<T>`
- `@RequestBody`、泛型返回值、复杂对象映射能力仍比较基础
- 错误处理、重试、超时、编解码扩展还比较简单
- 示例接口依赖外部公开地址，运行结果会受网络环境影响
- Mockito 在 JDK 21 下存在动态 agent 警告，但当前不影响测试通过

## 核心源码入口

- [EnableMountainClient.java](./mountain-webflux/src/main/java/cn/ivan/mountain/facorty/EnableMountainClient.java)
- [MountainClientScannerRegistrar.java](./mountain-webflux/src/main/java/cn/ivan/mountain/facorty/MountainClientScannerRegistrar.java)
- [JdkApiProxyCreator.java](./mountain-webflux/src/main/java/cn/ivan/mountain/proxy/impl/JdkApiProxyCreator.java)
- [MethodMetadataBuilder.java](./mountain-webflux/src/main/java/cn/ivan/mountain/proxy/MethodMetadataBuilder.java)
- [WebClientImpl.java](./mountain-webflux/src/main/java/cn/ivan/mountain/client/impl/WebClientImpl.java)

## 后续可改进方向

- 增加请求头、认证、拦截器和统一 tracing 支持
- 完善 `@RequestBody`、泛型返回值、集合对象处理
- 增加超时、重试、熔断和更细粒度日志
- 补齐更多单元测试和本地可重复的示例接口
- 对齐 OpenFeign 风格的开发体验
