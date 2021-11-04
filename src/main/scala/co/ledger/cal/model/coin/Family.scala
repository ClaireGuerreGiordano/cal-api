package co.ledger.cal.model.coin

import doobie.postgres.implicits.pgEnumStringOpt
import doobie.util.meta.Meta
import enumeratum.CirceEnum
import enumeratum.Enum
import enumeratum.EnumEntry
import enumeratum.EnumEntry.Lowercase
import sttp.tapir
import sttp.tapir.CodecFormat.TextPlain

sealed trait Family extends EnumEntry with Lowercase

object Family extends Enum[Family] with CirceEnum[Family] {
  val values: IndexedSeq[Family] = findValues

  case object Bitcoin  extends Family
  case object Ethereum extends Family
  case object Ripple   extends Family

  implicit val meta: Meta[Family] =
    pgEnumStringOpt("family", withNameOption, _.entryName)

  implicit val tapirCodec: tapir.Codec[String, Family, TextPlain] =
    tapir.Codec.string.mapDecode(tapirCodec.decode)(_.entryName)
  implicit val schema: tapir.Schema[Family] = tapirCodec.schema

}
