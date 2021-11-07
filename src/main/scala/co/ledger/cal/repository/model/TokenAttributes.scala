package co.ledger.cal.repository.model

import co.ledger.cal.model.getFromDecoder
import co.ledger.cal.model.putFromEncoder
import doobie.Get
import doobie.Put
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema

case class TokenAttributes(
    symbol: Option[String],
    decimals: Int,
    totalSupply: Option[BigDecimal],
    disableCountervalue: Boolean = false,
    delisted: Boolean = false
)

object TokenAttributes {
  implicit val codec: Codec[TokenAttributes]   = deriveCodec
  implicit val schema: Schema[TokenAttributes] = Schema.derived
  val example: TokenAttributes = TokenAttributes(
    Some("symbol"),
    1,
    Some(BigDecimal(10)),
    disableCountervalue = true,
    delisted = true
  )
  implicit val get: Get[TokenAttributes] = getFromDecoder
  implicit val put: Put[TokenAttributes] = putFromEncoder
}
