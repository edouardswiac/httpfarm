package io.httpfarm.repository;

import io.httpfarm.dao.HttpJobExecutionDao;

public class HttpJobExecutionRepository {
  private final HttpJobExecutionDao dao;

  public HttpJobExecutionRepository(HttpJobExecutionDao dao) {
    this.dao = dao;
  }
}
