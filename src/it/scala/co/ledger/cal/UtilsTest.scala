package co.ledger.cal

import co.ledger.cal.repository.Fixture

class UtilsTest extends Fixture {

  behavior of "Utils"

  it should "return files under subdirectories of a zipped folder" in {
    val files = Utils.unzip(zippedCoinsFolder, "common.json").get()
    files.length should be (2)
    files.filter(_.name == "common.json").length should be (2)
  }

}
