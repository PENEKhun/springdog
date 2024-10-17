# <img src="https://github.com/PENEKhun/springdog/assets/13290706/33c52782-f6b8-442a-9b6f-ae93b50376d4" align="right" width="100">springdog

[![Build Status](https://github.com/PENEKhun/springdog/actions/workflows/build-push-main.yml/badge.svg)](https://github.com/PENEKhun/springdog/actions/workflows/build-push-main.yml)
[![Codecov](https://codecov.io/gh/PENEKhun/springdog/graph/badge.svg?token=AQ1A6SAJTY)](https://codecov.io/gh/PENEKhun/springdog)
[![Maven Central](https://img.shields.io/maven-central/v/org.easypeelsecurity/springdog-starter.svg?label=Maven%20Central&color=)](https://mvnrepository.com/artifact/org.easypeelsecurity/springdog-starter)
[![JavaDoc](https://javadoc.io/badge2/org.easypeelsecurity/springdog-domain/javadoc.svg)](https://javadoc.io/doc/org.easypeelsecurity/)
[![License](https://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Enhance your software's security and stability with a SaaS library by simply adding a single
dependency—no need for additional infrastructure setup or complex code changes!

## Requirements

> ⚠️ **Requires Java 17+ and Spring Boot 3.x**

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
    
    @SpringDogEnable // Add this annotation to your main class or bean class.
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

## Features

### Spring-friendly Rate Limit

Easily limit the request rate to your application endpoints to protect resources.

![Spring-friendly Rate Limit](https://github.com/user-attachments/assets/7c8d0a11-7ce4-4b28-9369-11a3b0551145)

### System & Endpoint Monitoring & Notification

Monitor system resource usage and endpoint performance in real-time, and receive email notifications
when thresholds are exceeded, allowing for quick responses to potential issues.

- **Endpoint Monitoring:**

  You can monitor the request volume and average response time for each endpoint.

  ![Endpoint Monitoring](https://github.com/user-attachments/assets/bc753cfa-e58c-4eda-a477-1b9cead5ded7)

- **System Usage Monitoring:**

  Monitor system usage as shown below:

  ![SystemWatch](https://github.com/user-attachments/assets/f8a0bae9-3022-4e6c-817f-e5f268aa14f7)

- **Email Notifications:**

  Receive email notifications when the thresholds set in the monitoring exceed or return to normal.
  To enable this feature, you need to activate SMTP using a Gmail account.

  ![Enable SMTP](https://github.com/user-attachments/assets/e53f70c9-5dfc-4515-af6b-6de6c2d77545)

You can also customize the email templates.

- Edit Email Template
  ![Email Template](https://github.com/user-attachments/assets/4b074002-bed0-42d9-a1fb-4172b3268bd8)
- Email Template Preview
  ![Email Template Preview](https://github.com/user-attachments/assets/1e23dc24-8b7c-41a8-8d6b-bc2af59046c9)

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
| basePath       | x        | The base path for the agent. Used to access the Springdog agent from a deployed server, such as `{{host}}/springdog/`. The basePath used with this option should never be used as the controller mapping address for your application. | springdog |
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

