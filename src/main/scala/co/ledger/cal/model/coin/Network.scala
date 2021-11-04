package co.ledger.cal.model.coin

import co.ledger.cal.model.getFromDecoder
import co.ledger.cal.model.putFromEncoder
import doobie.Get
import doobie.util.Put
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import sttp.tapir.Schema

case class Network(`type`: Type, blockchain_name: String)

object Network {
  implicit def decoder: Decoder[Network] = deriveDecoder[Network]
  implicit def encoder: Encoder[Network] = deriveEncoder[Network]
  implicit val get: Get[Network]         = getFromDecoder
  implicit val put: Put[Network]         = putFromEncoder
  implicit val schema: Schema[Network]   = Schema.derived
}
