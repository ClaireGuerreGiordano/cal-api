package co.ledger.cal.model

import cats.data.NonEmptyList
import cats.data.ValidatedNel
import cats.implicits.catsSyntaxTuple2Semigroupal
import cats.implicits.toTraverseOps
import co.ledger.cal.Types.HexStr
import co.ledger.cal.config.JDBCUrl.CatsRefinedTypeOps
import doobie.Get
import doobie.util.Put
import io.circe.Codec
import io.circe.generic.JsonCodec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema
import io.circe.refined._

@JsonCodec case class NonValidatedDappContract(
    address: String,
    contractName: String,
    methods: NonEmptyList[NonValidatedMethod]
)

final case class DappContract(address: HexStr, contractName: String, methods: NonEmptyList[Method])

object DappContract {
  implicit val codec: Codec[DappContract]   = deriveCodec
  implicit val schema: Schema[DappContract] = Schema.derived
  val example: DappContract =
    DappContract(HexStr.unsafeFrom("0x1"), "name", NonEmptyList.one(Method.example))
  implicit val get: Get[DappContract] = getFromDecoder
  implicit val put: Put[DappContract] = putFromEncoder
}

trait DappContractValidator {
  type ValidationResult[A] = ValidatedNel[String, A]

  object ContractMethodValidator extends MethodValidator

  def validateDappContract(c: NonValidatedDappContract): ValidationResult[DappContract] = {
    (
      HexStr.validateNel(c.address),
      c.methods.map(ContractMethodValidator.validateMethod(_)).sequence
    ).mapN { case (a, l) =>
      DappContract(
        a,
        c.contractName,
        l
      )
    }
  }
}
