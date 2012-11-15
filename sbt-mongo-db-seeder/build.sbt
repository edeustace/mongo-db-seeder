sbtPlugin := true

name := "mongo-db-seeder-sbt"

version := "0.1"

organization := "com.ee"

libraryDependencies ++= Seq(
  "com.ee" %% "mongo-db-seeder" % "0.1" ,
  "org.specs2" %% "specs2" % "1.12.2" % "test"
  )

