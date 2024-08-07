# <img src="https://github.com/PENEKhun/springdog/assets/13290706/33c52782-f6b8-442a-9b6f-ae93b50376d4" align="right" width="100">Springdog

[![Build Status](https://github.com/PENEKhun/springdog/actions/workflows/build-push-main.yml/badge.svg)](https://github.com/PENEKhun/springdog/actions/workflows/build-push-main.yml)
[![Codecov](https://codecov.io/gh/PENEKhun/springdog/graph/badge.svg?token=AQ1A6SAJTY)](https://codecov.io/gh/PENEKhun/springdog)
[![Maven Central](https://img.shields.io/maven-central/v/org.easypeelsecurity/springdog-starter.svg?label=Maven%20Central&color=)](https://mvnrepository.com/artifact/org.easypeelsecurity/springdog-starter)
[![JavaDoc](https://javadoc.io/badge2/org.easypeelsecurity/springdog-starter/javadoc.svg)](https://javadoc.io/doc/org.easypeelsecurity/springdog-starter)
[![License](https://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Add security configuration to your Spring application effortlessly by simply adding a Springdog.
**No additional code configuration is required!**

> Since it's still in beta, the software currently has limited features. If you're interested in
> following the project's progress, please press the ⭐ button to stay updated.

## Requirements

> ⚠️ **Requires Spring Boot 3.x**

## Dependencies

It hasn't been deployed yet, please be patient.

<!-- ```groovy
implementation "org.easypeelsecurity:springdog-starter:${springdogVersion}"
annotationProcessor "org.easypeelsecurity:springdog-starter:${springdogVersion}"
``` -->

## Overview

TODO: fill with screenshots.

## Options

For detailed configurations, you can use the `springdog Dashboard`, and for other minor configurations, you can use the following.

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

| Name           | Required | Description                                                                                                                                                                                                                           | Default   | Value Sets        |
|----------------|----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|-------------------|
| basePath       | x        | The base path for the agent. Used to access the springdog agent from a deployed server, such as `{{host}}/springdog`. The basePath used with this option should never be used as the controller mapping address for your application. | springdog |
| username       | x        | The username for the Springdog agent. Empty fields are not allowed.                                                                                                                                                                   | admin     |
| password       | x        | The password for the Springdog agent. Empty fields are not allowed.                                                                                                                                                                   | admin     |
| externalAccess | x        | Whether to allow external access to the Springdog agent. If `false`, access from external IPs is not allowed.                                                                                                                         | false     | `true` or `false` |

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
> ⚠️ To receive notifications, [this](#springdognotificationgmail) option must be enabled.

| Name            | Required   | Description                                                                                      | Default | Value Sets         |
|-----------------|------------|--------------------------------------------------------------------------------------------------|---------|--------------------|
| enabled         | x          | Whether to enable system watch.                                                                  | false   | `true` or `false`  |
| cpuThreshold    | △(enabled) | The CPU usage threshold. If the CPU usage exceeds this value, a notification will be sent.       | 0.0     | `0.0 < x <= 100.0` |
| memoryThreshold | △(enabled) | The memory usage threshold. If the memory usage exceeds this value, a notification will be sent. | 0.0     | `0.0 < x <= 100.0` |
| diskThreshold   | △(enabled) | The disk usage threshold. If the disk usage exceeds this value, a notification will be sent.     | 0.0     | `0.0 < x <= 100.0` |

### springdog.slow-response

> For all endpoints, send a notification if the response time is slower than a specified number of
*milliseconds*.
>
> ⚠️ To receive notifications, [this](#springdognotificationgmail) option must be enabled.

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

