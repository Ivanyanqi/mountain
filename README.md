# mountain

`mountain` 是一个基于 Spring WebFlux `WebClient` 的声明式 HTTP 客户端实验项目。

它的目标和 OpenFeign 类似：把远程 HTTP 接口声明成 Java 接口，通过注解描述请求路径、请求方法和参数，运行时自动生成代理对象，并返回响应式类型 `Mono` / `Flux`。

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

## 当前实现机制

项目启动时：

1. `@EnableMountainClient` 触发 `MountainClientScannerRegistrar`
2. 扫描指定包下标注了 `@MountainClient` 的接口
3. 使用 `JdkApiProxyCreator`（默认）为接口生成动态代理
4. 解析方法上的 `@GetMapping` / `@PostMapping` 等注解生成请求元数据
5. 底层通过 `WebClient` 发起 HTTP 请求并返回 `Mono` / `Flux`

## 已支持的注解和能力

### 类级别

- `@MountainClient("http://example.com")`
- `@MountainClient("${api.test.baidu}")`

### 方法级别

- `@RequestMapping`
- `@GetMapping`
- `@PostMapping`
- `@PutMapping`
- `@DeleteMapping`

### 参数级别

- `@PathVariable`
- `@RequestParam`
- `@RequestBody`

## 环境要求

- JDK 8
- Maven 3.6+
- Spring Boot 2.3.x / Spring WebFlux 5.2.x

## 快速开始

### 1. 引入依赖

如果你只使用核心模块，可以引入：

```xml
<dependency>
    <groupId>com.ivan</groupId>
    <artifactId>mountain-webflux</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 开启扫描

在 Spring Boot 启动类或配置类上启用：

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

### 3. 定义远程服务接口

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

### 4. 在业务代码中直接注入

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

## 自定义代理创建方式

`@EnableMountainClient` 提供了 `proxyClass` 参数，默认使用 `JdkApiProxyCreator`：

```java
@EnableMountainClient(proxyClass = JdkApiProxyCreator.class)
```

项目中也提供了 `CglibApiProxyCreator` 实现，可以按需替换。

## 自定义底层客户端

默认情况下，框架内部会使用 `WebClientImpl` 发起请求。

如果你想替换默认客户端，可以定义一个 `MountainClient` Bean，或者通过 `MountainClientBuilder` 指定自定义实现。示例工程中的 [ApiConfig.java](./mountain-test/src/main/java/cn/ivan/mountain/config/ApiConfig.java) 给出了相关入口。

## 运行示例工程

### 启动

在项目根目录执行：

```bash
mvn test
```

或启动示例应用：

```bash
mvn -pl mountain-test spring-boot:run
```

### 可参考的示例接口

- `GET /api/say`
- `GET /api/say/flux`
- `GET /api/say/response`
- `GET /api/say/flux/stream`
- `GET /api/baidu/index`

相关代码位于：

- [BasicService.java](./mountain-test/src/main/java/cn/ivan/mountain/service/BasicService.java)
- [BaiduService.java](./mountain-test/src/main/java/cn/ivan/mountain/service/BaiduService.java)
- [ApiController.java](./mountain-test/src/main/java/cn/ivan/mountain/api/ApiController.java)

## 已知限制

当前版本更适合作为学习和实验项目，主要限制包括：

- 返回值按当前实现应使用 `Mono<T>` 或 `Flux<T>`
- `@RequestBody` 当前按响应式请求体处理，实际传入对象时需要结合代码实现确认
- 仅覆盖了基础的请求注解与参数解析场景
- 错误处理、重试、超时、编解码扩展等能力还比较基础
- 目前示例依赖外部公开地址，演示结果会受网络环境影响

## 核心源码入口

- [EnableMountainClient.java](./mountain-webflux/src/main/java/cn/ivan/mountain/facorty/EnableMountainClient.java)
- [MountainClientScannerRegistrar.java](./mountain-webflux/src/main/java/cn/ivan/mountain/facorty/MountainClientScannerRegistrar.java)
- [JdkApiProxyCreator.java](./mountain-webflux/src/main/java/cn/ivan/mountain/proxy/impl/JdkApiProxyCreator.java)
- [MethodMetadataBuilder.java](./mountain-webflux/src/main/java/cn/ivan/mountain/proxy/MethodMetadataBuilder.java)
- [WebClientImpl.java](./mountain-webflux/src/main/java/cn/ivan/mountain/client/impl/WebClientImpl.java)

## 后续可改进方向

- 支持请求头、拦截器和统一认证
- 完善 `@RequestBody` / 泛型返回值 / 集合对象处理
- 增加超时、重试、熔断和日志追踪能力
- 增加更完整的单元测试与示例场景
- 对齐 OpenFeign 风格的开发体验
