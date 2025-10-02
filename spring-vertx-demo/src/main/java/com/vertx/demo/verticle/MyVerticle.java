package com.vertx.demo.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import org.springframework.http.MediaType;

public class MyVerticle extends AbstractVerticle {
  @Override
  public void start() {
    Router router = Router.router(vertx);

    // Register GET route
    router.get("/myreactive").consumes(MediaType.TEXT_PLAIN_VALUE)
            .handler(ctx -> {
              ctx.response()
                      .putHeader("Content-Type", "text/plain")
                      .end("Hello from Vert.x!");
            });

    router.get("/vertxstream").handler(ctx -> {
      ctx.response()
              .putHeader("Content-Type", "text/event-stream")
              .setChunked(true);

      vertx.setPeriodic(1000, id -> {
        ctx.response().write("data: Hello #" + id + "\n\n");
      });
    });

    // GET /stream (SSE)
    router.get("/stream").handler(ctx -> {
      ctx.response()
              .putHeader("Content-Type", "text/event-stream")
              .setChunked(true);

      long timerId = vertx.setPeriodic(1000, id -> {
        ctx.response().write("data: Hello #" + id + "\n\n");
      });

      // Stop the periodic timer after 2 minutes (120000 ms)
      vertx.setTimer(10000, t -> {
        vertx.cancelTimer(timerId);
        ctx.response().end(); // closes the SSE stream});
      });
    });


    // Start HTTP server
    vertx.createHttpServer()
            .requestHandler(router)
            .listen(8081);
  }
}