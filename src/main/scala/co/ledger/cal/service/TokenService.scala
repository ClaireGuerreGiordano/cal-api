package co.ledger.cal.service

import better.files.File
import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.toFlatMapOps
import co.ledger.cal.model.token.Token
import co.ledger.cal.parser.TokenJsonParser
import co.ledger.cal.repository.Repository
import co.ledger.cal.repository.TokenId
import fs2.Stream

case class TokenService(repository: Repository[TokenId, Token]) extends Service[TokenId, Token] {

  override def bulkInsert(file: File): IO[List[Token]] = {
    val tokens: IO[List[Token]] = TokenJsonParser.parseDirectoryContent(file)
    tokens.flatTap(l =>
      NonEmptyList.fromList(l) match {
        case Some(nel) => repository.insert(nel)
        case None      => IO.delay(logger.error("nothing to insert"))
      }
    )
  }

  override def getOne(id: TokenId): IO[Token] = repository.getOne(id)

  override def getAll: Stream[IO, Token] = repository.getAll

}
