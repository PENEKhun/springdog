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

import org.easypeelsecurity.springdog.shared.dto.EndpointDto;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Ratelimit cache instance.
 *
 * @author PENEKhun
 */
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public final class RatelimitCache {

  private RatelimitCache() {
  }

  /**
   * Add timestamp(access time) at the local database. and return the recent access histories.
   *
   * @param requestHashed request information hashed
   * @param nowTime       now time
   * @return access histories
   */
  private static LocalDateTime[] addTimestamp(String requestHashed, LocalDateTime nowTime) {
    LocalDateTime[] before = getAccessHistoryInstance().getIfPresent(requestHashed);
    LocalDateTime[] after;
    if (before != null) {
      after = new LocalDateTime[before.length + 1];
      System.arraycopy(before, 0, after, 0, before.length);
      after[before.length] = nowTime;
    } else {
      after = new LocalDateTime[] {nowTime};
    }

    getAccessHistoryInstance().put(requestHashed, after);
    return after;
  }

  public static void ban(String requestHashed, LocalDateTime banUntil) {
    getAccessHistoryInstance().invalidate(requestHashed);
    getBanHistoryInstance().put(requestHashed, banUntil);
  }

  public static void clearCaches() {
    getAccessHistoryInstance().invalidateAll();
    getBanHistoryInstance().invalidateAll();
  }

  /**
   * When this method called, add access timestamp to the local database. and check if the request is banned or
   * not by the ratelimit rule.
   *
   * @param requestHashed request information hashed
   * @param endpoint      endpoint information
   * @param nowTime       now time
   * @return true if the request is banned, false otherwise
   */
  public static synchronized boolean isBannedRequest(String requestHashed, EndpointDto endpoint,
      LocalDateTime nowTime) {
    if (checkAlreadyBanned(requestHashed, nowTime)) {
      return true;
    }

    LocalDateTime[] timestamps = RatelimitCache.addTimestamp(requestHashed, nowTime);
    int count = 0;
    int timeFrameSeconds = endpoint.getRuleTimeLimitInSeconds();
    for (LocalDateTime timestamp : timestamps) {
      if (timestamp.isAfter(nowTime.minusSeconds(timeFrameSeconds))) {
        count++;
      }
    }

    if (count > endpoint.getRuleRequestLimitCount()) {
      int banTimeSeconds =
          endpoint.isRulePermanentBan() ? Integer.MAX_VALUE : endpoint.getRuleBanTimeInSeconds();
      RatelimitCache.ban(requestHashed, nowTime.plusSeconds(banTimeSeconds));
      return true;
    } else {
      return false;
    }
  }

  private static boolean checkAlreadyBanned(String requestHashed, LocalDateTime nowTime) {
    LocalDateTime banExpireDateTime = getBanHistoryInstance().getIfPresent(requestHashed);
    if (banExpireDateTime != null) {
      if (banExpireDateTime.isBefore(nowTime)) {
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
