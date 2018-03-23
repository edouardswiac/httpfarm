package io.httpfarm.dao;

import io.httpfarm.domain.HttpJobExecution;

import java.util.List;
import java.util.Optional;

public interface HttpJobExecutionDao  {
  void save(HttpJobExecution httpJobDefinition) throws DaoException;
  Optional<HttpJobExecution> get(String uuid) throws DaoException;
  void delete(String uuid) throws DaoException;
  List<HttpJobExecution> getAll(String jobDefinitionUuid) throws DaoException;
}
