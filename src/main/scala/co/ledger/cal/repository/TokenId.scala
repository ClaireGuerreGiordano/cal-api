package co.ledger.cal.repository

import co.ledger.cal.model.token.Contract

case class TokenId(ticker: String, blockchainName: String, contractAddress: Contract.Address)
