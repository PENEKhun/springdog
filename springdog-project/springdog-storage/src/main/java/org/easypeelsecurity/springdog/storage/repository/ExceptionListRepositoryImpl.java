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

package org.easypeelsecurity.springdog.storage.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionCause;
import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionClass;
import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionListingRepository;
import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionType;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.ObjectSelect;

/**
 * Repository implementation for {@link ExceptionType}, {@link ExceptionClass} entities.
 */
@Repository
public class ExceptionListRepositoryImpl implements ExceptionListingRepository {

  @Override
  public List<ExceptionType> findAllExceptions(ObjectContext context) {
    return ObjectSelect.query(ExceptionType.class)
        .orderBy(ExceptionType.PACKAGE_TYPE.asc())
        .prefetch(ExceptionType.EXCEPTION_CLASSES.joint())
        // TODO: Add order by for ExceptionClass
        .select(context);
  }

  @Override
  public ExceptionClass findExceptionClassByIdOrNull(ObjectContext context, long exceptionClassId) {
    return ObjectSelect.query(ExceptionClass.class)
        .where(ExceptionClass.ID.eq(exceptionClassId))
        .selectOne(context);
  }

  @Override
  public ExceptionClass findByExceptionClassByFQCM(ObjectContext context, String exceptionFQCM) {
    return ObjectSelect.query(ExceptionClass.class)
        .where(ExceptionClass.EXCEPTION_CLASS_NAME.eq(exceptionFQCM))
        .selectOne(context);
  }

  @Override
  public List<ExceptionCause> findAllParentExceptionCauses(ObjectContext context) {
    String ejbql = """
        SELECT e FROM ExceptionCause e
        WHERE e.id NOT IN (SELECT e2.nextException.id FROM ExceptionCause e2 WHERE e2.nextException IS NOT NULL)
        ORDER BY e.timestamp DESC
        """;

    EJBQLQuery query = new EJBQLQuery(ejbql);
    return context.performQuery(query);
  }

  @Override
  public ExceptionCause findExceptionCauseByIdOrNull(ObjectContext context, long exceptionCauseId) {
    return ObjectSelect.query(ExceptionCause.class)
        .where(ExceptionCause.ID.eq(exceptionCauseId))
        .selectOne(context);
  }
}
