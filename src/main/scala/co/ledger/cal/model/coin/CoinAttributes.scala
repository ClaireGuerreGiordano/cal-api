package co.ledger.cal.model.coin

import cats.data.NonEmptyList
import co.ledger.cal.model.getFromDecoder
import co.ledger.cal.model.putFromEncoder
import doobie.Get
import doobie.util.Put
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema

case class CoinAttributes(
    coin_type: Int,
    has_segwit: Boolean,
    has_tokens: Boolean,
    units: NonEmptyList[Unit],
    networks: NonEmptyList[Network]
)

object CoinAttributes {

  implicit val codec: Codec[CoinAttributes]   = deriveCodec
  implicit val schema: Schema[CoinAttributes] = Schema.derived
  val example: CoinAttributes = CoinAttributes(
    0,
    has_segwit = true,
    has_tokens = false,
    NonEmptyList.one(Unit("name", "code", 0L)),
    NonEmptyList.one(Network(Type.Main, "blockchain_name"))
  )
  implicit val get: Get[Unit] = getFromDecoder
  implicit val put: Put[Unit] = putFromEncoder
}
