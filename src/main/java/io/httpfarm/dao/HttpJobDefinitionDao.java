package io.httpfarm.dao;

import io.httpfarm.domain.HttpJobDefinition;

import java.util.List;
import java.util.Optional;

public interface HttpJobDefinitionDao {
  void save(HttpJobDefinition httpJobDefinition) throws DaoException;
  Optional<HttpJobDefinition> get(String uuid) throws DaoException;
  void delete(String uuid) throws DaoException;
  List<HttpJobDefinition> getAll() throws DaoException;
}
