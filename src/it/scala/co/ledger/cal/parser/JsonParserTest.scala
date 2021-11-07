package co.ledger.cal.parser

import co.ledger.cal.model.{Coin, Dapp, Token}
import co.ledger.cal.repository.Fixture

import scala.reflect.io.{File, Path}
import scala.util.Properties

class JsonParserTest extends Fixture {

  behavior of "JsonParser"

  it should "parse common.json files under coins directory into valid coins" in {
    val coins: List[Coin] = CoinJsonParser.parseDirectoryContent(zippedCoinsFolder).unsafeRunSync()
    coins.length should be (2)
    coins.exists(_.name == "Bitcoin") should be (true)
    coins.exists(_.name == "Bitcoin Cash") should be (true)
  }

  it should "allow only one network of type main for a coin" in {

    val bad = File(Path("src/it/resources/coin_with_2_main_networks.json")).lines().mkString(Properties.lineSeparator)

    val result = CoinJsonParser.parseJson(bad)

    result.isLeft should be (true)

    result.left.toSeq.head.errors.toList.exists(_.contains("Only one network of type main is allowed")) should be (true)
  }

  it should "ensure that a coin has at least a unit with magnitude 0, the smallest unit recorded" in {

    val bad = File(Path("src/it/resources/coins_with_no_unit_magnitude_0.json")).lines().mkString(Properties.lineSeparator)

    val result = CoinJsonParser.parseJson(bad)

    result.isLeft should be (true)

    result.left.toSeq.head.errors.toList.exists(_.contains("There must be at least a unit with magnitude 0")) should be (true)

  }

  it should "parse common.json files under tokens directory into valid tokens" in {
    val tokens: List[Token] = TokenJsonParser.parseDirectoryContent(zippedTokensFolder).unsafeRunSync()
    tokens.length should be (4)
    tokens.exists(_.name == "0chain") should be (true)
    tokens.exists(_.name == "0xBitcoin") should be (true)
  }

  it should "ensure that token ticker must be at least 2 characters length" in {
    val bad = File(Path("src/it/resources/token_with_1_letter_length_ticker.json")).lines().mkString(Properties.lineSeparator)

    val result = TokenJsonParser.parseJson(bad)

    result.isLeft should be (true)

    result.left.toSeq.head.errors.toList.head.contains("must be at least 2 characters length") should be (true)
  }

  it should "ensure that token contract address format matches ^0[xX][a-zA-Z0-9]*$" in {
    val bad = File(Path("src/it/resources/token_with_wrong_contract_address.json")).lines().mkString(Properties.lineSeparator)

    val result = TokenJsonParser.parseJson(bad)

    result.isLeft should be (true)

    result.left.toSeq.head.errors.toList.head.contains(".matches(\"^0[xX][a-zA-Z0-9]*$\")") should be (true)
  }

  it should "parse b2c.json files under dapps directory into valid dapp descriptors" in {
    val dapps: List[Dapp] = DappJsonParser.parseDirectoryContent(zippedDappsFolder).unsafeRunSync()
    dapps.length should be (2)
    dapps.exists(_.name == "1inch") should be (true)
    dapps.exists(_.name == "Aave") should be (true)
  }

  it should "ensure that dapps contract address format matches ^0[xX][a-zA-Z0-9]*$" in {
    val bad = File(Path("src/it/resources/dapps_with_wrong_contract_address.json")).lines().mkString(Properties.lineSeparator)

    val result = DappJsonParser.parseJson(bad)

    result.isLeft should be (true)

    result.left.toSeq.head.errors.toList.head.contains(".matches(\"^0[xX][a-zA-Z0-9]*$\")") should be (true)
  }

  it should "ensure that dapps method selector format matches ^0[xX][a-zA-Z0-9]*$" in {
    val bad = File(Path("src/it/resources/dapps_selector_with_wrong_contract_address.json")).lines().mkString(Properties.lineSeparator)

    val result = DappJsonParser.parseJson(bad)

    result.isLeft should be (true)

    result.left.toSeq.head.errors.toList.head.contains(".matches(\"^0[xX][a-zA-Z0-9]*$\")") should be (true)
  }

}
