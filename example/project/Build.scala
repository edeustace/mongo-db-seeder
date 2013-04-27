import sbt._
import Keys._

object Build extends sbt.Build{
  lazy val main = Project(
    id = "example-mongo-db-seeder",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "mongo-db-seeder-example",
      organization := "com.ee",
      version := "0.1",
      scalaVersion := "2.9.2"
    )
  )

}