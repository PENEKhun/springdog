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

package smoketest.data.jpa.service;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import smoketest.data.jpa.domain.Book;

@SpringBootTest
class BookServiceIntegrationTests {

  @Autowired
  private BookService bookService;

  @Test
  void test() {
    // given
    String authorName = "PENEKhun";

    // when
    List<Book> results = bookService.allBooksByAuthor(authorName);

    // then
    assertThat(results)
        .extracting(Book::getAuthor, Book::getName, Book::getDescription, Book::getPrice)
        .containsExactlyInAnyOrder(
            tuple("PENEKhun", "To Kill a Mockingbird",
                "A gripping and heart-wrenching story of racial injustice in the Deep South, told through the eyes of young Scout Finch.",
                18_000L),
            tuple("PENEKhun", "1984",
                "A dystopian novel exploring the dangers of totalitarianism and extreme political ideology in a repressive society.",
                14_000L)
        );
  }

}
