import sbt._
import Keys._
import Project._

object Build extends sbt.Build {

  import Dependencies._

  val name = "mongo-db-seeder"

  val baseVersion = "0.5"

  lazy val appVersion = {
    val other = Process("git rev-parse --short HEAD").lines.head
    baseVersion + "-" + other
  }

  def buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.ee",
    scalaVersion := "2.10.1",
    crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.0"),
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

  val main = Project(name, base = file("."), settings = buildSettings )
    .dependsOn(lib, plugin)
    .aggregate(lib, plugin)
}