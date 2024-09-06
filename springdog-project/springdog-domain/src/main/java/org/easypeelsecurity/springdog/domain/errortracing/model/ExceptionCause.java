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

package org.easypeelsecurity.springdog.domain.errortracing.model;

import java.time.LocalDateTime;

import org.easypeelsecurity.springdog.domain.errortracing.model.auto._ExceptionCause;

@SuppressWarnings("all")
public class ExceptionCause extends _ExceptionCause {

  private static final long serialVersionUID = 1L;

  @Override
  protected void onPrePersist() {
    setTimestamp(LocalDateTime.now());
  }
}
