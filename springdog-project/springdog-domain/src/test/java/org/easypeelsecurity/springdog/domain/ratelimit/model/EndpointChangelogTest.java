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

package org.easypeelsecurity.springdog.domain.ratelimit.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EndpointChangelogTest {

  @Test
  @DisplayName("Should return true when two EndpointChangelog objects are equal")
  void equalsWhenObjectsAreEqual() {
    // given
    EndpointChangelog changelog1 = new EndpointChangelog();
    changelog1.setId(1L);
    changelog1.setChangeType("ADD");
    changelog1.setDetailString("Added new API");
    changelog1.setIsResolved(false);
    changelog1.setTargetMethod("com.example.TestController.testMethod(String)");
    changelog1.setTargetMethodSignature("String testMethod(String)");
    changelog1.setTargetPath("/api/test");

    EndpointChangelog changelog2 = new EndpointChangelog();
    changelog2.setId(1L);
    changelog2.setChangeType("ADD");
    changelog2.setDetailString("Added new API");
    changelog2.setIsResolved(false);
    changelog2.setTargetMethod("com.example.TestController.testMethod(String)");
    changelog2.setTargetMethodSignature("String testMethod(String)");
    changelog2.setTargetPath("/api/test");

    // when
    boolean isEqual = changelog1.equals(changelog2);

    // then
    assertTrue(isEqual);
    assertEquals(changelog1.hashCode(), changelog2.hashCode());
  }

  @Test
  @DisplayName("Should return false when two EndpointChangelog objects are not equal")
  void equalsWhenObjectsAreNotEqual() {
    // given
    EndpointChangelog changelog1 = new EndpointChangelog();
    changelog1.setId(1L);
    changelog1.setChangeType("ADD");

    EndpointChangelog changelog2 = new EndpointChangelog();
    changelog2.setId(2L);
    changelog2.setChangeType("REMOVE");

    // when
    boolean isEqual = changelog1.equals(changelog2);

    // then
    assertFalse(isEqual);
    assertNotEquals(changelog1.hashCode(), changelog2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values and different types in equals()")
  void equalsWithNullAndDifferentTypes() {
    // given
    EndpointChangelog changelog = new EndpointChangelog();
    changelog.setId(1L);

    // when
    boolean isEqualToNull = changelog.equals(null);
    boolean isEqualToDifferentType = changelog.equals(new Object());

    // then
    assertFalse(isEqualToNull);
    assertFalse(isEqualToDifferentType);
  }

  @Test
  @DisplayName("Should correctly set and get fields")
  void setAndGetFields() {
    // given
    EndpointChangelog changelog = new EndpointChangelog();

    // when
    changelog.setId(1L);
    changelog.setChangeType("MODIFY");
    changelog.setDetailString("Modified the API");
    changelog.setIsResolved(true);
    changelog.setTargetMethod("testMethod");
    changelog.setTargetMethodSignature("testSignature");
    changelog.setTargetPath("/api/update");

    // then
    assertThat(changelog.getId()).isEqualTo(1L);
    assertThat(changelog.getChangeType()).isEqualTo("MODIFY");
    assertThat(changelog.getDetailString()).isEqualTo("Modified the API");
    assertTrue(changelog.isIsResolved());
    assertThat(changelog.getTargetMethod()).isEqualTo("testMethod");
    assertThat(changelog.getTargetMethodSignature()).isEqualTo("testSignature");
    assertThat(changelog.getTargetPath()).isEqualTo("/api/update");
  }
}
