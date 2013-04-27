import sbt._
import sbt.Keys._

object SbtMongoDbSeederBuild extends Build {

  lazy val sbtMongoDbSeeder = Project(
    id = "mongo-db-seeder-sbt",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "Mongo Db Seeder Sbt",
      organization := "com.ee",
      version := "0.1",
      scalaVersion := "2.9.2",
      publishMavenStyle := true,
      publishTo <<= version { (v: String) =>
        def isSnapshot = v.trim.endsWith("SNAPSHOT")
        val finalPath = (if (isSnapshot) "/snapshots" else "/releases")
        Some(
          Resolver.sftp(
            "Ed Eustace",
            "edeustace.com",
            "/home/edeustace/edeustace.com/public/repository" + finalPath ))
       }
    )
  )
}
