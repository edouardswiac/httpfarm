package io.httpfarm;

import io.httpfarm.dao.HttpJobDefinitionDao;
import io.httpfarm.dao.HttpJobExecutionDao;
import io.httpfarm.dao.InMemoryHttpJobDefinitionDao;
import io.httpfarm.dao.InMemoryHttpJobExecutionDao;
import io.httpfarm.domain.HttpJobDefinition;
import io.httpfarm.http.RestController;
import io.httpfarm.repository.HttpJobRepository;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class Main {
  private final static Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    LOG.info("Welcome to HTTPFarm!");
    LOG.info("Press ctrl+c to exit.");

    System.setProperty(
      io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME,
      SLF4JLogDelegateFactory.class.getName()
    );

    Vertx vertx = Vertx.vertx();
    Runtime rt = Runtime.getRuntime();

    HttpJobDefinitionDao jobDefDao = new InMemoryHttpJobDefinitionDao();
    Clock clock = Clock.systemUTC();
    HttpJobRepository repo = new HttpJobRepository(jobDefDao, clock);
    LOG.info("using clock {}",clock);
    vertx.deployVerticle(new JobPoller(repo), poller -> {
      rt.addShutdownHook(new Thread(() -> {
        CountDownLatch latch = new CountDownLatch(1);
        LOG.info("SIGINT received. Waiting for current polling batch to complete...");
        vertx.sharedData().getLock(JobPoller.EXEC_LOCK, r1 -> {
          vertx.undeploy(poller.result(), executor -> {
            LOG.info("Waiting for inflight requests to complete...");
            vertx.setPeriodic(1000, r2 -> {
              vertx.sharedData().getCounter(HttpExecutor.INFLIGHT_COUNTER, inflightCounterRes -> {
                inflightCounterRes.result().get(inflightCounter -> {
                  if (inflightCounter.result() == 0) {
                    LOG.info("Inflight requests have completed");
                    latch.countDown();
                  } else {
                    LOG.info("Waiting for {} inflight requests to complete...", inflightCounter.result());
                  }
                });
              });
            });
          });
        });

        try {
          latch.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException iex) {
          LOG.error("exception while shutting down poller", iex);
        }
        LOG.info("bye");
      }));
    });

    HttpJobExecutionDao jobExecDao = new InMemoryHttpJobExecutionDao();
    HttpExecutor ex = new HttpExecutor(jobExecDao);
    vertx.deployVerticle(ex, new DeploymentOptions());
    vertx.deployVerticle(new RestController(jobDefDao, jobExecDao));
  }
}
