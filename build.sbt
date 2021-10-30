// *****************************************************************************
// Projects
// *****************************************************************************

lazy val cal =
  project
    .in(file("."))
    .enablePlugins(BuildInfoPlugin, JavaServerAppPackaging, JavaAgent, DockerPlugin)
    .configs(IntegrationTest)
    .settings(
      organization := "co.ledger",
      name         := "cal",
      scalaVersion := "2.13.6",
      scalacOptions += "-Ymacro-annotations",
      buildInfoKeys    := Seq[BuildInfoKey](name, version),
      buildInfoPackage := "co.ledger.cal",
      Defaults.itSettings,
      Test / fork                         := true,
      IntegrationTest / fork              := true,
      IntegrationTest / parallelExecution := false,
      addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
      addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
      libraryDependencies ++= Seq(
        library.awskms,
        library.catsCore,
        library.circeCore,
        library.circeGeneric,
        library.circeRefined,
        library.circeParser,
        library.doobieCore,
        library.doobieHikari,
        library.doobiePostgres,
        library.doobieRefined,
        library.enumeratumCirce,
        library.flywayCore,
        library.http4sCirce,
        library.http4sDsl,
        library.http4sBlazeClient,
        library.http4sEmberServer,
        library.logback,
        library.newtype,
        library.postgresql,
        library.pureConfig,
        library.refinedCats,
        library.refinedCore,
        library.refinedPureConfig,
        library.scodecBits,
        library.tapirCats,
        library.tapirCirce,
        library.tapirCore,
        library.tapirEnumeratum,
        library.tapirHttp4s,
        library.tapirOpenApiDocs,
        library.tapirOpenApiYaml,
        library.tapirRefined,
        library.tapirSwaggerUi,
        library.enumeratum,
        library.circuit,
        library.scalaLogging,
        library.betterFiles,
        library.sttpClient,
        library.munit             % IntegrationTest,
        library.munitCatsEffect   % IntegrationTest,
        library.munitScalaCheck   % IntegrationTest,
        library.refinedScalaCheck % IntegrationTest,
        library.scalaCheck        % IntegrationTest,
        library.scalaTest         % IntegrationTest,
        library.doobieScalaTest   % IntegrationTest,
        library.munit             % Test,
        library.munitCatsEffect   % Test,
        library.munitScalaCheck   % Test,
        library.refinedScalaCheck % Test,
        library.scalaCheck        % Test
      ),
      ThisBuild / dynverSeparator  := "-",
      dockerBaseImage              := "openjdk:11-slim",
      dockerRepository             := Some("ghcr.io/ledgerhq"),
      dockerUpdateLatest           := true,
      dockerExposedPorts           := Seq(8080),
      javaAgents += "com.datadoghq" % "dd-java-agent" % "0.89.0"
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val awssdk          = "2.17.69"
      val cats            = "2.6.1"
      val circe           = "0.14.1"
      val doobie          = "0.13.4"
      val enumeratum      = "1.7.0"
      val flyway          = "8.0.2"
      val http4s          = "0.22.7"
      val logback         = "1.2.6"
      val munit           = "0.7.29"
      val munitCatsEffect = "1.0.6"
      val newtype         = "0.4.4"
      val postgresql      = "42.3.0"
      val pureConfig      = "0.17.0"
      val refined         = "0.9.27"
      val scalaCheck      = "1.15.4"
      val tapir           = "0.18.3"
      val scalaTest       = "3.2.10"
      val circuit         = "0.4.4"
      val scalaLogging    = "3.9.4"
      val scodecBits      = "1.1.29"
      val betterFiles     = "3.9.1"
      val sttpClient      = "3.3.16"
    }
    val awskms            = "software.amazon.awssdk" % "kms"                 % Version.awssdk
    val catsCore          = "org.typelevel"         %% "cats-core"           % Version.cats
    val circeCore         = "io.circe"              %% "circe-core"          % Version.circe
    val circeGeneric      = "io.circe"              %% "circe-generic"       % Version.circe
    val circeRefined      = "io.circe"              %% "circe-refined"       % Version.circe
    val circeParser       = "io.circe"              %% "circe-parser"        % Version.circe
    val doobieCore        = "org.tpolecat"          %% "doobie-core"         % Version.doobie
    val doobieHikari      = "org.tpolecat"          %% "doobie-hikari"       % Version.doobie
    val doobiePostgres    = "org.tpolecat"          %% "doobie-postgres"     % Version.doobie
    val doobieRefined     = "org.tpolecat"          %% "doobie-refined"      % Version.doobie
    val doobieScalaTest   = "org.tpolecat"          %% "doobie-scalatest"    % Version.doobie
    val enumeratum        = "com.beachape"          %% "enumeratum"          % Version.enumeratum
    val enumeratumCirce   = "com.beachape"          %% "enumeratum-circe"    % Version.enumeratum
    val flywayCore        = "org.flywaydb"           % "flyway-core"         % Version.flyway
    val http4sCirce       = "org.http4s"            %% "http4s-circe"        % Version.http4s
    val http4sDsl         = "org.http4s"            %% "http4s-dsl"          % Version.http4s
    val http4sEmberServer = "org.http4s"            %% "http4s-ember-server" % Version.http4s
    val http4sBlazeClient = "org.http4s"            %% "http4s-blaze-client" % Version.http4s
    val logback           = "ch.qos.logback"         % "logback-classic"     % Version.logback
    val munit             = "org.scalameta"         %% "munit"               % Version.munit
    val munitCatsEffect = "org.typelevel"         %% "munit-cats-effect-2" % Version.munitCatsEffect
    val munitScalaCheck = "org.scalameta"         %% "munit-scalacheck"    % Version.munit
    val newtype         = "io.estatico"           %% "newtype"             % Version.newtype
    val postgresql      = "org.postgresql"         % "postgresql"          % Version.postgresql
    val pureConfig      = "com.github.pureconfig" %% "pureconfig"          % Version.pureConfig
    val refinedCore     = "eu.timepit"            %% "refined"             % Version.refined
    val refinedCats     = "eu.timepit"            %% "refined-cats"        % Version.refined
    val refinedPureConfig = "eu.timepit"     %% "refined-pureconfig" % Version.refined
    val refinedScalaCheck = "eu.timepit"     %% "refined-scalacheck" % Version.refined
    val scalaCheck        = "org.scalacheck" %% "scalacheck"         % Version.scalaCheck
    val scalaTest         = "org.scalatest"  %% "scalatest"          % Version.scalaTest
    val scodecBits        = "org.scodec"     %% "scodec-bits"        % Version.scodecBits
    val tapirCats        = "com.softwaremill.sttp.tapir" %% "tapir-cats"          % Version.tapir
    val tapirCirce       = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"    % Version.tapir
    val tapirCore        = "com.softwaremill.sttp.tapir" %% "tapir-core"          % Version.tapir
    val tapirEnumeratum  = "com.softwaremill.sttp.tapir" %% "tapir-enumeratum"    % Version.tapir
    val tapirHttp4s      = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % Version.tapir
    val tapirOpenApiDocs = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"  % Version.tapir
    val tapirOpenApiYaml =
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir
    val tapirRefined   = "com.softwaremill.sttp.tapir" %% "tapir-refined"           % Version.tapir
    val tapirSwaggerUi = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % Version.tapir
    val circuit      = "io.chrisdavenport"          %% "circuit"       % Version.circuit
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % Version.scalaLogging
    val betterFiles = "com.github.pathikrit" %% "better-files" % Version.betterFiles
    val sttpClient = "com.softwaremill.sttp.client3" %% "core" % Version.sttpClient
  }
