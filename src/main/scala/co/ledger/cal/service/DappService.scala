package co.ledger.cal.service

import better.files.File
import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.toFlatMapOps
import co.ledger.cal.model.Dapp
import co.ledger.cal.parser.DappJsonParser
import co.ledger.cal.repository.DappId
import co.ledger.cal.repository.Repository
import fs2.Stream

case class DappService(repository: Repository[DappId, Dapp]) extends Service[DappId, Dapp] {

  override def bulkInsert(file: File): IO[List[Dapp]] = {
    val dapps: IO[List[Dapp]] = DappJsonParser.parseDirectoryContent(file)
    dapps.flatTap(l =>
      NonEmptyList.fromList(l) match {
        case Some(nel) => repository.insert(nel)
        case None      => IO.delay(logger.error("nothing to insert"))
      }
    )
  }

  override def getOne(id: DappId): IO[Dapp] = repository.getOne(id)

  override def getAll: Stream[IO, Dapp] = repository.getAll

}
