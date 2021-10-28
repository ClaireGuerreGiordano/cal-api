package co.ledger.cal

import cats.data.NonEmptyList

trait ApplicationError extends Throwable

final case class JsonParsingFailure(errors: NonEmptyList[String]) extends ApplicationError {
  override def getMessage: String = s"Failed to parse json because : ${errors.toList.mkString(",")}"
}

final case class JsonFileNotFound(subFolderName: String) extends ApplicationError {
  override def getMessage: String = s"json file missing under directory $subFolderName"
}