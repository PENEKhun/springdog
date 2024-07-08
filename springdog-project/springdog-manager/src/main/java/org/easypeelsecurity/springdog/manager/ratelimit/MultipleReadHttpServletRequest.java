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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

/**
 * A class to provide the ability to read a {@link jakarta.servlet.ServletRequest}'s body multiple
 * times. via <a
 * href="https://www.jvt.me/posts/2020/05/25/httpmessagenotreadableexception-contentcachingrequestwrapper/">article</a>
 */
public class MultipleReadHttpServletRequest extends HttpServletRequestWrapper {

  private ByteArrayOutputStream cachedBytes;

  /**
   * Construct a new multi-read wrapper.
   *
   * @param request to wrap around
   */
  public MultipleReadHttpServletRequest(HttpServletRequest request) {
    super(request);
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (cachedBytes == null) {
      cacheInputStream();
    }

    return new CachedServletInputStream(cachedBytes.toByteArray());
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  private void cacheInputStream() throws IOException {
    /* Cache the input-stream in order to read it multiple times. For
     * convenience, I use apache.commons IOUtils
     */
    cachedBytes = new ByteArrayOutputStream();
    IOUtils.copy(super.getInputStream(), cachedBytes);
  }

  /* An input-stream which reads the cached request body */
  private static class CachedServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream buffer;

    CachedServletInputStream(byte[] contents) {
      this.buffer = new ByteArrayInputStream(contents);
    }

    @Override
    public int read() {
      return buffer.read();
    }

    @Override
    public boolean isFinished() {
      return buffer.available() == 0;
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
      throw new UnsupportedOperationException();
    }
  }
}
