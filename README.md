# GeneSys

![GeneSys Logo v1.0](GeneSys_Logo_v1.0.png)

---

## Description

This is a project to be used as a `base` or a `cookie-cutter` if you will.

This version of the project is for building a Java Spring Boot Web and REST applications from scratch.

The project comes pre-configured incorporating the following technologies:

- Java
- Spring Boot
    - Aspect Oriented Programming (AOP)
    - Web & REST
        - Controllers
        - Filters
        - Exception Handling
        - Thymeleaf
- Maven
    - Enforcer plugin
- Logging
    - Structured Logging in JSON format
    - SLF4J
    - MDC
- Distributed Tracing
    - Sleuth
    - Zipkin

**Note:** For versions, please see the [POM file](pom.xml).

---

## SETUP

- Setting up the Maven wrapper

```sh
mvn -N io.takari:maven:wrapper
```

- Building

```
mvn clean install
```

- Running

```
mvn spring-boot:run
```

---

## NOTES

[APPENDIX A](APPENDIX_A.md)

---

# REFERENCES

> <https://spring.io/>
>
> <http://patorjk.com/software/taag/#p=display&f=Graffiti&t=Type%20Something%20>

---
