package co.ledger.cal.parser

import co.ledger.cal.model.Coin
import co.ledger.cal.repository.Fixture

import scala.reflect.io.{File, Path}
import scala.util.Properties

class JsonParserTest extends Fixture {

  behavior of "JsonParser"

  it should "parse common.json files under coins directory into valid coins" in {
    val coins: List[Coin] = CoinJsonParser.parseDirectoryContent(zippedCoinsFolder).unsafeRunSync()
    coins.length should be (2)
    coins.find(_.name == "Bitcoin").isDefined should be (true)
    coins.find(_.name == "Bitcoin Cash").isDefined should be (true)
  }

  it should "allow only one network of type main" in {

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

}
