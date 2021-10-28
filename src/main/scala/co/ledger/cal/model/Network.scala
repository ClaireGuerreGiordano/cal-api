package co.ledger.cal.model

import io.circe.generic.JsonCodec

@JsonCodec case class Network(`type`: Type, blockchain_name: String)


