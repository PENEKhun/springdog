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

window.springdogFetch = function (url, options, data, successCallback,
    errorCallback) {
  if (springdogBasePath === undefined) {
    console.error(
        `springdogBasePath is not defined. Please define it in your HTML file.
        Like this : 
        
        /*<![CDATA[*/
          const springdogBasePath = /*[[$\{springdogProperties.computeAbsolutePath\}]]*/ '';
        /*]]>*/
        `);
    return;
  }

  if (url.startsWith('/')) {
    url = url.substring(1);
  }

  const fetchOptions = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
  };

  if (data) {
    fetchOptions.body = JSON.stringify(data);
  }

  return fetch(springdogBasePath + url, fetchOptions)
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        throw new Error(errorData.message || 'Network response was not ok');
      });
    }
    return response.json().catch(() => ({}));
  });
}
