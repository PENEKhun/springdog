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

package org.easypeelsecurity.springdog.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ViewStructuresTests {

  @ParameterizedTest
  @EnumSource(ViewStructures.class)
  void allStaticResourcesAreExist(ViewStructures item) {
    // given
    String filePath = item.getResourcePath();

    // when & then
    File file = new File("src/main/resources" + filePath);
    assertTrue(file.exists());
  }


  @Test
  void allUrisAreApplyOnce() {
    // given
    HashSet<String> uris = new HashSet<>();
    for (ViewStructures item : ViewStructures.values()) {
      uris.add(item.getUrlPath());
    }

    // when & then
    assertEquals(uris.size(), ViewStructures.values().length);
  }

  @Test
  void allStaticResourcesAreApplyOnce() {
    // given
    HashSet<String> filePaths = new HashSet<>();
    for (ViewStructures item : ViewStructures.values()) {
      filePaths.add(item.getResourcePath());
    }

    // when & then
    assertEquals(filePaths.size(), ViewStructures.values().length);
  }

}