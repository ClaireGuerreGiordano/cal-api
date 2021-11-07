package co.ledger.cal

import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.RefinedTypeOps
import eu.timepit.refined.string.MatchesRegex

object Types {

  type HexStr = String Refined MatchesRegex["^0[xX][a-zA-Z0-9]*$"]

  object HexStr extends RefinedTypeOps[HexStr, String]

}
