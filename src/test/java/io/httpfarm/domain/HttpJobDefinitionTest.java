package io.httpfarm.domain;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpJobDefinitionTest {
  private HttpJobDefinition def1 = new HttpJobDefinition("url1", "cron1");
  private HttpJobDefinition def2 = new HttpJobDefinition("url2", "cron2");

  @Test
  public void testEqualityAndIdentity() {
    assertEquals("equality with self", def1, def1);
    assertEquals("identity with self", def1.hashCode(), def1.hashCode());

    assertNotEquals(def1, def2);
    assertNotEquals(def1.hashCode(), def2.hashCode());

    HttpJobDefinition def3 = new HttpJobDefinition(def1.getUuid(),def1.getUrl(), def1.getCronExpr());
    assertEquals(def1, def3);
    assertEquals(def1.hashCode(), def3.hashCode());
  }

  @Test
  public void testJsonSerialization() {
    Map<String, String> headers = new HashMap<>();
    headers.put("k1", "v1");
    headers.put("k2", "v2");
    HttpJobDefinition def3 = new HttpJobDefinition("uuid3", "url3", "cron3", headers, 1000, 3);

    JsonObject json = def3.toJson();
    String exp = "{\"url\":\"url3\",\"headers\":{\"k1\":\"v1\",\"k2\":\"v2\"},\"cronExpr\":\"cron3\",\"uuid\":\"uuid3\",\"timeoutMillis\":1000,\"maxTries\":3,\"method\":\"GET\"}";
    assertEquals("json string", exp, json.toString());
    assertEquals("serialize/deserialize", def3, HttpJobDefinition.fromJson(json));
  }

  @Test(expected = NullPointerException.class)
  public void testNullParams() {
    new HttpJobDefinition(null, null);
  }
}
