package co.ledger.cal.repository.model

import co.ledger.cal.Types.HexStr
import co.ledger.cal.model.Token
import io.circe.Codec
import io.circe.generic.semiauto._

case class TokenDTO(
    ticker: String,
    name: String,
    blockchainName: String,
    contractAddress: String,
    attributes: TokenAttributes
) {
  def to: Token = Token(
    ticker,
    name,
    blockchainName,
    HexStr.unsafeFrom(contractAddress),
    attributes.symbol,
    attributes.decimals,
    attributes.totalSupply,
    attributes.disableCountervalue,
    attributes.delisted
  )
}

object TokenDTO {
  implicit val codec: Codec[TokenDTO] = deriveCodec
  def from(token: Token): TokenDTO = TokenDTO(
    token.ticker,
    token.name,
    token.blockchainName,
    token.contractAddress.value,
    TokenAttributes(
      token.symbol,
      token.decimals,
      token.totalSupply,
      token.disableCountervalue,
      token.delisted
    )
  )
}
