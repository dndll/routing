package dev.hekate.routing

import com.fasterxml.jackson.annotation.JsonInclude
import dev.hekate.routing.verticle.ROUTE_REQUEST_CONSUMER
import dev.hekate.routing.verticle.RouteServiceVerticle
import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.core.Launcher
import io.vertx.core.Promise
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import java.util.logging.Level
import java.util.logging.Logger

class MainVerticle : AbstractVerticle() {
  private val logger = Logger.getLogger(this::class.qualifiedName)

  private lateinit var config: JsonObject

  override fun start(startPromise: Promise<Void>) {
    val configRetriever = ConfigRetriever.create(vertx)
    configRetriever.getConfig { json ->
      if (json.failed()) {
        println("Configuration failed")
      } else {
        config = json.result()
      }
    }
    applyConfiguration()
    setSystemProperties()

    val router = Router.router(vertx)
    val server = vertx.createHttpServer()


    initializeJsonObjectMapperProperties()
    deployVerticles()

    /**
     * Routes implemented
     */
    router.get("/routing").handler(::serviceHandler)
    router.get("/testing").handler(::serviceHandler)
    router.get("/testing/").handler(::serviceHandler)
    router.get("/routing/").handler(::serviceHandler)
    router.get("/routing/service").handler(::serviceHandler)
    router.get("/routing/calculate").handler(::calculateHandler)
    router.get().handler(::calculateHandler)

//    val httpServerOptions = HttpServerOptions()
//      .setLogActivity(true)
//      .setUseAlpn(false)
//      .setSsl(false)
//      .setDecompressionSupported(true)
//      .setCompressionSupported(true)

    server.requestHandler(router).listen(SETTINGS_MAP["PORT"]?.toInt() ?: 8751) { http ->
      if (http.succeeded()) {
        println("HTTP server started on port ${SETTINGS_MAP["PORT"] ?: 8751}")
        startPromise.complete()
      } else {
        startPromise.fail(http.cause())
      }
    }

    router.routes.forEach { route -> logger.info(route.toString())}
  }

  private fun serviceHandler(it: RoutingContext) {
    val json = JsonObject()
      .put("title", "Routing")
      .put("description", "Test")
      .put("references", listOf("TEST"))
      .put("tags", listOf("test"))
    it.response().putHeader("Content-Type", "application/json")
    it.response().end(json.encode())
  }

  private fun applyConfiguration() {
    SETTINGS_MAP["OSRM_HOST"] = config.getString("OSRM_HOST", "localhost")
    SETTINGS_MAP["OSRM_PORT"] = config.getString("OSRM_PORT", 5000.toString())
    SETTINGS_MAP["PORT"] = config.getInteger("PORT", 8751).toString()
  }

  private fun initializeJsonObjectMapperProperties() {
    Json.mapper.findAndRegisterModules().setSerializationInclusion(JsonInclude.Include.NON_NULL)
  }

  private fun setSystemProperties() {
    System.setProperty("vertx.logger-delegate-factory-class-name", " io.vertx.core.logging.Log4j2LogDelegateFactory")
  }

  private fun deployVerticles() {
    vertx.deployVerticle(RouteServiceVerticle())
  }


  private fun calculateHandler(routingContext: RoutingContext) {

    val queryParams = routingContext.queryParams()
    val listOfQueryParams = listOf(
      Pair("waypoints", queryParams.getAll("waypoint").joinToString(separator = ";").removeSuffix(";")),
      Pair("preference", queryParams.get("preference")),
      Pair("method", queryParams.get("method")))
      .toMap()

    vertx.eventBus()
      .request<Buffer>(ROUTE_REQUEST_CONSUMER, JsonObject(listOfQueryParams).toBuffer()) { ar ->
        if (ar.succeeded()) {
          routingContext.response().putHeader("Content-Type", "application/json")
          routingContext.response().isChunked = true
          logger.log(Level.FINE, "Response written ${ar.result()}")
          routingContext.response().end(ar.result().body())
        } else {
          routingContext.response().statusCode = 500
          routingContext.response().end(ar.cause().message)
        }
      }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Launcher.executeCommand("run", MainVerticle::class.java.name)
    }
  }
}
