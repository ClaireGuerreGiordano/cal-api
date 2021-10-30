package co.ledger.cal.repository

import cats.effect.IO
import co.ledger.cal.config.DatabaseConfig
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import eu.timepit.refined.auto._

object FlywayDatabaseMigrator {

  def migrate(config: DatabaseConfig): IO[MigrateResult] =
    IO {
      val flyway: Flyway =
        Flyway.configure().dataSource(config.url, config.user, config.pass).load()
      flyway.migrate()
    }

}
