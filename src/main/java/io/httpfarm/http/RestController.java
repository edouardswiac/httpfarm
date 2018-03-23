package io.httpfarm.http;

import io.httpfarm.dao.HttpJobDefinitionDao;
import io.httpfarm.dao.HttpJobExecutionDao;
import io.httpfarm.domain.HttpJobDefinition;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.UUID;


public final class RestController extends AbstractVerticle {
  private final HttpJobDefinitionDao httpJobDefinitionDao;
  private final HttpJobExecutionDao httpJobExecutionDao;
  private final static String CONTENT_TYPE = "Content-Type";
  private final static String APPLICATION_JSON = "application/json";

  public RestController(HttpJobDefinitionDao httpJobDefinitionDao, HttpJobExecutionDao httpJobExecutionDao) {
    this.httpJobDefinitionDao = httpJobDefinitionDao;
    this.httpJobExecutionDao = httpJobExecutionDao;
  }

  @Override
  public void start() throws Exception {
    Router router = Router.router(vertx);

    // View all jobs
    router.get("/jobs").handler(ctx -> {
      JsonObject coll = new JsonObject();
      coll.put("definitions", new JsonArray((httpJobDefinitionDao.getAll())));
      ctx.response()
              .putHeader(CONTENT_TYPE, APPLICATION_JSON)
              .end(coll.toString());
    });

    // Create a job without supplying a UUID
    router.post("/jobs").handler(BodyHandler.create()).handler(ctx -> {
      HttpJobDefinition def = HttpJobDefinition.fromJson(ctx.getBodyAsJson());
      String jobId = UUID.randomUUID().toString();
      def.setUuid(jobId);
      httpJobDefinitionDao.save(def);
      ctx.response().setStatusCode(HttpResponseStatus.ACCEPTED.code()).end(
              new JsonObject().put("uuid", jobId).toString()
      );
    });

    // Create a job with a preset UUID
    router.put("/jobs/:uuid").handler(BodyHandler.create()).handler(ctx -> {
      String jobId = ctx.request().getParam("uuid");
      if (jobId == null || jobId.isEmpty()) {
        ctx.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end("A valid string UUID is required");
        return;
      }

      HttpJobDefinition def = HttpJobDefinition.fromJson(ctx.getBodyAsJson());
      def.setUuid(jobId);
      httpJobDefinitionDao.save(def);
      ctx.response().setStatusCode(HttpResponseStatus.ACCEPTED.code()).end(
              new JsonObject().put("uuid", jobId).toString()
      );;
    });

    router.delete("/jobs/:uuid").handler(ctx -> {
      String jobId = ctx.request().getParam("uuid");

      if (jobId == null || jobId.isEmpty()) {
        ctx.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end("A valid string UUID is required");
        return;
      }
      HttpJobDefinition def = HttpJobDefinition.fromJson(ctx.getBodyAsJson());
      def.setUuid(jobId);
      httpJobDefinitionDao.delete(jobId);
      ctx.response().setStatusCode(HttpResponseStatus.ACCEPTED.code());
    });

    router.get("/jobs/:uuid").handler(ctx -> {
      String jobId = ctx.request().getParam("uuid");
      JsonObject coll = new JsonObject();
      coll.put("executions", new JsonArray((httpJobExecutionDao.getAll(jobId))));
      ctx.response()
              .putHeader(CONTENT_TYPE, APPLICATION_JSON)
              .end(coll.toString());
    });



    HttpServer server = vertx.createHttpServer();

    server.requestHandler(router::accept).listen(8080);
  }

}
