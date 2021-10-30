package co.ledger.cal.repository

import better.files.File
import cats.effect.{ContextShift, IO, Timer}
import co.ledger.cal.config.{CALConfig, DatabaseConfig}
import com.typesafe.config.{Config, ConfigFactory}
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import org.postgresql.ds.PGSimpleDataSource
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Outcome}
import org.scalatest.flatspec.AnyFlatSpec
import pureconfig.ConfigSource
import eu.timepit.refined.auto._
import org.scalatest.matchers.should.Matchers

import java.nio.file.Paths
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

trait Fixture extends AnyFlatSpec with BeforeAndAfterEach with BeforeAndAfterAll with IOChecker with Matchers {

  val zippedCoinsFolder: File = Paths.get("src/it/resources/coins.zip")

  implicit protected def timer: Timer[IO] = IO.timer(global)

  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  val config: Config                = ConfigFactory.load(getClass.getClassLoader)
  private val calConfig: CALConfig  = ConfigSource.fromConfig(config).loadOrThrow[CALConfig]
  val dbConfig: DatabaseConfig      = calConfig.database

  val transactor: Transactor[IO] = Transactor.fromDriverManager[IO](
    dbConfig.driver,
    dbConfig.url,
    dbConfig.user,
    dbConfig.pass
  )

  val datasource: PGSimpleDataSource = new PGSimpleDataSource
  datasource.setURL(dbConfig.url)
  datasource.setUser(dbConfig.user)
  datasource.setPassword(dbConfig.pass)

  lazy val flyway: Flyway = Flyway
    .configure()
    .dataSource(datasource)
    .locations(s"classpath:/db")
    .load

  val coinRepository: PostgresCoinRepository = new PostgresCoinRepository(transactor)

  val blockingPool: ExecutorService       = Executors.newFixedThreadPool(2)
  val ec: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(blockingPool)

  override def withFixture(test: NoArgTest): Outcome = {
    try {
      flyway.migrate()
      val outcome = super.withFixture(test)
      outcome
    } finally {
      flyway.clean()
      ()
    }
  }

  override protected def afterAll(): Unit = ()

  override protected def beforeEach(): Unit = {
    flyway.clean()
    ()
  }
}

