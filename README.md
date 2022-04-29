## Annotations

### Application Level Annotations

#### @SpringBootApplication
- Convenience annotation that includes `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`

#### @EnableScheduling
- Enables support for the `@Schedule` annotation.

#### @EnableSwagger2

#### @EnableFeignClients

#### @EnableAsync

---

### Core Annotations

#### @Service

#### @Controller

#### @Component

#### @PostConstruct

#### @PreDestroy

---

### Configuration Annotations

#### @Configuration

#### @EnableConfigurationProperties

#### @EnableAutoConfiguration

#### @ConfigurationProperties

---

###  Bean Annotations

#### @Scope

#### @Bean

#### @Autowired

#### @Value

#### @BeanName

#### @Qualifier

#### @Profile

#### @Lazy

---

### Rest Annotations

#### @RestController

#### @ApiOperation

#### @ApiResponses

#### @ApiParam

#### @ApiModel

#### @ApiProperty

#### @ApiIgnore

#### @ApiError

---

### Lombok Annotations

[lombok official page](https://projectlombok.org/features/all)

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.12</version>
</dependency>
```


#### val <span style="color:orange">**F**</span>
- final local variables
```java
val map = new HashMap();
for (val entry : map.entrySet()) {}
```
#### var <span style="color:orange">**F**</span>
- local variables

#### @Getter(lazy=true) / @Setter(lazy=true) <span style="color:orange">**TF**</span>

#### @RequiredArgsConstructor <span style="color:orange">**T**</span>
- constructor for all final fields

#### @AllArgsConstructor <span style="color:orange">**T**</span>

#### @NoArgsConstructor <span style="color:orange">**T**</span>

#### @Builder <span style="color:orange">**T**</span>
- implementation of the bulder pattern

#### @ToString <span style="color:orange">**T**</span>
- definition of annotation before the class, to implement the standard toString method
```java
@ToString(exclude="f")
public class Example { }
```

#### @Singular <span style="color:orange">**F**</span>
- used for objects in a single copy (adding an item to the collection, etc.
```java
@Builder
public class Example {
  private String name;
  private int age;
  @Singular 
  private Set occupations;
}
```
#### @EqualsAndHashCode <span style="color:orange">**T**</span>
- easy creation of Equals and HashCode methods
```java
@EqualsAndHashCode(exclude={"id1", "id2"})
public class Example {}
```
#### @Value <span style="color:orange">**T**</span>
- creation of immutable classes, analogous to Data, but for immutable classes

#### @Data <span style="color:orange">**T**</span>
- generation of all utility methods, immediately replaces the `@ToString`, `@EqualsAndHashCode`, `@Getter` , `@Setter` , `@RequiredArgsConstructor` commands

#### @With <span style="color:orange">**F**</span>

#### @SneakyThrows <span style="color:orange">**M**</span>
- wrapper of checked exceptions
```java
@SneakyThrows (UnsupportedEncodingException.class)
public String utf8ToString (byte [] bytes) {
  return new String (bytes, "UTF-8");
}
```

#### @NonNull <span style="color:orange">**P**</span>
- handling variables that should not receive null

#### @Synchronized <span style="color:orange">**M**</span>

#### @cleanup <span style="color:orange">**F**</span>
- A simple definition of resources, so that they are automatically closed after the end of the code.
```java
@Cleanup InputStream in = new FileInputStream(args[0]);
@Cleanup OutputStream out = new FileOutputStream(args[1]);
```
---

### Task Execution and Scheduling

#### @EnableScheduling <span style="color:orange">**C**</span>
- Enables support for the `@Schedule` annotation.

#### @EnableAsync
- Enables support for the `@Async` annotation.

#### @Scheduled <span style="color:orange">**M**</span>
- Indicates that a method should be called on a scheduled basis.
```java
@Scheduled(fixedRate=5000)
@Scheduled(initialDelay=9000, fixedDelay=5000)
@Scheduled(fixedRateString="${delay.fixedRateInMs:21600000}")
```

#### @Async <span style="color:orange">**M**</span>
- Indicates that a method may be called asynchronously.


---
<span style="color:orange">**T**</span> Type (Class)

<span style="color:orange">**C**</span> Constructor 

<span style="color:orange">**P**</span> Parameter

<span style="color:orange">**M**</span> Method

<span style="color:orange">**F**</span> Field

---