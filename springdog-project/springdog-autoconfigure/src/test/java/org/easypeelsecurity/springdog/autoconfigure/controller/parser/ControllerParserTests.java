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

package org.easypeelsecurity.springdog.autoconfigure.controller.parser;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

import javax.tools.JavaFileObject;

import org.easypeelsecurity.springdog.shared.ratelimit.ApiParameter;
import org.easypeelsecurity.springdog.shared.ratelimit.ApiParameterType;
import org.easypeelsecurity.springdog.shared.ratelimit.HttpMethod;
import org.easypeelsecurity.springdog.shared.ratelimit.ParsedMetadata;
import org.junit.jupiter.api.Test;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

class ControllerParserTests {

  private final String SOURCE_PATH = "codes/controller/";

  @Test
  void getMappingWithNoParam() {
    // given
    JavaFileObject src = JavaFileObjects.forResource(SOURCE_PATH + "GetMappingWithNoParam.java");
    ParsedMetadata parsedMetadata = new ParsedMetadata("/api/v1/no-param-page",
        "org.epsec.testing.GetMappingWithNoParam.test", HttpMethod.GET);

    // when
    Compilation compilation = javac()
        .withProcessors(
            new org.easypeelsecurity.springdog.autoconfigure.controller.parser.ControllerParseProcessor())
        .compile(src);

    // then
    assertThat(compilation)
        .hadNoteContaining(parsedMetadata.toString());
  }

  @Test
  void deleteMappingWithParams() {
    // given
    JavaFileObject src = JavaFileObjects.forResource(SOURCE_PATH + "DeleteMappingWithParams.java");
    ParsedMetadata parsedMetadata = new ParsedMetadata("/api/v1/edit/{id}",
        "org.epsec.testing.DeleteMappingWithParams.editPage", HttpMethod.DELETE);
    ApiParameter[] parameters = new ApiParameter[] {
        new ApiParameter("id", ApiParameterType.PATH),
        new ApiParameter("title", ApiParameterType.QUERY),
    };

    // when
    Compilation compilation = javac()
        .withProcessors(
            new org.easypeelsecurity.springdog.autoconfigure.controller.parser.ControllerParseProcessor())
        .compile(src);

    // then
    assertThat(compilation)
        .hadNoteContaining(parsedMetadata.toString());

    for (ApiParameter parameter : parameters) {
      assertThat(compilation)
          .hadNoteContaining(parameter.toString());
    }
  }

  @Test
  void postMappingWithParams() {
    // given
    JavaFileObject src = JavaFileObjects.forResource(SOURCE_PATH + "PostMappingWithParams.java");
    ParsedMetadata parsedMetadata = new ParsedMetadata("/api/v1/post",
        "org.epsec.testing.PostMappingWithParams.postPage", HttpMethod.POST);
    ApiParameter[] parameters = new ApiParameter[] {
        new ApiParameter("title", ApiParameterType.BODY),
        new ApiParameter("content", ApiParameterType.BODY)
    };

    // when
    Compilation compilation = javac()
        .withProcessors(
            new org.easypeelsecurity.springdog.autoconfigure.controller.parser.ControllerParseProcessor())
        .compile(src);

    // then
    System.out.println(compilation.notes());
    assertThat(compilation)
        .hadNoteContaining(parsedMetadata.toString());

    for (ApiParameter parameter : parameters) {
      assertThat(compilation)
          .hadNoteContaining(parameter.toString());
    }
  }

  @Test
  void getMappingWithCustomQueryName() {
    // given
    JavaFileObject src = JavaFileObjects.forResource(SOURCE_PATH + "GetMappingWithCustomQueryName.java");
    ParsedMetadata parsedMetadata = new ParsedMetadata("/api/v1/post",
        "org.epsec.testing.GetMappingWithCustomQueryName.postPage", HttpMethod.GET);
    ApiParameter[] parameters = new ApiParameter[] {
        new ApiParameter("postNumber", ApiParameterType.QUERY),
        new ApiParameter("title", ApiParameterType.QUERY),
        new ApiParameter("postContent", ApiParameterType.QUERY)
    };

    // when
    Compilation compilation = javac()
        .withProcessors(
            new org.easypeelsecurity.springdog.autoconfigure.controller.parser.ControllerParseProcessor())
        .compile(src);

    // then
    assertThat(compilation)
        .hadNoteContaining(parsedMetadata.toString());

    for (ApiParameter parameter : parameters) {
      assertThat(compilation)
          .hadNoteContaining(parameter.toString());
    }
  }

  @Test
  void variousMappingParseTest() {
    // given
    JavaFileObject src = JavaFileObjects.forResource(SOURCE_PATH + "MultipleMappings.java");
    ParsedMetadata[] parsedMetadata = new ParsedMetadata[] {
        new ParsedMetadata("/api/v1/get",
            "org.epsec.testing.MultipleMappings.getMapping", HttpMethod.GET),
        new ParsedMetadata("/api/v1/post",
            "org.epsec.testing.MultipleMappings.postMapping", HttpMethod.POST),
        new ParsedMetadata("/api/v1/put",
            "org.epsec.testing.MultipleMappings.putMapping", HttpMethod.PUT),
        new ParsedMetadata("/api/v1/delete",
            "org.epsec.testing.MultipleMappings.deleteMapping", HttpMethod.DELETE)
    };

    // when
    Compilation compilation = javac()
        .withProcessors(
            new org.easypeelsecurity.springdog.autoconfigure.controller.parser.ControllerParseProcessor())
        .compile(src);

    // then
    for (ParsedMetadata metadata : parsedMetadata) {
      assertThat(compilation)
          .hadNoteContaining(metadata.toString());
    }
  }
}
