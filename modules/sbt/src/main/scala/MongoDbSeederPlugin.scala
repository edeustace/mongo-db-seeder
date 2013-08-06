import sbt._
import Keys._

import com.ee.seeder.MongoDbSeeder


object MongoDbSeederPlugin extends Plugin
{
    val seedDevTask = TaskKey[Unit]("seed-dev")
    val seedTestTask = TaskKey[Unit]("seed-test")
    val seedProdTask = TaskKey[Unit]("seed-prod")

    val unSeedDevTask = TaskKey[Unit]("unseed-dev")
    val unSeedTestTask = TaskKey[Unit]("unseed-test")
    val unSeedProdTask = TaskKey[Unit]("unseed-prod")

    val testUri = SettingKey[String]("test-uri")
    val testPaths = SettingKey[String]("test-paths")

    val devUri = SettingKey[String]("dev-uri")
    val devPaths = SettingKey[String]("dev-paths")

    def seed(uri:String,path:String,name:String) {
      run("seed",uri,path, MongoDbSeeder.seed)
    }

    def unseed(uri:String,path:String,name:String) {
      run("unseed",uri,path, MongoDbSeeder.unseed)
    }

    private def run(prefix:String, uri:String, path:String, fn:(String,List[String]) => Unit){
      println("[mongo-db-seeder-sbt] " + prefix)
      val finalUri = uri.format(name)
      println("using uri: " + finalUri)
      println("from path: " + path)
      val paths = path.split(",").toList

      try{
        fn(finalUri, paths.toList)
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
      unSeedTestTask <<= (testUri, testPaths, name) map(unseed),

      devUri := defaultUri("dev"),
      devPaths := ("seed/dev"),
      seedDevTask <<= (devUri, devPaths, name ) map(seed),
      unSeedDevTask <<= (devUri, devPaths, name) map(unseed)
      )
}
