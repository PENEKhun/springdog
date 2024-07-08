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

package smoketest.apache.cayenne;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringBootTest
class BookIntegrationTests {

  @Autowired
  CayenneRuntime cayenneRuntime;

  @Autowired
  BookService bookService;

  @BeforeEach
  void setUp() {
    // given
    ObjectContext context = cayenneRuntime.newContext();
    context.deleteObjects(
        ObjectSelect.query(Book.class)
            .select(context));

    Book book1 = context.newObject(Book.class);
    book1.setAuthor("PENEKhun");
    book1.setName("To Kill a Mockingbird");
    book1.setDescription(
        "A gripping and heart-wrenching story of racial injustice in the Deep South, told through the eyes of young Scout Finch.");
    book1.setPrice(18_000L);

    Book book2 = context.newObject(Book.class);
    book2.setAuthor("PENEKhun");
    book2.setName("1984");
    book2.setDescription(
        "A dystopian novel exploring the dangers of totalitarianism and extreme political ideology in a repressive society.");
    book2.setPrice(14_000L);

    Book book3 = context.newObject(Book.class);
    book3.setAuthor("George Orwell");
    book3.setName("Animal Farm");
    book3.setDescription("A satirical allegory of the Russian Revolution and the rise of Stalinism.");
    book3.setPrice(12_000L);

    context.commitChanges();
  }

  @Test
  void testApplicationCayenneWork() {
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
