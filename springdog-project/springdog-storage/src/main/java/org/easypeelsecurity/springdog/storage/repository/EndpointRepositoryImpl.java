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

import org.easypeelsecurity.springdog.domain.ratelimit.EndpointRepository;
import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

/**
 * Implementation of {@link EndpointRepository}.
 *
 * @author PENEKhun
 */
@Repository
public class EndpointRepositoryImpl implements EndpointRepository {

  @Override
  public Endpoint findByMethodSignatureOrNull(ObjectContext context, String methodSignature) {
    return ObjectSelect.query(Endpoint.class)
        .where(Endpoint.METHOD_SIGNATURE.eq(methodSignature))
        .selectOne(context);
  }

  @Override
  public Endpoint findByIdOrNull(ObjectContext context, Long id) {
    return ObjectSelect.query(Endpoint.class)
        .where(Endpoint.ID.eq(id))
        .selectOne(context);
  }

  @Override
  public List<Endpoint> findAll(ObjectContext context) {
    return ObjectSelect.query(Endpoint.class)
        .select(context);
  }

  @Override
  public long getEndpointCount(ObjectContext context) {
    return ObjectSelect.query(Endpoint.class)
        .selectCount(context);
  }

  @Override
  public long getEndpointCountByStatus(ObjectContext context, String status) {
    return ObjectSelect.query(Endpoint.class)
        .where(Endpoint.RULE_STATUS.eq(status))
        .selectCount(context);
  }
}
