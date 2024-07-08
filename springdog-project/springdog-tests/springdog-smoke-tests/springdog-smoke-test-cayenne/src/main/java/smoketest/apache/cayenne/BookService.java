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

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.query.ObjectSelect;

@Service
public class BookService {

  private final CayenneRuntime cayenneRuntime;

  public BookService(@Qualifier("cayenneRuntime") CayenneRuntime cayenneRuntime) {
    this.cayenneRuntime = cayenneRuntime;
  }

  public List<Book> allBooksByAuthor(String authorName) {
    ObjectContext context = cayenneRuntime.newContext();
    return ObjectSelect.query(Book.class)
        .where(Book.AUTHOR.eq(authorName))
        .select(context);
  }
}
