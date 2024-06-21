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

package smoketest.data.jpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;
  private String author;
  private String description;
  private long price;

  public Book() {
  }

  public Book(String name, String author, String description, long price) {
    if (price < 0) {
      throw new IllegalArgumentException("book's price must greater than 0");
    }
    this.name = name;
    this.author = author;
    this.description = description;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public String getAuthor() {
    return author;
  }

  public String getDescription() {
    return description;
  }

  public long getPrice() {
    return price;
  }
}
