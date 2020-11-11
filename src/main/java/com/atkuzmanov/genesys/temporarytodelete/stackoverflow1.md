<https://stackoverflow.com/questions/64786024/the-dreaded-java-springboot-app-not-connecting-to-mysql-with-docker-compose-java>

---

```
1 Answer

1


To fix it you just need to change parameter spring.datasource.jdbc-url to spring.datasource.url and connection string to jdbc:mysql://genesysmysql:3306/db_example?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true. Your connection string has an error.

share  edit  follow  flag 
answered 22 mins ago

Naitonium
4511 silver badge55 bronze badges
You sir, are absolutely correct, this fixed it, thank you ever so much! I guess I was looking at it for so long, I missed it. Now the only message which I am getting is genesysmysql    | mbind: Operation not permitted when the app starts up, any idea why? – atkuzmanov 4 mins ago    Edit   
1

@atkuzmanov try to look here https://stackoverflow.com/questions/55559386/how-to-fix-mbind-operation-not-permitted-in-mysql-error-log – Naitonium 1 min ago

cap_add: - SYS_NICE  # CAP_SYS_NICE did the trick, thank you again!! ( : – atkuzmanov just now   Edit   
```

---

The dreaded Java SpringBoot app not connecting to MySQL with Docker-compose java.net.ConnectException: Connection refused

Hi Folks,

I have been struggling with the following issue - the dreaded Java SpringBoot app not connecting to MySQL with docker compose exceptions:

**com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure** 

**JDBCConnectionException: Unable to open JDBC Connection for DDL execution** 

**java.net.ConnectException: Connection refused**

