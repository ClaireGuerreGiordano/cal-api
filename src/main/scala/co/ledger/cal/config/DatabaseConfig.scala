package co.ledger.cal.config

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader
import eu.timepit.refined.pureconfig._

final case class DatabaseConfig(
    driver: JDBCDriverName,
    url: JDBCUrl,
    user: JDBCUsername,
    pass: JDBCPassword
)

object DatabaseConfig {
  implicit val configReader: ConfigReader[DatabaseConfig] = deriveReader[DatabaseConfig]
}
