package co.ledger.cal.model

import doobie.Get
import doobie.util.Put
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.Schema

case class Unit(name: String, code: String, magnitude: Long)

object Unit {
  implicit def decoder: Decoder[Unit] = deriveDecoder[Unit]
  implicit def encoder: Encoder[Unit] = deriveEncoder[Unit]
  implicit val get: Get[Unit]         = getFromDecoder
  implicit val put: Put[Unit]         = putFromEncoder
  implicit val schema: Schema[Unit] = Schema.derived
}
