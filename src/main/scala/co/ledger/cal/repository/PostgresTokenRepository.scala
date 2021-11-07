package co.ledger.cal.repository

import cats.data.NonEmptyList
import cats.effect.IO
import co.ledger.cal.model.Token
import co.ledger.cal.repository.PostgresTokenRepository.insertTokensQuery
import co.ledger.cal.repository.PostgresTokenRepository.selectAllTokensQuery
import co.ledger.cal.repository.PostgresTokenRepository.selectTokenQuery
import co.ledger.cal.repository.model.TokenDTO
import doobie.Query0
import doobie.Update
import doobie.util.transactor.Transactor
import doobie.implicits._
import fs2.Stream

class PostgresTokenRepository(transactor: Transactor[IO]) extends Repository[TokenId, Token] {

  override def insert(tokens: NonEmptyList[Token]): IO[Unit] = insertTokensQuery()
    .updateMany(tokens.map(TokenDTO.from))
    .transact(transactor)
    .void

  override def getAll: Stream[IO, Token] =
    selectAllTokensQuery.map(_.to).stream.transact(transactor)

  override def getOne(id: TokenId): IO[Token] =
    selectTokenQuery(id.ticker, id.blockchainName, id.contractAddress.value)
      .map(_.to)
      .unique
      .transact(transactor)
}

object PostgresTokenRepository {
  def insertTokensQuery(): Update[TokenDTO] = Update[TokenDTO](
    s"""INSERT INTO TOKEN(ticker, name, blockchain_name, contract_address, attributes)
       |VALUES (?, ?, ?, ?, ?) ON CONFLICT (ticker, blockchain_name, contract_address)
       |DO UPDATE SET name = EXCLUDED.name, attributes = EXCLUDED.attributes""".stripMargin
  )

  def selectAllTokensQuery: Query0[TokenDTO] = sql"""select * from token""".query[TokenDTO]

  def selectTokenQuery(
      ticker: String,
      blockchainName: String,
      contractAddress: String
  ): Query0[TokenDTO] =
    sql"""select * from token where LOWER(ticker) = LOWER($ticker) and LOWER(blockchain_name) = LOWER($blockchainName) and LOWER(contract_address) = LOWER($contractAddress)"""
      .query[TokenDTO]

}
