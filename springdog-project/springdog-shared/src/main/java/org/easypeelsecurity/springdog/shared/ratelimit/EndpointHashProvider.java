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

package org.easypeelsecurity.springdog.shared.ratelimit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;

import org.easypeelsecurity.springdog.shared.util.Assert;

/**
 * Endpoint version system.
 */
public class EndpointHashProvider implements EndpointHash {

  @Override
  public String getHash(EndpointDto... endpointDto) {
    Assert.notNull(endpointDto, "EndpointDto must not be null");

    return generateSHA256Hex(endpointsToString(endpointDto));
  }

  @Override
  public String getParamHash(EndpointDto apiInfo, EndpointParameterDto... apiParams) {
    Assert.notNull(apiParams, "EndpointParameterDto must not be null");
    Assert.isTrue(apiParams.length > 0, "EndpointParameterDto must not be empty");

    return generateSHA256Hex(parametersToString(apiInfo, apiParams));
  }

  private String endpointsToString(EndpointDto... endpoints) {
    // Sort the endpoints array before processing
    Arrays.sort(endpoints, (e1, e2) -> {
      int pathCompare = e1.getPath().compareTo(e2.getPath());
      if (pathCompare != 0) {
        return pathCompare;
      }

      int methodCompare = e1.getHttpMethod().compareTo(e2.getHttpMethod());
      if (methodCompare != 0) {
        return methodCompare;
      }

      return e1.getFqmn().compareTo(e2.getFqmn());
    });

    StringBuilder sb = new StringBuilder();
    for (EndpointDto endpoint : endpoints) {
      if (endpoint.getParameters() != null) {
        sb.append(PROPERTY_DELIMITER)
            .append(
                parametersToString(endpoint, endpoint.getParameters().toArray(new EndpointParameterDto[0])));
      }
    }

    return sb.toString();
  }

  private String parametersToString(EndpointDto api, EndpointParameterDto... parameters) {
    Comparator<EndpointParameterDto> comparator = Comparator
        .comparing(EndpointParameterDto::getName)
        .thenComparing(EndpointParameterDto::getType);
    Arrays.sort(parameters, comparator);

    StringBuilder sb = new StringBuilder()
        .append(api.getPath())
        .append(PROPERTY_DELIMITER)
        .append(api.getHttpMethod())
        .append(PROPERTY_DELIMITER)
        .append(api.getFqmn());

    for (EndpointParameterDto parameter : parameters) {
      sb.append(parameter.getName())
          .append(PROPERTY_DELIMITER)
          .append(parameter.getType());
    }
    return sb.toString();
  }

  private String generateSHA256Hex(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
      byte[] encoded = digest.digest(input.getBytes());
      return bytesToHex(encoded);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(HASH_ALGORITHM + " algorithm not found", e);
    }
  }

  private String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
