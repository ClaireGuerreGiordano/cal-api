package co.ledger.cal

import co.ledger.cal.parser.CoinJsonParser
import com.typesafe.scalalogging.StrictLogging

import scala.reflect.io.Path

object CALParser extends StrictLogging {
  def main(args: Array[String]): Unit = {
    val parser = new CoinJsonParser
    val p = Path("/home/cguerregiordano/IdeaProjects/crypto-assets/coins/")
    val r = parser.directoryParser(p).compile.toList.unsafeRunSync()
    logger.info(s"${r.length}")
    logger.info(r.mkString(","))
  }
}