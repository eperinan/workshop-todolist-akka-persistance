import com.trueaccord.scalapb.compiler.Version.scalapbVersion

lazy val Versions = new {
  val akkaHttpVersion = "10.0.9"
  val akkaVersion = "2.5.3"
  val scalatest = "3.0.1"
  val leveldb = "0.7"
  val leveldbjni = "1.8"
  val circe = "0.7.0"
  val httpCirce = "1.17.0"
}

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.todolist",
      scalaVersion := "2.12.3",
      version := "0.0.1"
    )),
    name := "todolist-akka-training",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % Versions.akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml" % Versions.akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % Versions.akkaVersion,
      "com.typesafe.akka" %% "akka-persistence" % Versions.akkaVersion,
      "com.typesafe.akka" %% "akka-persistence-query" % Versions.akkaVersion,
      "org.iq80.leveldb" % "leveldb" % Versions.leveldb,
      "org.fusesource.leveldbjni" % "leveldbjni-all" % Versions.leveldbjni,
      "com.typesafe.akka" %% "akka-remote" % Versions.akkaVersion,
      "io.circe" %% "circe-core" % Versions.circe,
      "io.circe" %% "circe-java8" % Versions.circe,
      "io.circe" %% "circe-parser" % Versions.circe,
      "de.heikoseeberger" %% "akka-http-circe" % Versions.httpCirce,
      "io.circe" %% "circe-generic-extras" % Versions.circe,
      "com.trueaccord.scalapb" %% "scalapb-runtime" % scalapbVersion % "protobuf",
      "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttpVersion % Test,
      "org.scalatest" %% "scalatest" % Versions.scalatest % Test
    )
  )

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)