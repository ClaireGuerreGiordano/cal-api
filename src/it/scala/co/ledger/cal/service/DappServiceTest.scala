package co.ledger.cal.service

import co.ledger.cal.repository.Fixture

class DappServiceTest extends Fixture {

  behavior of "DappService"

  val service: DappService = DappService(dappRepository)

  it should "parse b2c.json files under dapps directory into valid dapps and insert them into database" in {
    val dappsFromService = service.bulkInsert(zippedDappsFolder).unsafeRunSync()
    val dappsFromDb = dappRepository.getAll.compile.toList.unsafeRunSync()
    dappsFromDb should contain theSameElementsAs dappsFromService
    dappsFromDb.length should be (2)
    dappsFromDb.exists(_.name == "1inch") should be (true)
    dappsFromDb.exists(_.name == "Aave") should be (true)
  }

}