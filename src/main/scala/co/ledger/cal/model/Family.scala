package co.ledger.cal.model

import doobie.postgres.implicits.pgEnumStringOpt
import doobie.util.meta.Meta
import enumeratum.{CirceEnum, Enum, EnumEntry}
import enumeratum.EnumEntry.Lowercase


sealed trait Family extends EnumEntry with Lowercase

object Family extends Enum[Family] with CirceEnum[Family] {
  val values: IndexedSeq[Family] = findValues

  case object Bitcoin extends Family
  case object Ethereum extends Family
  case object Ripple extends Family

  implicit val chainMeta: Meta[Family] =
    pgEnumStringOpt("family", withNameOption, _.entryName)
}

