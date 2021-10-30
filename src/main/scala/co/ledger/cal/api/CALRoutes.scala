package co.ledger.cal.api

import cats.effect.ContextShift
import cats.effect.IO
import cats.effect.Timer
import co.ledger.cal.model.Coin
import co.ledger.cal.service.CoinService
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.model.StatusCode
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.endpoint
import sttp.tapir.path
import sttp.tapir.statusCode
import sttp.tapir._
import cats.syntax.all._
import com.typesafe.scalalogging.StrictLogging
import sttp.tapir.generic.auto._

final class CALRoutes(coinService: CoinService)(implicit cs: ContextShift[IO], timer: Timer[IO])
    extends Http4sDsl[IO]
    with StrictLogging {
  private val postCoins: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(CALRoutes.postCoins) { is =>
      {
        val tmp = better.files.File.temporaryFile()
        tmp
          .map(f => f.appendByteArray(is.readAllBytes()))
          .map(b =>
            coinService
              .bulkInsert(b)
              .attempt
              .map(_.leftMap(_ => StatusCode.ServiceUnavailable))
          )
      }.get()

    }

  private val getCoin: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(CALRoutes.getCoin) { case (ticker, name) =>
      coinService
        .getOne(ticker, name)
        .attempt
        .map(_.leftMap(_ => StatusCode.NotFound))
    }

  private val getAllCoins: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(CALRoutes.getAllCoins) { _ =>
      coinService.getAll.compile.toList.attempt.map(_.leftMap(_ => StatusCode.NotFound))

    }

  val routes: HttpRoutes[IO] = postCoins <+> getCoin <+> getAllCoins
}

object CALRoutes {

  private[api] val postCoins =
    endpoint.post
      .in("cal" / "insert")
      .in(inputStreamBody)
      .errorOut(statusCode)
      .out(jsonBody[List[Coin]].example(List(Coin.example)))
      .description(
        "Batch insert coins from a directory. Each common.json file describing the coin should rely on its own subdirectory"
      )

  private[api] val getCoin =
    endpoint.get
      .in("cal" / "coin" / path[String]("ticker") / path[String]("name"))
      .errorOut(statusCode)
      .out(jsonBody[Coin].example(Coin.example))
      .description("Get a coin from its ticker and name")

  private[api] val getAllCoins =
    endpoint.get
      .in("cal" / "coins")
      .errorOut(statusCode)
      .out(jsonBody[List[Coin]].example(List(Coin.example)))
      .description("Get all coins")

  val endpoints = List(
    CALRoutes.postCoins,
    CALRoutes.getCoin,
    CALRoutes.getAllCoins
  )
}
