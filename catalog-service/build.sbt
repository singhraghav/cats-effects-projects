import Dependencies.*

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val allSourceDependencies = List(
  CompilerPlugins.zerowaste,
  Libraries.cats,
  Libraries.catsEffect,
  Libraries.circeCore,
  Libraries.circeParser
)

lazy val root = (project in file("."))
  .settings(
    name                := "catalog-service",
    logo                := "Catalog-Service",
    libraryDependencies := allSourceDependencies
  )
