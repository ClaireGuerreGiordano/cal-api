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
import co.ledger.cal.model.NonValidatedCoin
import io.circe.parser

import scala.util.Properties

abstract class JsonParser[T] {

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
