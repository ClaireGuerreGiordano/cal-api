package co.ledger.cal.model

import cats.data._
import cats.implicits._
import co.ledger.cal.model.Type.Main
import io.circe.generic.JsonCodec


@JsonCodec case class NonValidatedCoin(ticker: String, name: String, symbol: String, family: Family, coin_type: Int,
                                       has_segwit: Boolean, has_tokens: Boolean, units: NonEmptyList[Unit],
                                       networks: NonEmptyList[Network])

final case class Coin(ticker: String, name: String, symbol: String, family: Family, coinType: Int,
                 hasSegwit: Boolean, hasToken: Boolean, units: NonEmptyList[Unit],
                 networks: NonEmptyList[Network])

trait CoinValidator {

  type ValidationResult[A] = ValidatedNel[String, A]

  private def validateUnits(units: NonEmptyList[Unit]): ValidationResult[NonEmptyList[Unit]] =
    units.toList.find(_.magnitude == 0) match {
      case Some(_) => units.validNel
      case None => "There must be at least a unit with magnitude 0".invalidNel
    }

  private def validateNetwork(networks: NonEmptyList[Network]): ValidationResult[NonEmptyList[Network]] =
    if(networks.toList.count(_.`type` == Main) > 1) "Only one network of type main is allowed".invalidNel else networks.validNel

  def validateCoin(coin: NonValidatedCoin): ValidationResult[Coin] = {
    (validateUnits(coin.units),
      validateNetwork(coin.networks)).mapN{
      case (u, n) => Coin(coin.ticker, coin.name, coin.symbol,
        coin.family, coin.coin_type, coin.has_segwit, coin.has_tokens, u, n)
    }
  }
}

