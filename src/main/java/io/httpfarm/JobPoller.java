package io.httpfarm;

import io.httpfarm.domain.HttpJobDefinition;
import io.httpfarm.repository.HttpJobRepository;
import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JobPoller extends AbstractVerticle {
  private final static Logger LOG = LoggerFactory.getLogger(JobPoller.class);
  public final static String EXEC_LOCK = "EXEC_LOCK";

  private final HttpJobRepository repo;
  private final int pollingIntervalSec = 60;
  public JobPoller(HttpJobRepository repo) {
    this.repo = repo;
  }

  public void poll() {
    vertx.sharedData().getLock(EXEC_LOCK, res -> {
      LOG.info("polling for due jobs...");
      List<HttpJobDefinition> jobsReady = repo.findDueForExecutionNow();
      LOG.info("found {} jobs ready", jobsReady.size());
      jobsReady.forEach(el -> {
        vertx.eventBus().send(HttpExecutor.EB_TOPIC, el.toJson());
      });
      res.result().release();
    });
  }

  @Override
  public void start() throws Exception {
    LOG.info("polling for due jobs every {} seconds", pollingIntervalSec);
    poll();
    vertx.setPeriodic(1000 * pollingIntervalSec, (v) -> {
      poll();
    });
  }

}
