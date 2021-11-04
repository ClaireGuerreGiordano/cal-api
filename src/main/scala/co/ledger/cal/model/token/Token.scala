package co.ledger.cal.model.token

import cats.data.ValidatedNel
import cats.implicits.catsSyntaxValidatedId
import io.circe.Codec
import io.circe.generic.JsonCodec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema

@JsonCodec case class NonValidatedToken(
    ticker: String,
    name: String,
    symbol: Option[String],
    blockchainName: String,
    contractAddress: Contract.Address,
    decimals: Int,
    totalSupply: Option[BigInt],
    disableCountervalue: Boolean = false,
    delisted: Boolean = false
)

final case class Token(
    ticker: String,
    name: String,
    symbol: Option[String],
    blockchainName: String,
    contractAddress: Contract.Address,
    decimals: Int,
    totalSupply: Option[BigInt],
    disableCountervalue: Boolean = false,
    delisted: Boolean = false
)
object Token {
  implicit val codec: Codec[Token]   = deriveCodec
  implicit val schema: Schema[Token] = Schema.derived
  val example: Token =
    Token("ticker", "name", Some("symbol"), "blockchainName", Contract.Address("0x1"), 10, None)
}

trait TokenValidator {

  type ValidationResult[A] = ValidatedNel[String, A]

  private def validateTicker(ticker: String): ValidationResult[String] =
    if (ticker.matches(".{2,}")) "Token ticker must be at least 2 characters length".invalidNel
    else ticker.validNel

  def validateToken(token: NonValidatedToken): ValidationResult[Token] = {
    validateTicker(token.ticker).map { case t =>
      Token(
        t,
        token.name,
        token.symbol,
        token.blockchainName,
        token.contractAddress,
        token.decimals,
        token.totalSupply,
        token.disableCountervalue,
        token.delisted
      )
    }
  }
}
