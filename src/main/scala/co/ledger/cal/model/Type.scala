package co.ledger.cal.model

import doobie.postgres.implicits.pgEnumStringOpt
import doobie.util.meta.Meta
import enumeratum.EnumEntry.Lowercase
import enumeratum.{CirceEnum, Enum, EnumEntry}

object Type extends Enum[Type] with CirceEnum[Type] {
  val values: IndexedSeq[Type] = findValues

  case object Main extends Type

  case object Test extends Type

  implicit val chainMeta: Meta[Type] =
    pgEnumStringOpt("type", withNameOption, _.entryName)
}

sealed trait Type extends EnumEntry with Lowercase