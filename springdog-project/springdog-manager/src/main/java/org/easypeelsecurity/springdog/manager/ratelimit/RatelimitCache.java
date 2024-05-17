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

package org.easypeelsecurity.springdog.manager.ratelimit;

import java.time.LocalDateTime;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Ratelimit cache instance.
 */
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public final class RatelimitCache {

  private RatelimitCache() {
  }

  /**
   * Call when access pages.
   *
   * @param requestHashed request information hashed
   * @param ldt           now time
   */
  public static LocalDateTime[] addTimestamp(String requestHashed, LocalDateTime ldt) {
    LocalDateTime[] before = getAccessHistoryInstance().getIfPresent(requestHashed);
    LocalDateTime[] after;
    if (before != null) {
      after = new LocalDateTime[before.length + 1];
      System.arraycopy(before, 0, after, 0, before.length);
      after[before.length] = ldt;
    } else {
      after = new LocalDateTime[] {ldt};
    }

    getAccessHistoryInstance().put(requestHashed, after);
    return after;
  }

  public static void ban(String requestHashed, LocalDateTime banUntil) {
    getAccessHistoryInstance().invalidate(requestHashed);
    getBanHistoryInstance().put(requestHashed, banUntil);
  }

  public static boolean checkBan(String requestHashed, LocalDateTime now) {
    LocalDateTime banTime = getBanHistoryInstance().getIfPresent(requestHashed);
    if (banTime != null) {
      if (banTime.isBefore(now)) {
        getBanHistoryInstance().invalidate(requestHashed);
        return false;
      }

      return true;
    }

    return false;
  }

  private static Cache<String, LocalDateTime[]> getAccessHistoryInstance() {
    return AccessHistoryHolder.INSTANCE;
  }

  private static Cache<String, LocalDateTime> getBanHistoryInstance() {
    return BanHistoryHolder.INSTANCE;
  }

  private static final class AccessHistoryHolder {

    private static final Cache<String, LocalDateTime[]> INSTANCE = Caffeine.newBuilder().build();
  }

  private static final class BanHistoryHolder {

    private static final Cache<String, LocalDateTime> INSTANCE = Caffeine.newBuilder().build();
  }
}
