package co.ledger.cal.parser

import better.files.File
import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.toBifunctorOps
import cats.implicits.toTraverseOps
import co.ledger.cal.JsonParsingFailure
import co.ledger.cal.Utils
import co.ledger.cal.model.coin.Coin
import co.ledger.cal.model.coin.CoinValidator
import co.ledger.cal.model.coin.NonValidatedCoin
import co.ledger.cal.model.token.NonValidatedToken
import co.ledger.cal.model.token.Token
import co.ledger.cal.model.token.TokenValidator
import io.circe.parser

import scala.util.Properties

sealed abstract class JsonParser[T] {

  def parseJson(input: String): Either[JsonParsingFailure, T]

  def parseDirectoryContent(file: File): IO[List[T]] = {
    val contents = Utils
      .unzip(file, "common.json")
      .map(l => l.map(f => f.lines().mkString(Properties.lineSeparator)))
      .get()
    contents.map(s => IO.fromEither(parseJson(s))).sequence
  }

}

object CoinJsonParser extends JsonParser[Coin] {

  object Validator extends CoinValidator

  override def parseJson(input: String): Either[JsonParsingFailure, Coin] =
    parser
      .parse(input)
      .flatMap(_.as[NonValidatedCoin])
      .leftMap(e => NonEmptyList.one(e.getMessage))
      .flatMap(Validator.validateCoin(_).toEither)
      .leftMap(JsonParsingFailure(_))

}

object TokenJsonParser extends JsonParser[Token] {

  object Validator extends TokenValidator

  override def parseJson(input: String): Either[JsonParsingFailure, Token] =
    parser
      .parse(input)
      .flatMap(_.as[NonValidatedToken])
      .leftMap(e => NonEmptyList.one(e.getMessage))
      .flatMap(Validator.validateToken(_).toEither)
      .leftMap(JsonParsingFailure(_))

}
