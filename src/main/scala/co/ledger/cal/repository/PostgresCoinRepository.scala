package co.ledger.cal.repository

import cats.data.NonEmptyList
import cats.effect.IO
import co.ledger.cal.model.coin.Coin
import co.ledger.cal.repository.PostgresCoinRepository.insertCoinsQuery
import co.ledger.cal.repository.PostgresCoinRepository.selectAllCoinsQuery
import co.ledger.cal.repository.PostgresCoinRepository.selectCoinQuery
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import fs2.Stream

class PostgresCoinRepository(transactor: Transactor[IO]) extends Repository[CoinId, Coin] {

  override def insert(coins: NonEmptyList[Coin]): IO[Unit] = insertCoinsQuery()
    .updateMany(coins)
    .transact(transactor)
    .void

  override def getAll: Stream[IO, Coin] = selectAllCoinsQuery.stream.transact(transactor)

  override def getOne(id: CoinId): IO[Coin] =
    selectCoinQuery(id.ticker, id.name).unique.transact(transactor)
}

object PostgresCoinRepository {
  def insertCoinsQuery(): Update[Coin] = Update[Coin](
    s"""INSERT INTO COIN(ticker, name, symbol, family, attributes)
       |VALUES (?, ?, ?, ?, ?) ON CONFLICT (ticker, name)
       |DO UPDATE SET symbol = EXCLUDED.symbol, family = EXCLUDED.family,
       |attributes = EXCLUDED.attributes""".stripMargin
  )

  def selectAllCoinsQuery: Query0[Coin] = sql"""select * from coin""".query[Coin]

  def selectCoinQuery(ticker: String, name: String): Query0[Coin] =
    sql"""select * from coin where LOWER(ticker) = LOWER($ticker) and LOWER(name) = LOWER($name)"""
      .query[Coin]

}
