package co.ledger.cal.model

import cats.data.ValidatedNel
import co.ledger.cal.Types.HexStr
import co.ledger.cal.config.JDBCUrl.CatsRefinedTypeOps
import doobie.Get
import doobie.util.Put
import io.circe.Codec
import io.circe.generic.JsonCodec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema
import io.circe.refined._

@JsonCodec case class NonValidatedMethod(
    selector: String,
    erc20OfInterest: List[String],
    method: String,
    plugin: String
)

final case class Method(
    selector: HexStr,
    erc20OfInterest: List[String],
    method: String,
    plugin: String
)

object Method {
  implicit val codec: Codec[Method]   = deriveCodec
  implicit val schema: Schema[Method] = Schema.derived
  val example: Method =
    Method(HexStr.unsafeFrom("0x1"), List.empty, "method", "plugin")

  implicit val get: Get[Method] = getFromDecoder
  implicit val put: Put[Method] = putFromEncoder
}

trait MethodValidator {
  type ValidationResult[A] = ValidatedNel[String, A]

  def validateMethod(method: NonValidatedMethod): ValidationResult[Method] = {
    HexStr.validateNel(method.selector).map { s =>
      Method(
        s,
        method.erc20OfInterest,
        method.method,
        method.plugin
      )
    }
  }
}
