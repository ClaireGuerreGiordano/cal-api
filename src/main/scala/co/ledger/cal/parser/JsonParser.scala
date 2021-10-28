package co.ledger.cal.parser

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.toBifunctorOps
import co.ledger.cal.{JsonFileNotFound, JsonParsingFailure}
import co.ledger.cal.model.{Coin, CoinValidator, NonValidatedCoin}
import com.typesafe.scalalogging.StrictLogging
import io.circe.parser
import fs2.Stream

import scala.reflect.io.{File, Path}
import scala.tools.nsc.io.Directory
import scala.util.Properties

abstract class JsonParser[T] extends StrictLogging {

  def parseJson(input: String): Either[JsonParsingFailure, T]

  def getCommonJsonFile(subFolder: Directory): IO[File]

  def directoryParser(path: Path): Stream[IO, T] = for {
    subFolder <- Stream.emits[IO, Directory](path.toDirectory.dirs.toSeq)
    common <- Stream.eval[IO, File](getCommonJsonFile(subFolder))
    content = common.lines().mkString(Properties.lineSeparator)
    coin <- Stream.eval[IO, T](IO.fromEither(parseJson(content)))
  } yield coin

}

class CoinJsonParser extends JsonParser[Coin] {

  object Validator extends CoinValidator

  override def parseJson(input: String): Either[JsonParsingFailure, Coin] =
    parser.parse(input).flatMap(_.as[NonValidatedCoin])
      .leftMap(e => NonEmptyList.one(e.getMessage))
      .flatMap(Validator.validateCoin(_).toEither)
      .leftMap(JsonParsingFailure(_))

  override def getCommonJsonFile(subFolder: Directory): IO[File] = {
    IO.fromEither(subFolder.files.find(_.name.toLowerCase == "common.json").toRight(JsonFileNotFound(subFolder.name)))
  }
}
