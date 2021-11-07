package co.ledger.cal.repository

import co.ledger.cal.model.Token
import co.ledger.cal.repository.model.TokenDTO

class PostgresTokenRepositoryTest extends Fixture {

  behavior of "PostgresTokenRepository"

  it should "have valid queries" in {
    check(PostgresTokenRepository.insertTokensQuery().toUpdate0(TokenDTO.from(Token.example)))
    check(PostgresTokenRepository.selectTokenQuery("ticker", "blockchainName", "0X1"))
    check(PostgresTokenRepository.selectAllTokensQuery)

  }

}
