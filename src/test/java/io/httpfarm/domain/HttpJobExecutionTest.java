package io.httpfarm.domain;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpJobExecutionTest {

  @Test(expected = NullPointerException.class)
  public void testNull() {
   new HttpJobExecution(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
  }

  @Test
  public void testEqualityAndIdentity() {
    Map<String, String> reqHeaders = new HashMap<>();
    reqHeaders.put("k1", "v1");
    reqHeaders.put("k2", "v2");

    Map<String, String> repHeaders = new HashMap<>();
    reqHeaders.put("k3", "v3");
    reqHeaders.put("k4", "v4");
    HttpJobExecution res1 = new HttpJobExecution(
            "uuid1",
            "url1",
            "jobUuid1",
            Instant.EPOCH.getEpochSecond(),
            Instant.EPOCH.plus(7, ChronoUnit.SECONDS).getEpochSecond(),
            reqHeaders,
            "respBody",
            repHeaders,
            1,
            "retryFromUuid1",
            "error1"
    );
    HttpJobExecution res11 = new HttpJobExecution(
            res1.getUuid(),
            res1.getUrl(),
            res1.getJobUuid(),
            res1.getStart(),
            res1.getEnd(),
            res1.getRequestHeaders(),
            res1.getResponseBody(),
            res1.getResponseHeaders(),
            res1.getResponseStatusCode(),
            res1.getRetryFrom(),
            res1.getError()
    );
    assertEquals(res1, res11);
    assertEquals(res1.hashCode(), res11.hashCode());
  }

  @Test
  public void testJsonSerialization() {
    Map<String, String> reqHeaders = new HashMap<>();
    reqHeaders.put("k1", "v1");
    reqHeaders.put("k2", "v2");

    Map<String, String> repHeaders = new HashMap<>();
    repHeaders.put("k3", "v3");
    repHeaders.put("k4", "v4");

    HttpJobExecution res1 = new HttpJobExecution(
            "uuid1",
            "url1",
            "jobUuid1",
            Instant.EPOCH.getEpochSecond(),
            Instant.EPOCH.plus(7, ChronoUnit.SECONDS).getEpochSecond(),
            reqHeaders,
            "respBody",
            repHeaders,
            1,
            "retryFromUuid1",
            "error1"
    );

    JsonObject json = res1.toJson();
    String exp = "{\"uuid\":\"uuid1\",\"url\":\"url1\",\"jobUuid\":\"jobUuid1\",\"start\":0,\"end\":7,\"requestHeaders\":{\"k1\":\"v1\",\"k2\":\"v2\"},\"responseBody\":\"respBody\",\"responseHeaders\":{\"k3\":\"v3\",\"k4\":\"v4\"},\"responseStatusCode\":1,\"retryFrom\":\"retryFromUuid1\",\"error\":\"error1\"}";
    assertEquals("json string", exp, json.toString());
    assertEquals(res1, HttpJobExecution.fromJson(json));
  }
}