*The app works fine on its own, but it can't seem to connect to mysql once I get it in Docker.
I am going out of my mind as I think I have all parameters correct, so any help is greatly appreciated! (:*

- Platform:
```text
MacOS Mojave 10.14.6
---
Docker version 19.03.13, build 4484c46d9d
docker-compose version 1.27.4, build 40524192
---
mysql  Ver 8.0.21 for osx10.14 on x86_64 (Homebrew)
---
openjdk 11.0.8 2020-07-14
OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.8+10)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.8+10, mixed mode)
---
Spring CLI v2.3.4.RELEASE
---
Apache Maven 3.5.4 (1edded0938998edf8bf061f1ceb3cfdeccf443fe; 2018-06-17T21:33:14+03:00)
Maven home: /usr/local/Cellar/maven@3.5/3.5.4_1/libexec
Java version: 11.0.8, vendor: AdoptOpenJDK, runtime: /Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home
Default locale: en_GB, platform encoding: UTF-8
OS name: "mac os x", version: "10.14.6", arch: "x86_64", family: "mac"
```

***Here is the whole codebase: [Codebase][1]***


My docker-compose.yml (The commented out stuff is things I tried):
```yaml
version: "3.8"
services:
  genesysmysql:
    container_name: genesysmysql
#    container_name: genesysmysql_container
    image: "mysql:8.0.21"
    restart: always
    environment:
      - MYSQL_ROOT_PASSWORD=root
      - MYSQL_DATABASE=db_example
      - MYSQL_USER=springuser
      - MYSQL_PASSWORD=ThePassword
    ports:
      - 3306:3306
    volumes:
      - ./setup.sql:/docker-entrypoint-initdb.d/setup.sql:ro
    healthcheck:
      test: [ "CMD", "mysqladmin" ,"ping", "-h", "localhost" ]
      timeout: 20s
      retries: 20
  genesys:
    build: .
    ports:
      - 8080:8080
    depends_on:
      genesysmysql:
        condition: service_healthy
    environment:
      - spring.datasource.jdbc-url=jdbc:genesysmysql://db_example:3306?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true
#      - spring.datasource.jdbc-url=jdbc:genesysmysql://db_example:3306?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true
      - spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
#      - spring.datasource.username=root
#      - spring.datasource.password=root
#      - spring.datasource.username=springuser
#      - spring.datasource.password=ThePassword
#      - spring.jpa.hibernate.ddl-auto=create-drop
      - spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
#      - spring.data.jpa.repositories.enabled=true
#      - spring.jpa.database-platform=org.hibernate.dialect.MySQL5Dialect
#      - spring.jpa.generate-ddl=true
```

My setup.sql script:
```sh
CREATE DATABASE db_example;
CREATE USER 'springuser'@'%' IDENTIFIED BY 'ThePassword';
GRANT ALL ON db_example.* to 'springuser'@'%';
GRANT ALL ON db_example.* TO 'springuser'@'localhost';
GRANT ALL ON db_example.* TO 'springuser'@'genesysmysql_container';
GRANT ALL ON db_example.* TO 'springuser'@'genesysmysql';
```

My dockerfile:
```sh
FROM adoptopenjdk/openjdk11:alpine
MAINTAINER  atkuzmanov <https://github.com/atkuzmanov>
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

My application.properties file:
```sh
### Spring properties
spring.application.name=@project.name@
#spring.output.ansi.console-available=true
#server.port=8080
spring.output.ansi.enabled=ALWAYS

### Spring banner properties
#spring.banner.location=classpath:banner.txt
#spring.main.banner-mode=off
info.app.name=@project.name@
info.app.version=@project.version@
info.app.url=@project.url@

### Spring Actuator properties
## Do not expose or enable the 'shutdown' option in a public production application.
#management.endpoint.shutdown.enabled=true
management.endpoints.web.exposure.include=health,info,httptrace

### Spring Devtools properties
#spring.devtools.add-properties=false
#spring.devtools.restart.log-condition-evaluation-delta=false
#spring.devtools.restart.enabled=false
#spring.devtools.livereload.enabled=false

### Spring logging properties
## Change logging levels as appropriate when debugging
logging.level.=WARN
logging.level.customLogbackLevel=INFO
logging.level.org.springframework=WARN
logging.level.org.springframework.web.servlet.=ERROR
logging.level.org.hibernate=ERROR
logging.level.org.apache.catalina.core.ContainerBase.=WARN

### Spring Distributed Tracing properties
## Zipkin properties
spring.zipkin.enabled=true
spring.zipkin.base-url=http://127.0.0.1:9411/
## Sleuth properties
spring.sleuth.enabled=true
spring.sleuth.sampler.percentage=0.5
#spring.sleuth.web.skipPattern=(^cleanup.*|.+favicon.*)

### Spring Data properties
## These are just to get things started, should be changed afterwards.
#spring.jpa.hibernate.ddl-auto=none
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/db_example?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=springuser
spring.datasource.password=ThePassword
#spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5

### Spring Open Session properties
### Warning message by default:
## "spring.jpa.open-in-view is enabled by default.
## Therefore, database queries may be performed during view rendering.
## Explicitly configure spring.jpa.open-in-view to disable this warning"
## See:
## RE: spring.jpa.open-in-view Warning
## - <https://stackoverflow.com/questions/30549489/what-is-this-spring-jpa-open-in-view-true-property-in-spring-boot>
## - <https://www.baeldung.com/spring-open-session-in-view>
spring.jpa.open-in-view=true

### Spring Thymeleaf properties
spring.thymeleaf.cache=false
```

Here are the exception messages:

- Exception 1:
[Exception 1][2]

- Eception 2:
[Exception 2][3]

- Exception 3:
[Exception 3][4]

- Exception 4, which encapsulates all of them:
[Exception 4][5]
```json
{
  "app": "GeneSys",
  "logMsgId": "f7b6c3cc-0dce-4eb6-b6c3-cc0dce1eb69c",
  "@timestamp": "2020-11-11T11:20:31.822Z",
  "@version": "1",
  "logger": "org.springframework.boot.SpringApplication",
  "level": "ERROR",
  "thread": "main",
  "stackTrace": "org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'timestampRepository' defined in com.atkuzmanov.genesys.dao.TimestampRepository defined in @EnableJpaRepositories declared on JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration: Cannot resolve reference to bean 'jpaMappingContext' while setting bean property 'mappingContext'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'jpaMappingContext': Invocation of init method failed; nested exception is javax.persistence.PersistenceException: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.exception.JDBCConnectionException: Unable to open JDBC Connection for DDL execution
	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveReference(BeanDefinitionValueResolver.java:342)
	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveValueIfNecessary(BeanDefinitionValueResolver.java:113)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyPropertyValues(AbstractAutowireCapableBeanFactory.java:1697)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.populateBean(AbstractAutowireCapableBeanFactory.java:1442)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:593)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:516)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:324)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:322)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:202)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeansOfType(DefaultListableBeanFactory.java:624)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeansOfType(DefaultListableBeanFactory.java:612)
	at org.springframework.data.repository.config.DeferredRepositoryInitializationListener.onApplicationEvent(DeferredRepositoryInitializationListener.java:51)
	at org.springframework.data.repository.config.DeferredRepositoryInitializationListener.onApplicationEvent(DeferredRepositoryInitializationListener.java:36)
	at org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(SimpleApplicationEventMulticaster.java:172)
	at org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:165)
	at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:139)
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:404)
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:361)
	at org.springframework.context.support.AbstractApplicationContext.finishRefresh(AbstractApplicationContext.java:898)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:554)
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:143)
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:758)
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:750)
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:397)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:315)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1237)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1226)
	at com.atkuzmanov.genesys.GenesysApplication.main(GenesysApplication.java:15)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.springframework.boot.loader.MainMethodRunner.run(MainMethodRunner.java:49)
	at org.springframework.boot.loader.Launcher.launch(Launcher.java:107)
	at org.springframework.boot.loader.Launcher.launch(Launcher.java:58)
	at org.springframework.boot.loader.JarLauncher.main(JarLauncher.java:88)
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'jpaMappingContext': Invocation of init method failed; nested exception is javax.persistence.PersistenceException: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.exception.JDBCConnectionException: Unable to open JDBC Connection for DDL execution
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1794)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:594)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:516)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:324)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:322)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:202)
	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveReference(BeanDefinitionValueResolver.java:330)
	... 36 common frames omitted
