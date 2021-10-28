package co.ledger.cal.model

import io.circe.generic.JsonCodec

@JsonCodec case class Unit(name: String, code: String, magnitude: Long)
