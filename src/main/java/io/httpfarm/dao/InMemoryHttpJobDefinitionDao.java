package io.httpfarm.dao;

import io.httpfarm.domain.HttpJobDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryHttpJobDefinitionDao implements HttpJobDefinitionDao {
  private final ConcurrentHashMap<String, HttpJobDefinition> store;
  private final static Logger LOG = LoggerFactory.getLogger(InMemoryHttpJobDefinitionDao.class);

  public InMemoryHttpJobDefinitionDao() {
    this.store = new ConcurrentHashMap<>();
  }

  @Override
  public void save(HttpJobDefinition httpJobDefinition) throws DaoException {
    Objects.requireNonNull(httpJobDefinition.getUuid());
    store.put(httpJobDefinition.getUuid(), httpJobDefinition);
    LOG.info("saving new job {} -> {} {} with cron: {}",
            httpJobDefinition.getUuid(),
            httpJobDefinition.getMethod(),
            httpJobDefinition.getUrl(),
            httpJobDefinition.getCronExpr());
  }

  @Override
  public Optional<HttpJobDefinition> get(String uuid) throws DaoException {
    Objects.requireNonNull(uuid);
    return Optional.ofNullable(store.get(uuid));
  }

  @Override
  public void delete(String uuid) throws DaoException {
    Objects.requireNonNull(uuid);
    store.remove(uuid);
    LOG.info("deleteing job {}", uuid);
  }

  //TODO pagination/stream once there are too many records
  @Override
  public List<HttpJobDefinition> getAll() throws DaoException {
    return new LinkedList<>(store.values());
  }
}
