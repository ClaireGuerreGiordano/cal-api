package co.ledger.cal

import cats.Show
import cats.data.NonEmptyList
import cats.implicits.toBifunctorOps
import cats.implicits.toShow
import cats.implicits.toTraverseOps
import doobie.Get
import doobie.util.Put
import io.circe.syntax.EncoderOps
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import io.circe.parser
import org.postgresql.util.PGobject
import sttp.tapir.Schema

import scala.reflect.ClassTag

package object model {
  def getFromDecoder[T: Decoder]: Get[T] = Get[Json].temap { s =>
    Decoder[T].decodeJson(s).leftMap(_.getMessage())
  }

  def putFromEncoder[T: Encoder]: Put[T] = Put[Json].contramap(m => m.asJson)

  implicit def nelGet[T](implicit decoder: Decoder[T]): Get[NonEmptyList[T]] = Get[Json].temap(s =>
    s.asArray match {
      case Some(value) =>
        value.map(json => decoder.decodeJson(json)).sequence match {
          case Left(value) => Left(value.getMessage())
          case Right(value) =>
            NonEmptyList.fromList(value.toList) match {
              case Some(s) => Right(s)
              case None    => Left("empty list")
            }
        }
      case None => Left("invalid list")
    }
  )

  implicit def nelPut[T](implicit encoder: Encoder[T]): Put[NonEmptyList[T]] = Put[Json].contramap {
    l =>
      Json.fromValues(l.toList.map(encoder(_)))
  }

  implicit val jsonPut: Put[Json] =
    Put.Advanced.other[PGobject](NonEmptyList.of("json")).tcontramap[Json] { j =>
      val o = new PGobject
      o.setType("json")
      o.setValue(j.noSpaces)
      o
    }

  implicit val showPGobject: Show[PGobject] = Show.show(_.getValue.take(2500))

  implicit val jsonGet: Get[Json] =
    Get.Advanced.other[PGobject](NonEmptyList.of("json")).temap[Json] { o =>
      parser.parse(o.getValue).leftMap(_.show)
    }

  implicit def schema[T: ClassTag](implicit s: Schema[T]): Schema[NonEmptyList[T]] =
    Schema
      .schemaForArray[T]
      .map[NonEmptyList[T]](a => NonEmptyList.fromList(List.from[T](a)))(nel =>
        Array.from[T](nel.toList)
      )

}
