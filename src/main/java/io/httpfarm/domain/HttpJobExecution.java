package io.httpfarm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.core.impl.StringEscapeUtils;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class HttpJobExecution {
  // we have these for every execution
  private String uuid;
  private String url;
  private String jobUuid;
  private Long start;
  private Long end;
  private Map<String, String> requestHeaders;

  // only when we receive an http response (regardless of status code)
  private String responseBody;
  private Map<String, String> responseHeaders;
  private Integer responseStatusCode;

  // only when a non-http failure happened //
  private String retryFrom; // if a retry
  private String error;

  public HttpJobExecution() { }

  public HttpJobExecution(String url,
                          String jobUuid,
                          Long start,
                          Long end,
                          Map<String, String> requestHeaders,
                          String responseBody,
                          Map<String, String> responseHeaders,
                          Integer responseStatusCode,
                          String retryFromUuid,
                          String error) {
    this(UUID.randomUUID().toString(),
            url,
            jobUuid,
            start,
            end,
            requestHeaders,
            responseBody,
            responseHeaders,
            responseStatusCode,
            retryFromUuid,
            error);
  }

  public HttpJobExecution(String url,
                          String jobUuid,
                          Long start,
                          Long end,
                          Map<String, String> requestHeaders) {
    this(UUID.randomUUID().toString(),
            url,
            jobUuid,
            start,
            end,
            requestHeaders,
            null,
            null,
            null,
            null,
            null);
  }

  public HttpJobExecution(String uuid,
                          String url,
                          String jobUuid,
                          Long start,
                          Long end,
                          Map<String, String> requestHeaders,
                          @Nullable  String responseBody,
                          @Nullable Map<String, String> responseHeaders,
                          @Nullable Integer responseStatusCode,
                          @Nullable String retryFromUuid,
                          @Nullable String error) {
    Objects.requireNonNull(uuid);
    Objects.requireNonNull(url);
    Objects.requireNonNull(jobUuid);
    Objects.requireNonNull(start);
    Objects.requireNonNull(end);
    Objects.requireNonNull(requestHeaders);

    this.uuid = uuid;
    this.url = url;
    this.jobUuid = jobUuid;
    this.start = start;
    this.end = end;
    this.requestHeaders = requestHeaders;
    try {
      this.responseBody = StringEscapeUtils.escapeJavaScript(responseBody);
    } catch (Exception e) {

    }
    this.responseHeaders = responseHeaders;
    this.responseStatusCode = responseStatusCode;
    this.retryFrom = retryFromUuid;
    try {
      this.error = StringEscapeUtils.escapeJavaScript(error);
    } catch (Exception e) {

    }
  }

  public String getUuid() {
    return uuid;
  }

  public String getUrl() {
    return url;
  }

  public String getJobUuid() {
    return jobUuid;
  }

  public Long getStart() {
    return start;
  }

  public Long getEnd() {
    return end;
  }

  public String getResponseBody() {
    return responseBody;
  }

  public Map<String, String> getRequestHeaders() {
    return requestHeaders;
  }

  public Map<String, String> getResponseHeaders() {
    return responseHeaders;
  }

  public Integer getResponseStatusCode() {
    return responseStatusCode;
  }

  public String getRetryFrom() {
    return retryFrom;
  }

  public String getError() {
    return error;
  }

  @JsonIgnore
  public boolean isSuccess() {
    return this.error == null;
  }

  @JsonIgnore
  public boolean isFailure() {
    return this.error == null;
  }

  @JsonIgnore
  public boolean isRetry() {
    return this.retryFrom != null;
  }

  public static HttpJobExecution fromJson(JsonObject json) {
    return json.mapTo(HttpJobExecution.class);
  }

  public JsonObject toJson() {
    return JsonObject.mapFrom(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HttpJobExecution that = (HttpJobExecution) o;
    return Objects.equals(getUuid(), that.getUuid()) &&
            Objects.equals(getUrl(), that.getUrl()) &&
            Objects.equals(getJobUuid(), that.getJobUuid()) &&
            Objects.equals(getStart(), that.getStart()) &&
            Objects.equals(getEnd(), that.getEnd()) &&
            Objects.equals(getRequestHeaders(), that.getRequestHeaders()) &&
            Objects.equals(getResponseBody(), that.getResponseBody()) &&
            Objects.equals(getResponseHeaders(), that.getResponseHeaders()) &&
            Objects.equals(getResponseStatusCode(), that.getResponseStatusCode()) &&
            Objects.equals(getRetryFrom(), that.getRetryFrom()) &&
            Objects.equals(getError(), that.getError());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUuid(), getUrl(), getJobUuid(), getStart(), getEnd(), getRequestHeaders(), getResponseBody(), getResponseHeaders(), getResponseStatusCode(), getRetryFrom(), getError());
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("HttpJobExecution{");
    sb.append("uuid='").append(uuid).append('\'');
    sb.append(", url='").append(url).append('\'');
    sb.append(", jobUuid='").append(jobUuid).append('\'');
    sb.append(", start=").append(start);
    sb.append(", end=").append(end);
    sb.append(", requestHeaders=").append(requestHeaders);
    sb.append(", responseBody='").append(responseBody).append('\'');
    sb.append(", responseHeaders=").append(responseHeaders);
    sb.append(", responseStatusCode=").append(responseStatusCode);
    sb.append(", retryFrom='").append(retryFrom).append('\'');
    sb.append(", error='").append(error).append('\'');
    sb.append('}');
    return sb.toString();
  }
}

