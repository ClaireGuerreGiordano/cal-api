package co.ledger.cal.model

import cats.data.NonEmptyList
import cats.data.ValidatedNel
import cats.implicits.toTraverseOps
import doobie.Get
import io.circe.Codec
import io.circe.generic.JsonCodec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema

@JsonCodec case class NonValidatedDapp(
    chainId: Int,
    contracts: NonEmptyList[NonValidatedDappContract],
    name: String
)

final case class Dapp(chainId: Int, name: String, contracts: NonEmptyList[DappContract])

object Dapp {
  implicit val codec: Codec[Dapp]   = deriveCodec
  implicit val schema: Schema[Dapp] = Schema.derived
  val example: Dapp =
    Dapp(1, "name", NonEmptyList.one(DappContract.example))

  implicit val get: Get[NonEmptyList[NonValidatedDappContract]] = getFromDecoder
}

trait DappValidator {
  type ValidationResult[A] = ValidatedNel[String, A]

  object ContractValidator extends DappContractValidator

  def validateDapp(dapp: NonValidatedDapp): ValidationResult[Dapp] = {
    dapp.contracts.map(ContractValidator.validateDappContract(_)).sequence.map { case c =>
      Dapp(dapp.chainId, dapp.name, c)
    }
  }
}
