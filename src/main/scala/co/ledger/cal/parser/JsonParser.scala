package co.ledger.cal.parser

import better.files.File
import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.toBifunctorOps
import cats.implicits.toTraverseOps
import co.ledger.cal.JsonParsingFailure
import co.ledger.cal.Utils
import co.ledger.cal.model.Coin
import co.ledger.cal.model.CoinValidator
import co.ledger.cal.model.Dapp
import co.ledger.cal.model.DappValidator
import co.ledger.cal.model.NonValidatedCoin
import co.ledger.cal.model.NonValidatedDapp
import co.ledger.cal.model.NonValidatedToken
import co.ledger.cal.model.Token
import co.ledger.cal.model.TokenValidator
import com.typesafe.scalalogging.StrictLogging
import io.circe.parser

import scala.util.Properties

sealed abstract class JsonParser[T] {

  def targetFileName: String

  def parseJson(input: String): Either[JsonParsingFailure, T]

  def parseDirectoryContent(file: File): IO[List[T]] = {
    val contents = Utils
      .unzip(file, targetFileName)
      .map(l => l.map(f => f.lines().mkString(Properties.lineSeparator)))
      .get()
    contents.map(s => IO.fromEither(parseJson(s))).sequence
  }

}

object CoinJsonParser extends JsonParser[Coin] {

  override def targetFileName: String = "common.json"

  object Validator extends CoinValidator

  override def parseJson(input: String): Either[JsonParsingFailure, Coin] =
    parser
      .parse(input)
      .flatMap(_.as[NonValidatedCoin])
      .leftMap(e => NonEmptyList.one(e.getMessage))
      .flatMap(Validator.validateCoin(_).toEither)
      .leftMap(JsonParsingFailure)

}

object TokenJsonParser extends JsonParser[Token] {

  override def targetFileName: String = "common.json"

  object Validator extends TokenValidator

  override def parseJson(input: String): Either[JsonParsingFailure, Token] =
    parser
      .parse(input)
      .flatMap(_.as[NonValidatedToken])
      .leftMap(e => NonEmptyList.one(e.getMessage))
      .flatMap(Validator.validateToken(_).toEither)
      .leftMap(JsonParsingFailure(_))

}

object DappJsonParser extends JsonParser[Dapp] with StrictLogging {

  override val targetFileName: String = "b2c.json"

  object Validator extends DappValidator

  override def parseJson(input: String): Either[JsonParsingFailure, Dapp] = {
    parser
      .parse(input)
      .flatMap(_.as[NonValidatedDapp])
      .leftMap(e => NonEmptyList.one(e.getMessage))
      .flatMap(Validator.validateDapp(_).toEither)
      .leftMap(JsonParsingFailure(_))
  }
}
