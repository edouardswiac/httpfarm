package io.httpfarm;

import io.httpfarm.dao.HttpJobExecutionDao;
import io.httpfarm.domain.HttpJobDefinition;
import io.httpfarm.domain.HttpJobExecution;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpExecutor extends AbstractVerticle {
  public static final String EB_TOPIC = "http.executor";
  public static final String INFLIGHT_COUNTER = "INFLIGHT_COUNTER";

  private static final Logger LOG = LoggerFactory.getLogger(HttpExecutor.class);

  private final HttpJobExecutionDao dao;
  private WebClientOptions options = new WebClientOptions()
          .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
          .setConnectTimeout(2_000)
          .setKeepAlive(false)
          .setIdleTimeout(2_000);

  public HttpExecutor(HttpJobExecutionDao dao) {
    this.dao = dao;
  }

  private static final Map<String, String> EMPTY_HEADERS = Collections.emptyMap();

  @Override
  public void start() throws Exception {
    EventBus eb = vertx.eventBus();

    WebClient client = WebClient.create(vertx, options);

    eb.localConsumer(EB_TOPIC, payload -> {
      incrInFlightCount();
      final HttpJobDefinition job = HttpJobDefinition.fromJson(JsonObject.mapFrom(payload.body()));
      final Instant start = Instant.now();
      HttpUrl url = HttpUrl.parse(job.getUrl());
      LOG.info("[{}] starting ({} {}) ", job.getUuid(), job.getMethod(), url.toString());
      client.get(url.host(), url.encodedPath())
              .timeout(job.getTimeoutMillis())
              .send(res -> {
        HttpJobExecution exec;

        Instant end = Instant.now();
        if (res.succeeded()) {
          exec = new HttpJobExecution(
                  job.getUrl(),
                  job.getUuid(),
                  start.toEpochMilli(),
                  end.toEpochMilli(),
                  job.getHeaders(),
                  res.result().bodyAsString(),
                  stringHeaders(res.result().headers()),
                  res.result().statusCode(),
                  null,
                  null
          );
        } else {
          LOG.error("exception", res.cause());
          exec = new HttpJobExecution(
                  job.getUrl(),
                  job.getUuid(),
                  start.toEpochMilli(),
                  end.toEpochMilli(),
                  job.getHeaders(),
                  null,
                  null,
                  null,
                  null,
                  res.cause().getMessage()
          );
        }
        dao.save(exec);
        LOG.debug("job: {}", job.toString());
        LOG.debug("execution: {}", exec.toString());
        LOG.info("[{}] completed ({} {}) ", exec.getJobUuid(), job.getMethod(), url.toString());
        decrInFlightCount();
      });
    });
  }

  private Map<String, String> stringHeaders(MultiMap headers) {
    return headers.entries().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())) ;
  }
  private void incrInFlightCount() {
    vertx.sharedData().getCounter(INFLIGHT_COUNTER, r1 -> {
      r1.result().incrementAndGet(r2 -> {

      });
    });
  }

  private void decrInFlightCount() {
    vertx.sharedData().getCounter(INFLIGHT_COUNTER, r1 -> {
      r1.result().decrementAndGet(r2 -> {

      });
    });
  }
}
