# <img src="https://github.com/PENEKhun/springdog/assets/13290706/33c52782-f6b8-442a-9b6f-ae93b50376d4" align="right" width="100">springdog

[![Build Status](https://github.com/PENEKhun/springdog/actions/workflows/build-push-main.yml/badge.svg)](https://github.com/PENEKhun/springdog/actions/workflows/build-push-main.yml)
[![Codecov](https://codecov.io/gh/PENEKhun/springdog/graph/badge.svg?token=AQ1A6SAJTY)](https://codecov.io/gh/PENEKhun/springdog)
[![Maven Central](https://img.shields.io/maven-central/v/org.easypeelsecurity/springdog-starter.svg?label=Maven%20Central&color=)](https://mvnrepository.com/artifact/org.easypeelsecurity/springdog-starter)
[![JavaDoc](https://javadoc.io/badge2/org.easypeelsecurity/springdog-domain/javadoc.svg)](https://javadoc.io/doc/org.easypeelsecurity/)
[![License](https://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Add security configuration to your Spring application effortlessly by simply adding a Springdog.
**No additional code configuration is required!**

## Requirements

> ⚠️ **Requires Spring Boot 3.x**

## Quick Start

1. Add the Springdog dependency to your `build.gradle` or `pom.xml`.

    ```groovy
    dependencies {
        // Fill in the version with the latest version.
        implementation "org.easypeelsecurity:springdog-starter:${springdogVersion}"
        annotationProcessor "org.easypeelsecurity:springdog-starter:${springdogVersion}"
    }
    ```

2. Enable the Springdog agent by adding the `@SpringDogEnable` annotation to your main class.

    ```java
    
    @SpringDogEnable // Add this annotation to your main class or configuration class.
    @SpringBootApplication
    public class YourMainApplication {
      public static void main(String[] args) {
        SpringApplication.run(YourMainApplication.class, args);
      }
    }
    ```

3. Run your application and access the Springdog agent in your browser.

   ```http request
   GET http://localhost:8080/springdog/
   // Host and port may vary depending on your application configuration.
   ```

## Demo Video (KOR)

This video shows how to use Springdog in a Spring Boot application. click the image to watch the
video.
[![Demo Video](http://img.youtube.com/vi/p0M_Ad0u8M8/0.jpg)](https://youtu.be/p0M_Ad0u8M8)

## Features

### Spring-friendly Rate Limit

![Spring-friendly Rate Limit](https://github.com/user-attachments/assets/7c8d0a11-7ce4-4b28-9369-11a3b0551145)

### System & Endpoint Monitoring

![System & Endpoint Monitoring](https://github.com/user-attachments/assets/74f8dacd-0bec-496f-8f79-14c79bab460e)

The system sends email notifications in the following scenarios:

1. When the pre-defined system usage threshold is exceeded or returns to
   normal  
   <img src="https://github.com/user-attachments/assets/e9d575d3-bda9-4ade-b1e5-5a09eee0bded" width="50%">

2. When the response time of a pre-defined endpoint exceeds the specified
   threshold  
   <img src="https://github.com/user-attachments/assets/58f59637-9693-4ebf-9e01-b1768b0b2ba8" width="30%">

These notification features allow system administrators to quickly identify and respond to potential
issues.

## Properties

```yaml
springdog:
  agent:
    basePath: springdog
    username: admin
    password: admin
    externalAccess: false
```

### springdog.agent

> The Springdog agent configuration.

| Name           | Required | Description                                                                                                                                                                                                                            | Default   | Value Sets        |
|----------------|----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|-------------------|
| basePath       | x        | The base path for the agent. Used to access the springdog agent from a deployed server, such as `{{host}}/springdog/`. The basePath used with this option should never be used as the controller mapping address for your application. | springdog |
| username       | x        | The username for the Springdog agent. Empty fields are not allowed.                                                                                                                                                                    | admin     |
| password       | x        | The password for the Springdog agent. Empty fields are not allowed.                                                                                                                                                                    | admin     |
| externalAccess | x        | Whether to allow external access to the Springdog agent. If `false`, access from external IPs is not allowed.                                                                                                                          | false     | `true` or `false` |

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
More information can be found in the [CONTRIBUTING.md] file.

[CONTRIBUTING.md]: CONTRIBUTING.md

## License

This project is licensed under the terms of the [Apache 2.0] license.

[apache 2.0]: LICENSE.txt

