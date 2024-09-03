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

package org.easypeelsecurity.springdog.domain.ratelimit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;

import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.domain.ratelimit.model.EndpointHeader;
import org.easypeelsecurity.springdog.domain.ratelimit.model.EndpointParameter;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;
import org.easypeelsecurity.springdog.shared.dto.EndpointHeaderDto;
import org.easypeelsecurity.springdog.shared.dto.EndpointParameterDto;

/**
 * Compares the version of endpoint definitions parsed from the controller with those retrieved from the
 * database.
 * Generates a hash to identify changes between the two sets of endpoint definitions.
 * Provides methods to get the new version hash and to compare the current version with the stored version.
 *
 * @author PENEKhun
 */
public class EndpointVersionCompare {
  private static final String PROPERTY_DELIMITER = " : ";
  private static final String HASH_ALGORITHM = "SHA-256";

  private final List<EndpointDto> parsedFromController;
  private final List<Endpoint> getFromDatabase;

  /**
   * Constructor.
   *
   * @param parsedFromController endpoint definitions parsed from the controller
   * @param getFromDatabase      endpoint definitions retrieved from the database
   */
  public EndpointVersionCompare(List<EndpointDto> parsedFromController, List<Endpoint> getFromDatabase) {
    this.parsedFromController = parsedFromController;
    this.getFromDatabase = getFromDatabase;
  }

  /**
   * Compares the version of endpoint definitions parsed from the controller with those retrieved from the
   * database.
   *
   * @return {@code EndpointVersionCompareResult.FIRST_RUN} if there are no stored definitions,
   *     {@code EndpointVersionCompareResult.SAME} if the definitions are identical,
   *     {@code EndpointVersionCompareResult.DIFFERENT} if the definitions differ
   */
  public EndpointVersionCompareResult compare() {
    if (getFromDatabase == null || getFromDatabase.isEmpty()) {
      return EndpointVersionCompareResult.FIRST_RUN;
    }

    return
        generateFullHashFromDto(parsedFromController).equals(this.generateFullHashFromEntity(getFromDatabase)) ?
            EndpointVersionCompareResult.SAME : EndpointVersionCompareResult.DIFFERENT;
  }

  private String generateFullHashFromEntity(List<Endpoint> endpoints) {
    endpoints.sort(Comparator.comparing(Endpoint::getPath)
        .thenComparing(Endpoint::getHttpMethod)
        .thenComparing(Endpoint::getMethodSignature));

    StringBuilder hash = new StringBuilder();
    for (Endpoint endpoint : endpoints) {
      hash.append(endpoint.getPath())
          .append(PROPERTY_DELIMITER)
          .append(endpoint.getHttpMethod())
          .append(PROPERTY_DELIMITER)
          .append(endpoint.getMethodSignature())
          .append(PROPERTY_DELIMITER);

      endpoint.getEndpointParameters()
          .stream()
          .sorted(Comparator.comparing(EndpointParameter::getName)
              .thenComparing(EndpointParameter::getType))
          .forEach(p -> hash.append(p.getName())
              .append(PROPERTY_DELIMITER)
              .append(p.getType()));

      endpoint.getEndpointHeaders()
          .stream()
          .sorted(Comparator.comparing(EndpointHeader::getName))
          .forEach(p -> hash.append(p.getName())
              .append(PROPERTY_DELIMITER));
    }

    return generateSHA256Hex(hash.toString());
  }

  /**
   * Generates a hash from the endpoint definitions parsed from the controller.
   *
   * @param endpoints endpoint definitions parsed from the controller
   * @return the hash
   */
  public String generateFullHashFromDto(List<EndpointDto> endpoints) {
    endpoints.sort(Comparator.comparing(EndpointDto::getPath)
        .thenComparing(EndpointDto::getHttpMethod)
        .thenComparing(EndpointDto::getMethodSignature));

    StringBuilder hash = new StringBuilder();
    for (EndpointDto endpoint : endpoints) {
      hash.append(endpoint.getPath())
          .append(PROPERTY_DELIMITER)
          .append(endpoint.getHttpMethod())
          .append(PROPERTY_DELIMITER)
          .append(endpoint.getMethodSignature())
          .append(PROPERTY_DELIMITER);

      endpoint.getParameters()
          .stream()
          .sorted(Comparator.comparing(EndpointParameterDto::getName)
              .thenComparing(EndpointParameterDto::getType))
          .forEach(p -> hash.append(p.getName())
              .append(PROPERTY_DELIMITER)
              .append(p.getType()));

      endpoint.getHeaders()
          .stream()
          .sorted(Comparator.comparing(EndpointHeaderDto::getName))
          .forEach(p -> hash.append(p.getName())
              .append(PROPERTY_DELIMITER));
    }

    return generateSHA256Hex(hash.toString());
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
