package co.ledger.cal

import cats.effect.Blocker
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import cats.effect.SyncIO
import cats.implicits.catsSyntaxFlatMapOps
import cats.implicits.toSemigroupKOps
import co.ledger.cal.api.CALRoutes
import co.ledger.cal.config.CALConfig
import co.ledger.cal.repository.FlywayDatabaseMigrator
import co.ledger.cal.repository.PostgresCoinRepository
import co.ledger.cal.repository.PostgresTokenRepository
import co.ledger.cal.service.CoinService
import co.ledger.cal.service.TokenService
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import doobie.util.transactor.Transactor
import pureconfig.ConfigSource
import eu.timepit.refined.auto._
import eu.timepit.refined.internal.BuildInfo
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.CORS
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object CALServer extends IOApp.WithContext with StrictLogging {
  val blockingPool: ExecutorService = Executors.newFixedThreadPool(2)
  override protected def executionContextResource: Resource[SyncIO, ExecutionContext] =
    Resource.eval(SyncIO(ExecutionContext.global))

  override def run(args: List[String]): IO[ExitCode] = {
    val blocker = Blocker.liftExecutorService(blockingPool)
    val serverResource = for {
      config <- IO(ConfigFactory.load(getClass.getClassLoader))
      config <- IO(ConfigSource.fromConfig(config).loadOrThrow[CALConfig])
      _      <- FlywayDatabaseMigrator.migrate(config.database)
      transactor = Transactor.fromDriverManager[IO](
        config.database.driver,
        config.database.url,
        config.database.user,
        config.database.pass
      )
      coinRepo     = new PostgresCoinRepository(transactor)
      tokenRepo    = new PostgresTokenRepository(transactor)
      coinService  = CoinService(coinRepo)
      tokenService = TokenService(tokenRepo)
      calRoutes    = new CALRoutes(coinService, tokenService)
      docs = OpenAPIDocsInterpreter().toOpenAPI(
        CALRoutes.endpoints,
        BuildInfo.name,
        BuildInfo.version
      )
      swagger = new SwaggerHttp4s(docs.toYaml)
      httpApp = CORS.policy {
        (calRoutes.routes <+> swagger.routes[IO]).orNotFound
      }
      serverBuilder = EmberServerBuilder
        .default[IO]
        .withBlocker(blocker)
        .withHost("0.0.0.0")
        .withPort(8080)
        .withHttpApp(httpApp)
        .build
    } yield serverBuilder

    serverResource.flatMap(res =>
      res.use(server =>
        IO.delay(logger.info("Server started at {}", server.address)) >> IO.never.as(
          ExitCode.Success
        )
      )
    )
  }
}
