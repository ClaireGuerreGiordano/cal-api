package co.ledger.cal.service

import co.ledger.cal.repository.Fixture

class CoinServiceTest extends Fixture {

  behavior of "CoinService"

  val service: CoinService = CoinService(coinRepository)

  it should "parse common.json files under coins directory into valid coins and insert them into database" in {
    val coinsFromService = service.bulkInsert(zippedCoinsFolder).unsafeRunSync()
    val coinsFromDb = coinRepository.getAll.compile.toList.unsafeRunSync()
    coinsFromDb should contain theSameElementsAs coinsFromService
    coinsFromDb.length should be (2)
    coinsFromDb.exists(_.name == "Bitcoin") should be (true)
    coinsFromDb.exists(_.name == "Bitcoin Cash") should be (true)
  }

}
