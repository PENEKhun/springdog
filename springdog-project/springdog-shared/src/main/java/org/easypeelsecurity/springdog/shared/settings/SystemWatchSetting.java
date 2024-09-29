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

package org.easypeelsecurity.springdog.shared.settings;

import lombok.Getter;
import lombok.Setter;

/**
 * System watch setting.
 */
@Getter
@Setter
public class SystemWatchSetting {
  private boolean isEnabled;
  private double cpuThreshold = 80.0;
  private double memoryThreshold = 80.0;
  private double diskThreshold = 80.0;

  /**
   * Validate properties.
   */
  protected void validate() {
    if (isEnabled) {
      if (cpuThreshold < 0 || cpuThreshold > 100) {
        throw new IllegalArgumentException("CPU threshold must be between 0 and 100");
      }
      if (memoryThreshold < 0 || memoryThreshold > 100) {
        throw new IllegalArgumentException("Memory threshold must be between 0 and 100");
      }
      if (diskThreshold < 0 || diskThreshold > 100) {
        throw new IllegalArgumentException("Disk threshold must be between 0 and 100");
      }
    }
  }
}
