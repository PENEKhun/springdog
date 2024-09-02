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

1. When the [pre-defined](#springdogsystem-watch) system usage threshold is exceeded or returns to
   normal  
   <img src="https://github.com/user-attachments/assets/e9d575d3-bda9-4ade-b1e5-5a09eee0bded" width="50%">

2. When the response time of a [pre-defined](#springdogslow-response) endpoint exceeds the specified
   threshold  
   <img src="https://github.com/user-attachments/assets/58f59637-9693-4ebf-9e01-b1768b0b2ba8" width="30%">

These notification features allow system administrators to quickly identify and respond to potential
issues.

## Options

For minor configurations, you can use the following options in your application properties. For more
detailed or advanced configurations that are not available here, you can access the Springdog agent
directly.

> ⚠️ Note: To receive notifications for [system-watch](#springdogsystem-watch)
> and [slow-response](#springdogslow-response) features, the gmail
> notification option must be enabled.

```yaml
springdog:
  agent:
    basePath: springdog
    username: admin
    password: admin
    externalAccess: false
  notification:
    gmail:
      enabled: false
      recipient:
      username:
      password:
  system-watch:
    enabled: false
    cpuThreshold: 80 # percentage
    memoryThreshold: 80 # percentage
    diskThreshold: 80 # percentage
  slow-response:
    enabled: false
    threshold: 1000 # ms
```

### springdog.agent

> The Springdog agent configuration.

| Name           | Required | Description                                                                                                                                                                                                                            | Default   | Value Sets        |
|----------------|----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|-------------------|
| basePath       | x        | The base path for the agent. Used to access the springdog agent from a deployed server, such as `{{host}}/springdog/`. The basePath used with this option should never be used as the controller mapping address for your application. | springdog |
| username       | x        | The username for the Springdog agent. Empty fields are not allowed.                                                                                                                                                                    | admin     |
| password       | x        | The password for the Springdog agent. Empty fields are not allowed.                                                                                                                                                                    | admin     |
| externalAccess | x        | Whether to allow external access to the Springdog agent. If `false`, access from external IPs is not allowed.                                                                                                                          | false     | `true` or `false` |

### springdog.notification.gmail

> [system-watch](#springdogsystem-watch), [slow-response](#springdogslow-response) notifications are
> sent via Gmail.

| Name      | Required   | Description                                                                                                                       | Default | Value Sets                    |
|-----------|------------|-----------------------------------------------------------------------------------------------------------------------------------|---------|-------------------------------|
| enabled   | x          | Whether to enable Gmail notifications.                                                                                            | false   | `true` or `false`             |
| recipient | △(enabled) | The recipient's email address.                                                                                                    |         | Must be a valid email address |
| username  | △(enabled) | The Gmail username.                                                                                                               |         |
| password  | △(enabled) | The Gmail App password. details in [here](https://support.google.com/mail/thread/205453566/how-to-generate-an-app-password?hl=en) |         |

### springdog.system-watch

> Monitor the system's CPU, memory, and disk usage.
> If the usage exceeds the threshold, a notification will be sent.
>
> ⚠️ Threshold `0` means that is disabled.

| Name            | Required   | Description                                                                                      | Default | Value Sets         |
|-----------------|------------|--------------------------------------------------------------------------------------------------|---------|--------------------|
| enabled         | x          | Whether to enable system watch.                                                                  | false   | `true` or `false`  |
| cpuThreshold    | △(enabled) | The CPU usage threshold. If the CPU usage exceeds this value, a notification will be sent.       | 0.0     | `0.0 < x <= 100.0` |
| memoryThreshold | △(enabled) | The memory usage threshold. If the memory usage exceeds this value, a notification will be sent. | 0.0     | `0.0 < x <= 100.0` |
| diskThreshold   | △(enabled) | The disk usage threshold. If the disk usage exceeds this value, a notification will be sent.     | 0.0     | `0.0 < x <= 100.0` |

### springdog.slow-response

> For all endpoints, send a notification if the response time is slower than a specified number of
*milliseconds*.

| Name        | Required   | Description                                                                                        | Default | Value Sets        |
|-------------|------------|----------------------------------------------------------------------------------------------------|---------|-------------------|
| enabled     | x          | Whether to enable slow response.                                                                   | false   | `true` or `false` |
| thresholdMs | △(enabled) | The slow response threshold. If the response time exceeds this value, a notification will be sent. | 0       | `0 < x` (ms)      |

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
More information can be found in the [CONTRIBUTING.md] file.

[CONTRIBUTING.md]: CONTRIBUTING.md

## License

This project is licensed under the terms of the [Apache 2.0] license.

[apache 2.0]: LICENSE.txt

