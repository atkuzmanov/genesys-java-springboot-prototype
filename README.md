# GeneSys

---

## Setup

- Setting up the Maven wrapper

```sh
mvn -N io.takari:maven:wrapper
```
---

## Notes

### ASPECT LOGGING and DISTRIBUTED TRACING

This [StackOverflow question/thread](https://stackoverflow.com/questions/33744875/spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl) 
demonstrates how hard the problem of combining LOGGING with DISTRIBUTED TRACING is, especially when you are using ASPECT ORIENTED PROGRAMMING (AOP) with Java and Spring Boot.
Furthermore this thread is only about logging all requests, things get really "fun", when you start adding DISTRIBUTED TRACING etc.

While you have many individual options for just one or the other, the difficulty arises when you try to combine them and make them play nice.
I am referring to combining the following:
- AOP (Controller Advice, ResponseBodyAdvice etc.)
- Logging (cross-cutting concern => AOP)
- Filters (Request/Responce Filters etc.)
- Interceptors (Request/Responce Interceptors etc.)
- Handlers (Exception Handlers etc.)
- Servlets
- Spring/Spring Boot Configuration
- Dependencies
- Java version

As you can see because of the amount of topics, and the amount of possible approaches for each topic, 
the amount of possible successful combinations grows exponentially small. 
It becomes almost as trying to find a needle in a haystack, as when you have just fixed or made two of them play nice 
together, the third next thing you need for the next functionality breaks one of the previous two.

> References:
> <https://stackoverflow.com/questions/33744875/spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl>

---

# References

> <https://spring.io/>
>
> <http://patorjk.com/software/taag/#p=display&f=Graffiti&t=Type%20Something%20>

---
