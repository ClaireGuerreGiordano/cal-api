package co.ledger.cal.model

import doobie.postgres.implicits.pgEnumStringOpt
import doobie.util.meta.Meta
import enumeratum.EnumEntry.Lowercase
import enumeratum.CirceEnum
import enumeratum.Enum
import enumeratum.EnumEntry
import sttp.tapir
import sttp.tapir.CodecFormat.TextPlain

sealed trait Type extends EnumEntry with Lowercase

object Type extends Enum[Type] with CirceEnum[Type] {
  val values: IndexedSeq[Type] = findValues

  case object Main extends Type

  case object Test extends Type

  implicit val meta: Meta[Type] =
    pgEnumStringOpt("network_type", withNameOption, _.entryName)

  implicit val tapirCodec: tapir.Codec[String, Type, TextPlain] =
    tapir.Codec.string.mapDecode(tapirCodec.decode)(_.entryName)
  implicit val schema: tapir.Schema[Type] = tapirCodec.schema
}
