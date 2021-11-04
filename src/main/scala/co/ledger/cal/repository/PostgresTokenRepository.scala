package co.ledger.cal.repository

import cats.data.NonEmptyList
import cats.effect.IO
import co.ledger.cal.model.token.Token
import co.ledger.cal.repository.PostgresCoinRepository.insertTokensQuery
import co.ledger.cal.repository.PostgresCoinRepository.selectAllTokensQuery
import co.ledger.cal.repository.PostgresCoinRepository.selectTokenQuery
import doobie.Query0
import doobie.Update
import doobie.util.transactor.Transactor
import doobie.implicits._
import fs2.Stream

class PostgresTokenRepository(transactor: Transactor[IO]) extends Repository[TokenId, Token] {

  override def insert(tokens: NonEmptyList[Token]): IO[Unit] = insertTokensQuery()
    .updateMany(tokens)
    .transact(transactor)
    .void

  override def getAll: Stream[IO, Token] = selectAllTokensQuery.stream.transact(transactor)

  override def getOne(id: TokenId): IO[Token] =
    selectTokenQuery(id.ticker, id.blockchainName, id.contractAddress.value).unique
      .transact(transactor)
}

object PostgresCoinRepository {
  def insertTokensQuery(): Update[Token] = Update[Token](
    s"""INSERT INTO TOKEN(ticker, name, symbol, blockchain_name, contract_address, decimals, total_supply, disable_countervalue, delisted)
       |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (ticker, blockchain_name, contract_address)
       |DO UPDATE SET name = EXCLUDED.name, symbol = EXCLUDED.symbol, decimals = EXCLUDED.decimals, total_supply = EXCLUDED.total_supply,
       |disable_countervalue = EXCLUDED.disable_countervalue, delisted = EXCLUDED.delisted""".stripMargin
  )

  def selectAllTokensQuery: Query0[Token] = sql"""select * from token""".query[Token]

  def selectTokenQuery(
      ticker: String,
      blockchainName: String,
      contractAddress: String
  ): Query0[Token] =
    sql"""select * from token where LOWER(ticker) = LOWER($ticker) and LOWER(blockchainName) = LOWER($blockchainName) and LOWER(contractAddress) = LOWER($contractAddress)"""
      .query[Token]

}
