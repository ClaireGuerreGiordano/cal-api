package co.ledger.cal.model

import cats.data.ValidatedNel
import cats.implicits.catsSyntaxTuple2Semigroupal
import cats.implicits.catsSyntaxValidatedId
import co.ledger.cal.Types.HexStr
import co.ledger.cal.config.JDBCUrl.CatsRefinedTypeOps
import io.circe.Codec
import io.circe.generic.JsonCodec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema
import io.circe.refined._

@JsonCodec case class NonValidatedToken(
    ticker: String,
    name: String,
    blockchain_name: String,
    contract_address: String,
    symbol: Option[String],
    decimals: Int,
    total_supply: Option[BigDecimal],
    disable_countervalue: Option[Boolean],
    delisted: Option[Boolean]
)

final case class Token(
    ticker: String,
    name: String,
    blockchainName: String,
    contractAddress: HexStr,
    symbol: Option[String],
    decimals: Int,
    totalSupply: Option[BigDecimal],
    disableCountervalue: Boolean = false,
    delisted: Boolean = false
)
object Token {
  implicit val codec: Codec[Token]   = deriveCodec
  implicit val schema: Schema[Token] = Schema.derived
  val example: Token =
    Token("ticker", "name", "blockchainName", HexStr.unsafeFrom("0x1"), Some("symbol"), 10, None)
}

trait TokenValidator {

  type ValidationResult[A] = ValidatedNel[String, A]

  private def validateTicker(ticker: String): ValidationResult[String] =
    if (ticker.matches("^.{2,}$")) ticker.validNel
    else s"Token ticker $ticker must be at least 2 characters length".invalidNel

  def validateToken(token: NonValidatedToken): ValidationResult[Token] = {
    (validateTicker(token.ticker), HexStr.validateNel(token.contract_address)).mapN { case (t, a) =>
      Token(
        t,
        token.name,
        token.blockchain_name,
        a,
        token.symbol,
        token.decimals,
        token.total_supply,
        token.disable_countervalue.getOrElse(false),
        token.delisted.getOrElse(false)
      )
    }
  }
}
