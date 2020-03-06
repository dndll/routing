package dev.hekate.routing.verticle

import dev.hekate.routing.PACKAGE_NAME
import dev.hekate.routing.SETTINGS_MAP
import io.vertx.core.AbstractVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json
import io.vertx.ext.web.client.WebClient
import java.util.logging.Level
import java.util.logging.Logger

const val ROUTE_REQUEST_CONSUMER = "$PACKAGE_NAME.RouteRequest"

class RouteServiceVerticle : AbstractVerticle() {
  private val logger: Logger = Logger.getLogger(this::class.java.name)

  override fun start() {
    logger.log(Level.INFO, "Deployed ${this::class.java.name}")
    val webClient = WebClient.create(vertx)

    vertx.eventBus()
      .consumer<Buffer>(ROUTE_REQUEST_CONSUMER)
      .handler { message: Message<Buffer> ->
        val params = Json.decodeValue(message.body(), Map::class.java) as Map<*, *>
        logger.info("Params: $params")

        val profile = if (params["preference"] == "fastest" || params["preference"] == "shortest") "route" else "trip"
        logger.info("Profile: $profile")
        logger.info("OSRM URI: ${SETTINGS_MAP["OSRM_HOST"]}/$profile/v1/car/${params["waypoints"]}")

        val request = when (val osrmHost = SETTINGS_MAP["OSRM_HOST"]) {
          "router.project-osrm.org" -> webClient
            .get(osrmHost, "/$profile/v1/car/${params["waypoints"]}") //OSRM_URL
            .addQueryParam("geometries", "polyline")
            .addQueryParam("steps", "true")
          else -> webClient
            .get(osrmHost, "/$profile/v1/car/${params["waypoints"]}") //OSRM_URL
            .port(SETTINGS_MAP["OSRM_PORT"]?.toInt() ?: 5000)
            .addQueryParam("geometries", "polyline")
            .addQueryParam("steps", "true")
        }

        logger.info(request.copy().toString())
        request.send { ar ->
          if (ar.succeeded()) {
            logger.info("${ar.result()}")
            message.reply(ar.result().body())
          } else {
            logger.severe(ar.cause().message)
            message.fail(500, ar.cause().message)
          }
        }
      }
    super.start()
  }

  override fun stop() {
    logger.log(Level.SEVERE, "Stopped RouteServiceVerticle")
    super.stop()
  }

}
