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
      scalaVersion := "2.9.1",
      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
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
