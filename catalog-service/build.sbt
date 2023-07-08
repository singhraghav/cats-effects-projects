import Dependencies.*

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val allSourceDependencies = List(
  CompilerPlugins.zerowaste,
  Libraries.cats,
  Libraries.catsEffect,
  Libraries.circeCore,
  Libraries.circeParser,
  Libraries.circeGeneric,
  Libraries.http4sDsl,
  Libraries.http4sCirce,
  Libraries.http4sServer,
  Libraries.log4cats,
  Libraries.logback % Runtime,
  Libraries.doobieCore,
  Libraries.doobiePostgres,
  Libraries.doobieHikari,
  Libraries.purConfigCore,
  Libraries.pureConfigCatsEffect
)

// This helps in Automatically deriving encoder and decoder for circe
scalacOptions += "-Ymacro-annotations"

lazy val root = (project in file("."))
  .settings(
    name                := "catalog-service",
    logo                := "Catalog-Service",
    libraryDependencies := allSourceDependencies
  )
