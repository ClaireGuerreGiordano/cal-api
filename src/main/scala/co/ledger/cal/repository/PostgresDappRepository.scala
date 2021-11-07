package co.ledger.cal.repository

import cats.data.NonEmptyList
import cats.effect.IO
import co.ledger.cal.model.Dapp
import co.ledger.cal.repository.PostgresDappRepository.insertDappsQuery
import co.ledger.cal.repository.PostgresDappRepository.selectAllDappsQuery
import co.ledger.cal.repository.PostgresDappRepository.selectDappQuery
import doobie.Query0
import doobie.Update
import doobie.implicits.toSqlInterpolator
import doobie.util.transactor.Transactor
import doobie.implicits._
import fs2.Stream

class PostgresDappRepository(transactor: Transactor[IO]) extends Repository[DappId, Dapp] {

  override def insert(dapps: NonEmptyList[Dapp]): IO[Unit] = insertDappsQuery()
    .updateMany(dapps)
    .transact(transactor)
    .void

  override def getAll: Stream[IO, Dapp] = selectAllDappsQuery.stream.transact(transactor)

  override def getOne(id: DappId): IO[Dapp] =
    selectDappQuery(id.chainId, id.name).unique
      .transact(transactor)
}

object PostgresDappRepository {
  def insertDappsQuery(): Update[Dapp] = Update[Dapp](
    s"""INSERT INTO DAPP(chain_id, name, contracts)
       |VALUES (?, ?, ?) ON CONFLICT (chain_id, name)
       |DO UPDATE SET contracts = EXCLUDED.contracts""".stripMargin
  )

  def selectAllDappsQuery: Query0[Dapp] = sql"""select * from dapp""".query[Dapp]

  def selectDappQuery(chainId: Int, name: String): Query0[Dapp] =
    sql"""select * from dapp where chain_id = $chainId and LOWER(name) = LOWER($name)"""
      .query[Dapp]

}
