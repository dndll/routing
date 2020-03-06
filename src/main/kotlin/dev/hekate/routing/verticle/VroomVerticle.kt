//package dev.hekate.routing.verticle
//
//import dev.hekate.routing.PACKAGE_NAME
//import io.vertx.core.AbstractVerticle
//import io.vertx.core.buffer.Buffer
//import java.nio.charset.Charset
//import java.util.logging.Level
//import java.util.logging.Logger
//
//const val VROOM_CONSUMER = "$PACKAGE_NAME"
//
//class VroomVerticle : AbstractVerticle() {
//  private val logger: Logger = Logger.getLogger("VroomVerticle")
//
//  override fun start() {
//    logger.log(Level.INFO, "Deployed VroomVerticle")
//    vertx.eventBus().consumer<Buffer>(VROOM_CONSUMER).handler { message ->
//      Observable.just(ProcessBuilder("/Users/donovan/tools/vroom", "-g", message.toString())
//        .start()
//        .inputStream
//        .bufferedReader(Charset.defaultCharset()))
//        .subscribe { br ->
//          br.use {
//            logger.info(it.readText())
//          }
//
//        }
//
//
//    }
//    super.start()
//  }
//
//  override fun stop() {
//    logger.log(Level.SEVERE, "Stopped RouteServiceVerticle")
//    super.stop()
//  }
//
//  private fun vroomRequestHandler() {
//
//  }
//}
