package co.ledger.cal.repository.model

import co.ledger.cal.model.Coin
import co.ledger.cal.model.Family
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class CoinDTO(
    ticker: String,
    name: String,
    symbol: String,
    family: Family,
    attributes: CoinAttributes
) {
  def to: Coin = Coin(
    ticker,
    name,
    symbol,
    family,
    attributes.coin_type,
    has_segwit = attributes.has_segwit,
    has_tokens = attributes.has_tokens,
    attributes.units,
    attributes.networks
  )
}

object CoinDTO {
  implicit val codec: Codec[CoinDTO] = deriveCodec
  def from(coin: Coin): CoinDTO = CoinDTO(
    coin.ticker,
    coin.name,
    coin.symbol,
    coin.family,
    CoinAttributes(
      coin.coin_type,
      has_segwit = coin.has_segwit,
      has_tokens = coin.has_tokens,
      coin.units,
      coin.networks
    )
  )
}
