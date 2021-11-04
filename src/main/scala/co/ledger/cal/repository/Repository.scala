package co.ledger.cal.repository

import cats.data.NonEmptyList
import cats.effect.IO
import fs2.Stream

trait Repository[V, T] {
  def insert(t: NonEmptyList[T]): IO[Unit]
  def getAll: Stream[IO, T]
  def getOne(id: V): IO[T]
}
