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

  private final EndpointDto[] parsedFromController;
  private final EndpointDto[] getFromDatabase;

  /**
   * Constructor.
   *
   * @param parsedFromController endpoint definitions parsed from the controller
   * @param getFromDatabase      endpoint definitions retrieved from the database
   */
  public EndpointVersionCompare(EndpointDto[] parsedFromController, EndpointDto[] getFromDatabase) {
    this.parsedFromController = parsedFromController;
    this.getFromDatabase = getFromDatabase;
  }

  /**
   * Generates a hash representing the new version of endpoint definitions parsed from the controller.
   *
   * @return a SHA-256 hash as a string
   */
  public String getNewVersionHash() {
    return generateFullHash(parsedFromController);
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
    if (getFromDatabase == null || getFromDatabase.length == 0) {
      return EndpointVersionCompareResult.FIRST_RUN;
    }

    return generateFullHash(parsedFromController).equals(generateFullHash(getFromDatabase)) ?
        EndpointVersionCompareResult.SAME : EndpointVersionCompareResult.DIFFERENT;
  }

  private String generateFullHash(EndpointDto... endpoints) {
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

    StringBuilder hash = new StringBuilder();
    for (EndpointDto endpoint : endpoints) {
      hash.append(endpoint.getPath())
          .append(PROPERTY_DELIMITER)
          .append(endpoint.getHttpMethod())
          .append(PROPERTY_DELIMITER)
          .append(endpoint.getFqmn())
          .append(PROPERTY_DELIMITER);

      endpoint.getParameters()
          .stream()
          .sorted(Comparator.comparing(EndpointParameterDto::getName)
              .thenComparing(EndpointParameterDto::getType))
          .forEach(p -> hash.append(p.getName())
              .append(PROPERTY_DELIMITER)
              .append(p.getType()));
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
