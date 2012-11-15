import sbt._
import Keys._

import com.ee.seeder.MongoDbSeeder


object MongoDbSeederPlugin extends Plugin
{
    val seedDevTask = TaskKey[Unit]("seed-dev")
    val seedTestTask = TaskKey[Unit]("seed-test")
    val seedProdTask = TaskKey[Unit]("seed-prod")


    val testUri = SettingKey[String]("test-uri")
    val testPaths = SettingKey[String]("test-paths")

    val devUri = SettingKey[String]("dev-uri")
    val devPaths = SettingKey[String]("dev-paths")

    def seed(uri:String,path:String,name:String) {
      println("[mongo-db-seeder-sbt]")
      val finalUri = uri.format(name)
      println("using uri: " + finalUri)
      println("from path: " + path)
      val paths = path.split(",").toList
      
      try{
        MongoDbSeeder.seed(finalUri, paths.toList)
      }
      catch {
        case e : IllegalArgumentException => println("Something went wrong: " + e.getMessage)
        case e : Throwable => println("unknown error: " + e.getMessage)
      }
    }

    def defaultUri(key:String) = "mongodb://localhost:27017/%s-" + key


    val newSettings = Seq(
      testUri := defaultUri("test"),
      testPaths := "seed/test",
      seedTestTask <<= (testUri, testPaths, name ) map(seed),
      devUri := defaultUri("dev"),
      devPaths := ("seed/dev"),
      seedDevTask <<= (devUri, devPaths, name ) map(seed)
      )
}
