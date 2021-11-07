package co.ledger.cal.repository

import co.ledger.cal.model.Dapp

class PostgresDappRepositoryTest extends Fixture {

  behavior of "PostgresDappRepository"

  it should "have valid queries" in {
    check(PostgresDappRepository.insertDappsQuery().toUpdate0(Dapp.example))
    check(PostgresDappRepository.selectDappQuery(1, "name"))
    check(PostgresDappRepository.selectAllDappsQuery)

  }

}
