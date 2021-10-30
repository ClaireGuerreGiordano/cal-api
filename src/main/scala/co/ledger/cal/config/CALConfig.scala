package co.ledger.cal.config

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

case class CALConfig(
                      database: DatabaseConfig
                    )
object CALConfig {
  implicit val ConfigReader: ConfigReader[CALConfig] = deriveReader
}
