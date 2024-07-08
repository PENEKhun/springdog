/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.easypeelsecurity.springdog.shared.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.mock.web.MockHttpServletRequest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class IpAddressUtilTest {

  @ParameterizedTest
  @CsvSource({
      "X-Forwarded-For",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_CLIENT_IP",
      "HTTP_X_FORWARDED_FOR"
  })
  void getClientIpTest(String headerName) {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    String expected = "220.123.237.103";
    request.addHeader(headerName, expected);

    // when
    String actual = IpAddressUtil.getClientIp(request);

    // then
    assertThat(actual).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({
      "127.0.0.1, true",
      "0:0:0:0:0:0:0:1, true",
      "::1, true",
      "220.123.237.103, false",
      "2001:0db8:85a3:0000:0000:8a2e:0370:7334, false"
  })
  void isLocalTest(String ip, boolean expected) {
    // when
    boolean actual = IpAddressUtil.isLocal(ip);

    // then
    assertThat(actual).isEqualTo(expected);
  }
}
