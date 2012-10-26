import sbt._
import sbt.Keys._

object ExamplemongodbseederBuild extends Build {

  lazy val examplemongodbseeder = Project(
    id = "example-mongo-db-seeder",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "example-mongo-db-seeder",
      organization := "com.ee",
      version := "0.1",
      scalaVersion := "2.9.1"
      // add other settings here
    )
  )
}
