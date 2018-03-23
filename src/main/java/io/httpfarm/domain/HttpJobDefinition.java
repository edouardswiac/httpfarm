package io.httpfarm.domain;

import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class HttpJobDefinition {
  private String url;
  private Map<String, String> headers;
  private String cronExpr;
  private String uuid;
  private Integer timeoutMillis;
  private Integer maxTries;
  private String method = "GET";

  public final static Integer DEFAULT_TIMEOUT_MILLIS = 5000;
  public final static Integer DEFAULT_MAX_TRIES = 2;

  public HttpJobDefinition() { }

  public HttpJobDefinition(String url, String cronExpr) {
    this(UUID.randomUUID().toString(), url, cronExpr, Collections.emptyMap(), DEFAULT_TIMEOUT_MILLIS, DEFAULT_MAX_TRIES);
  }

  public HttpJobDefinition(String uuid, String url, String cronExpr) {
    this(uuid, url, cronExpr, Collections.emptyMap(), DEFAULT_TIMEOUT_MILLIS, DEFAULT_MAX_TRIES);
  }

  public HttpJobDefinition(String url,
                           String cronExpr,
                           Map<String, String> headers,
                           Integer timeoutMillis,
                           Integer maxTries) {
    this(UUID.randomUUID().toString(), url, cronExpr, headers, timeoutMillis, maxTries);
  }

  public HttpJobDefinition(String uuid,
                           String url,
                           String cronExpr,
                           Map<String, String> headers,
                           Integer timeoutMillis,
                           Integer maxTries) {
    Objects.requireNonNull(uuid);
    Objects.requireNonNull(url);
    Objects.requireNonNull(cronExpr);
    Objects.requireNonNull(headers);
    Objects.requireNonNull(timeoutMillis);
    Objects.requireNonNull(maxTries);

    this.url = url;
    this.headers = headers;
    this.cronExpr = cronExpr;
    this.uuid = uuid;
    this.timeoutMillis = timeoutMillis;
    this.maxTries = maxTries;
  }

  public String getUuid() {
    return uuid;
  }

  public String getUrl() {
    return url;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getCronExpr() {
    return cronExpr;
  }

  public Integer getTimeoutMillis() {
    return timeoutMillis;
  }

  public Integer getMaxTries() {
    return maxTries;
  }

  public String getMethod() {
    return method.toUpperCase();
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public static HttpJobDefinition fromJson(JsonObject json) throws IllegalArgumentException {
    return json.mapTo(HttpJobDefinition.class);
  }

  public JsonObject toJson() {
    return JsonObject.mapFrom(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HttpJobDefinition that = (HttpJobDefinition) o;
    return Objects.equals(getUrl(), that.getUrl()) &&
            Objects.equals(getHeaders(), that.getHeaders()) &&
            Objects.equals(getCronExpr(), that.getCronExpr()) &&
            Objects.equals(getUuid(), that.getUuid()) &&
            Objects.equals(getTimeoutMillis(), that.getTimeoutMillis()) &&
            Objects.equals(getMaxTries(), that.getMaxTries());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUrl(), getHeaders(), getCronExpr(), getUuid(), getTimeoutMillis(), getMaxTries());
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("HttpJobDefinition{");
    sb.append("url='").append(url).append('\'');
    sb.append(", headers=").append(headers);
    sb.append(", cronExpr='").append(cronExpr).append('\'');
    sb.append(", uuid='").append(uuid).append('\'');
    sb.append(", timeoutMillis=").append(timeoutMillis);
    sb.append(", maxTries=").append(maxTries);
    sb.append('}');
    return sb.toString();
  }
}
