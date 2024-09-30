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
import java.util.Objects;

import org.easypeelsecurity.springdog.domain.errortracing.model.auto._ExceptionCause;

@SuppressWarnings("all")
public class ExceptionCause extends _ExceptionCause {

  private static final long serialVersionUID = 1L;

  @Override
  protected void onPrePersist() {
    setTimestamp(LocalDateTime.now());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ExceptionCause that = (ExceptionCause) o;
    return line == that.line && className.equals(that.className) && fileName.equals(that.fileName)
           && message.equals(that.message) && methodName.equals(that.methodName) && Objects.equals(
        parentExceptionId, that.parentExceptionId) && Objects.equals(nextException, that.nextException);
  }

  @Override
  public int hashCode() {
    int result = className.hashCode();
    result = 31 * result + fileName.hashCode();
    result = 31 * result + line;
    result = 31 * result + message.hashCode();
    result = 31 * result + methodName.hashCode();
    result = 31 * result + Objects.hashCode(parentExceptionId);
    result = 31 * result + Objects.hashCode(nextException);
    return result;
  }
}
