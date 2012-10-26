import sbt._
import sbt.Keys._

object MongoDbSeederBuild extends Build {

  lazy val mongoDbSeeder = Project(
    id = "mongo-db-seeder",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "Mongo Db Seeder",
      organization := "com.ee",
      version := "0.1",
      scalaVersion := "2.9.1"
      // add other settings here
    )
  )
}
