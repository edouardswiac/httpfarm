package io.httpfarm.dao;

import io.httpfarm.domain.HttpJobExecution;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryHttpJobExecutionDao
        implements HttpJobExecutionDao {
  private final ConcurrentHashMap<String, HttpJobExecution> store;

  public InMemoryHttpJobExecutionDao() {
    store = new ConcurrentHashMap<>();
  }

  @Override
  public void save(final HttpJobExecution httpJobExecution) throws DaoException {
    store.put(httpJobExecution.getUuid(), httpJobExecution);
  }

  @Override
  public Optional<HttpJobExecution> get(final String uuid) throws DaoException {
    return Optional.ofNullable(store.get(uuid));
  }

  @Override
  public void delete(final String uuid) throws DaoException {
    store.remove(uuid);
  }

  //TODO pagination/stream once there are too many records
  @Override
  public List<HttpJobExecution> getAll(final String jobDefinitionUuid) throws DaoException {
    return store
        .values()
        .stream()
        .filter(el -> el.getJobUuid().equals(jobDefinitionUuid))
        .sorted((f1, f2) -> Long.compare(f2.getStart(), f1.getStart()))
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return store.toString();
  }
}