Caused by: javax.persistence.PersistenceException: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.exception.JDBCConnectionException: Unable to open JDBC Connection for DDL execution
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:403)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:834)
Caused by: org.hibernate.exception.JDBCConnectionException: Unable to open JDBC Connection for DDL execution
	at org.hibernate.exception.internal.SQLStateConversionDelegate.convert(SQLStateConversionDelegate.java:112)
	at org.hibernate.exception.internal.StandardSQLExceptionConverter.convert(StandardSQLExceptionConverter.java:42)
	at org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert(SqlExceptionHelper.java:113)
	at org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert(SqlExceptionHelper.java:99)
	at org.hibernate.resource.transaction.backend.jdbc.internal.DdlTransactionIsolatorNonJtaImpl.getIsolatedConnection(DdlTransactionIsolatorNonJtaImpl.java:69)
	at org.hibernate.tool.schema.internal.exec.ImprovedExtractionContextImpl.getJdbcConnection(ImprovedExtractionContextImpl.java:60)
	at org.hibernate.tool.schema.internal.exec.ImprovedExtractionContextImpl.getJdbcDatabaseMetaData(ImprovedExtractionContextImpl.java:67)
	at org.hibernate.tool.schema.extract.internal.InformationExtractorJdbcDatabaseMetaDataImpl.getTables(InformationExtractorJdbcDatabaseMetaDataImpl.java:333)
	at org.hibernate.tool.schema.extract.internal.DatabaseInformationImpl.getTablesInformation(DatabaseInformationImpl.java:120)
	at org.hibernate.tool.schema.internal.GroupedSchemaMigratorImpl.performTablesMigration(GroupedSchemaMigratorImpl.java:65)
	at org.hibernate.tool.schema.internal.AbstractSchemaMigrator.performMigration(AbstractSchemaMigrator.java:207)
	at org.hibernate.tool.schema.internal.AbstractSchemaMigrator.doMigration(AbstractSchemaMigrator.java:114)
	at org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator.performDatabaseAction(SchemaManagementToolCoordinator.java:184)
	at org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator.process(SchemaManagementToolCoordinator.java:73)
	at org.hibernate.internal.SessionFactoryImpl.<init>(SessionFactoryImpl.java:316)
	at org.hibernate.boot.internal.SessionFactoryBuilderImpl.build(SessionFactoryBuilderImpl.java:469)
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.build(EntityManagerFactoryBuilderImpl.java:1259)
	at org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.createContainerEntityManagerFactory(SpringHibernateJpaPersistenceProvider.java:58)
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.createNativeEntityManagerFactory(LocalContainerEntityManagerFactoryBean.java:365)
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:391)
	... 4 common frames omitted
