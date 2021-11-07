package co.ledger.cal.repository

import co.ledger.cal.model.Coin
import co.ledger.cal.repository.model.CoinDTO

class PostgresCoinRepositoryTest extends Fixture {

  behavior of "PostgresCoinRepository"

  it should "have valid queries" in {
    check(PostgresCoinRepository.insertCoinsQuery().toUpdate0(CoinDTO.from(Coin.example)))
    check(PostgresCoinRepository.selectCoinQuery("ticker", "name"))
    check(PostgresCoinRepository.selectAllCoinsQuery)

  }

}
