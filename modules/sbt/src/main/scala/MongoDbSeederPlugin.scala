import sbt._
import Keys._

import com.ee.seeder.MongoDbSeeder

object MongoDbSeederPlugin extends Plugin with com.ee.seeder.log.ConsoleLogger {
  val seedDevTask = TaskKey[Unit]("seed-dev")
  val seedTestTask = TaskKey[Unit]("seed-test")
  val seedProdTask = TaskKey[Unit]("seed-prod")

  val unSeedDevTask = TaskKey[Unit]("unseed-dev")
  val unSeedTestTask = TaskKey[Unit]("unseed-test")
  val unSeedProdTask = TaskKey[Unit]("unseed-prod")

  val testUri = SettingKey[String]("test-uri")
  val testPaths = SettingKey[String]("test-paths")
  val testClear = SettingKey[Boolean]("test-clear")

  val devUri = SettingKey[String]("dev-uri")
  val devPaths = SettingKey[String]("dev-paths")
  val devClear = SettingKey[Boolean]("dev-clear")

  val logLevel = SettingKey[String]("log-level")

  def seed(uri: String, path: String, name: String, logLevel: String, clear: Boolean) {
    MongoDbSeeder.logLevel = com.ee.seeder.log.ConsoleLogger.Level.withName(logLevel)
    run("seed", uri, path, (uri:String, paths:List[String]) => MongoDbSeeder.seed(uri,paths,clear))
  }

  def unseed(uri: String, path: String, name: String, logLevel: String) {
    MongoDbSeeder.logLevel = com.ee.seeder.log.ConsoleLogger.Level.withName(logLevel)
    run("unseed", uri, path, MongoDbSeeder.unseed)
  }

  private def run(prefix: String, uri: String, path: String, fn: (String, List[String]) => Unit) {
    debug(prefix)
    val finalUri = uri.format(name)
    info("using uri: " + finalUri + " from path: " + path)
    val paths = path.split(",").toList

    try {
      fn(finalUri, paths.toList)
    } catch {
      case e: IllegalArgumentException => error("Something went wrong: " + e.getMessage)
      case e: Throwable => error("unknown error: " + e.getMessage)
    }
  }

  def defaultUri(key: String) : String = "mongodb://localhost:27017/%s-" + key

  val newSettings = Seq(
    testUri := defaultUri("test"),
    testPaths := "seed/test",
    testClear := true,
    seedTestTask <<= (testUri, testPaths, name, logLevel, testClear) map (seed),
    unSeedTestTask <<= (testUri, testPaths, name, logLevel) map (unseed),

    devUri := defaultUri("dev"),
    logLevel := "OFF",
    devPaths := "seed/dev",
    devClear := true,
    seedDevTask <<= (devUri, devPaths, name, logLevel, devClear) map (seed),
    unSeedDevTask <<= (devUri, devPaths, name, logLevel) map (unseed))
}
