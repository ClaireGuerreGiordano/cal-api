package co.ledger.cal.repository

import co.ledger.cal.Types.HexStr

case class TokenId(ticker: String, blockchainName: String, contractAddress: HexStr)
