package co.ledger.cal

import eu.timepit.refined.W
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.cats.CatsRefinedTypeOpsSyntax
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.string.MatchesRegex

package object config {
  type JDBCDriverName =
    String Refined MatchesRegex[W.`"^\\\\w+\\\\.[\\\\w\\\\d\\\\.]+[\\\\w\\\\d]+$"`.T]
  object JDBCDriverName extends RefinedTypeOps[JDBCDriverName, String] with CatsRefinedTypeOpsSyntax

  type JDBCUrl = String Refined MatchesRegex[W.`"^jdbc:[a-zA-z0-9]+:.*"`.T]
  object JDBCUrl extends RefinedTypeOps[JDBCUrl, String] with CatsRefinedTypeOpsSyntax

  type JDBCUsername = String Refined NonEmpty
  object JDBCUsername extends RefinedTypeOps[JDBCUsername, String] with CatsRefinedTypeOpsSyntax

  type JDBCPassword = String Refined NonEmpty
  object JDBCPassword extends RefinedTypeOps[JDBCPassword, String] with CatsRefinedTypeOpsSyntax

}
