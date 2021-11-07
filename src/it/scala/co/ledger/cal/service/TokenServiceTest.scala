package co.ledger.cal.service

import co.ledger.cal.repository.Fixture

class TokenServiceTest  extends Fixture {

  behavior of "TokenService"

  val service: TokenService = TokenService(tokenRepository)

  it should "parse common.json files under tokens directory into valid tokens and insert them into database" in {
    val tokensFromService = service.bulkInsert(zippedTokensFolder).unsafeRunSync()
    val tokensFromDb = tokenRepository.getAll.compile.toList.unsafeRunSync()
    tokensFromDb should contain theSameElementsAs tokensFromService
    tokensFromDb.length should be (4)
    tokensFromDb.exists(_.name == "0chain") should be (true)
    tokensFromDb.exists(_.name == "0xBitcoin") should be (true)
  }

}
