scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.mongodb" %% "casbah" % "2.4.1",
  "org.specs2" %% "specs2" % "1.12.2" % "test"
  )

resolvers ++= Seq(
  "Sbt plugins" at "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/",
  "Sonatype OSS" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "mvn repo" at "http://repo1.maven.org/maven2/"
)
