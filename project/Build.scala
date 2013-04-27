import sbt._
import Keys._
import Project._

object Build extends sbt.Build {


  import Dependencies._

  val name = "mongo-db-seeder"

  val appVersion = "0.2-SNAPSHOT"

  def buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.ee",
    scalaVersion := "2.9.2",
    version := appVersion,
    resolvers ++= Resolvers.commons,
    parallelExecution in Test := false,
    publishMavenStyle := true,
    publishTo <<= version {
      (v: String) =>
        def isSnapshot = v.trim.endsWith("SNAPSHOT")
        val finalPath = (if (isSnapshot) "/snapshots" else "/releases")
        Some(
          Resolver.sftp(
            "Ed Eustace",
            "edeustace.com",
            "/home/edeustace/edeustace.com/public/repository" + finalPath))
    },
    scalacOptions := Seq(
      "-deprecation",
      "-unchecked")
  )

  val lib = Project(name + "-lib", file("modules/lib"), settings = buildSettings)
    .settings(libraryDependencies ++= Seq(casbah, specs2))

  val plugin = Project(name + "-sbt", file("modules/sbt"), settings = buildSettings)
    .dependsOn(lib)
    .settings(sbtPlugin := true)

  //val example = Project(name + "-example", file("modules/example"), settings = buildSettings)
  //  .dependsOn(plugin, lib)


  val main = Project(name, base = file("."))
    .settings(
    version := appVersion,
    organization := "com.ee"
  )
    .dependsOn(lib, plugin)
    .aggregate(lib, plugin)
}