Caused by: com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure

The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.
	at com.mysql.cj.jdbc.exceptions.SQLError.createCommunicationsException(SQLError.java:174)
	at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:64)
	at com.mysql.cj.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:836)
	at com.mysql.cj.jdbc.ConnectionImpl.<init>(ConnectionImpl.java:456)
	at com.mysql.cj.jdbc.ConnectionImpl.getInstance(ConnectionImpl.java:246)
	at com.mysql.cj.jdbc.NonRegisteringDriver.connect(NonRegisteringDriver.java:197)
	at com.zaxxer.hikari.util.DriverDataSource.getConnection(DriverDataSource.java:138)
	at com.zaxxer.hikari.pool.PoolBase.newConnection(PoolBase.java:358)
	at com.zaxxer.hikari.pool.PoolBase.newPoolEntry(PoolBase.java:206)
	at com.zaxxer.hikari.pool.HikariPool.createPoolEntry(HikariPool.java:477)
	at com.zaxxer.hikari.pool.HikariPool.checkFailFast(HikariPool.java:560)
	at com.zaxxer.hikari.pool.HikariPool.<init>(HikariPool.java:115)
	at com.zaxxer.hikari.HikariDataSource.getConnection(HikariDataSource.java:112)
	at org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl.getConnection(DatasourceConnectionProviderImpl.java:122)
	at org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator$ConnectionProviderJdbcConnectionAccess.obtainConnection(JdbcEnvironmentInitiator.java:180)
	at org.hibernate.resource.transaction.backend.jdbc.internal.DdlTransactionIsolatorNonJtaImpl.getIsolatedConnection(DdlTransactionIsolatorNonJtaImpl.java:43)
	... 19 common frames omitted
Caused by: com.mysql.cj.exceptions.CJCommunicationsException: Communications link failure

The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.
	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:490)
	at com.mysql.cj.exceptions.ExceptionFactory.createException(ExceptionFactory.java:61)
	at com.mysql.cj.exceptions.ExceptionFactory.createException(ExceptionFactory.java:105)
	at com.mysql.cj.exceptions.ExceptionFactory.createException(ExceptionFactory.java:151)
	at com.mysql.cj.exceptions.ExceptionFactory.createCommunicationsException(ExceptionFactory.java:167)
	at com.mysql.cj.protocol.a.NativeSocketConnection.connect(NativeSocketConnection.java:91)
	at com.mysql.cj.NativeSession.connect(NativeSession.java:144)
	at com.mysql.cj.jdbc.ConnectionImpl.connectOneTryOnly(ConnectionImpl.java:956)
	at com.mysql.cj.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:826)
	... 32 common frames omitted
Caused by: java.net.ConnectException: Connection refused (Connection refused)
	at java.base/java.net.PlainSocketImpl.socketConnect(Native Method)
	at java.base/java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:399)
	at java.base/java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:242)
	at java.base/java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:224)
	at java.base/java.net.SocksSocketImpl.connect(SocksSocketImpl.java:403)
	at java.base/java.net.Socket.connect(Socket.java:609)
	at com.mysql.cj.protocol.StandardSocketFactory.connect(StandardSocketFactory.java:155)
	at com.mysql.cj.protocol.a.NativeSocketConnection.connect(NativeSocketConnection.java:65)
	... 35 common frames omitted
",
  "message": "Application run failed",
  "method": "reportFailure",
  "class": "org.springframework.boot.SpringApplication"
}
```


  [1]: https://github.com/atkuzmanov/genesys-java-springboot-docker
  [2]: https://github.com/atkuzmanov/genesys-java-springboot-docker/blob/main/src/main/java/com/atkuzmanov/genesys/temporarytodelete/json1.json
  [3]: https://github.com/atkuzmanov/genesys-java-springboot-docker/blob/main/src/main/java/com/atkuzmanov/genesys/temporarytodelete/json2.json
  [4]: https://github.com/atkuzmanov/genesys-java-springboot-docker/blob/main/src/main/java/com/atkuzmanov/genesys/temporarytodelete/json3.json
  [5]: https://github.com/atkuzmanov/genesys-java-springboot-docker/blob/main/src/main/java/com/atkuzmanov/genesys/temporarytodelete/json4.json