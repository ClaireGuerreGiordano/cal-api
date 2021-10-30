package co.ledger.cal.service

import better.files.File
import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.toFlatMapOps
import co.ledger.cal.model.Coin
import co.ledger.cal.parser.CoinJsonParser
import co.ledger.cal.repository.CoinId
import co.ledger.cal.repository.Repository
import com.typesafe.scalalogging.StrictLogging
import fs2.Stream

case class CoinService(repository: Repository[CoinId, Coin]) extends StrictLogging {

  def bulkInsert(file: File): IO[List[Coin]] = {
    val coins: IO[List[Coin]] = CoinJsonParser.parseDirectoryContent(file)
    coins.flatTap(l =>
      NonEmptyList.fromList(l) match {
        case Some(nel) => repository.insert(nel)
        case None      => IO.delay(logger.error("nothing to insert"))
      }
    )
  }

  def getOne(ticker: String, name: String): IO[Coin] = repository.getOne(CoinId(ticker, name))

  def getAll: Stream[IO, Coin] = repository.getAll

}
