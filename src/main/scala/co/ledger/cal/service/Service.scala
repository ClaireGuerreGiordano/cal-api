package co.ledger.cal.service

import better.files.File
import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import fs2.Stream

trait Service[I, T] extends StrictLogging {
  def bulkInsert(file: File): IO[List[T]]

  def getOne(id: I): IO[T]

  def getAll: Stream[IO, T]
}
