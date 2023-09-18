ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "syntropy-pubsub-scala"
  )

libraryDependencies += "io.nats" % "jnats" % "2.16.14"
libraryDependencies += "net.i2p.crypto" % "eddsa" % "0.3.0"
libraryDependencies += "com.google.code.gson" % "gson" % "2.10.1"
