package co.ledger.cal.api

import cats.effect.ContextShift
import cats.effect.IO
import cats.effect.Timer
import co.ledger.cal.service.CoinService
import co.ledger.cal.service.DappService
import co.ledger.cal.service.TokenService
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
import co.ledger.cal.InvalidContractAddress
import co.ledger.cal.Types.HexStr
import co.ledger.cal.config.JDBCUrl.CatsRefinedTypeOps
import co.ledger.cal.model.Coin
import co.ledger.cal.model.Dapp
import co.ledger.cal.model.Token
import co.ledger.cal.repository.CoinId
import co.ledger.cal.repository.DappId
import co.ledger.cal.repository.TokenId
import com.typesafe.scalalogging.StrictLogging
import sttp.tapir.generic.auto._

final class CALRoutes(
    coinService: CoinService,
    tokenService: TokenService,
    dappService: DappService
)(implicit
    cs: ContextShift[IO],
    timer: Timer[IO]
) extends Http4sDsl[IO]
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
        .getOne(CoinId(ticker, name))
        .attempt
        .map(_.leftMap(_ => StatusCode.NotFound))
    }

  private val getAllCoins: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(CALRoutes.getAllCoins) { _ =>
      coinService.getAll.compile.toList.attempt.map(_.leftMap(_ => StatusCode.NotFound))

    }

  private val postTokens: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(CALRoutes.postTokens) { is =>
      {
        val tmp = better.files.File.temporaryFile()
        tmp
          .map(f => f.appendByteArray(is.readAllBytes()))
          .map(b =>
            tokenService
              .bulkInsert(b)
              .attempt
              .map(_.leftMap(_ => StatusCode.ServiceUnavailable))
          )
      }.get()

    }

  private val getToken: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(CALRoutes.getToken) { case (ticker, bn, ca) =>
      IO.fromEither(
        HexStr
          .validateNel(ca)
          .toEither
          .leftMap[InvalidContractAddress](nel =>
            InvalidContractAddress(ca, nel.toList.mkString(","))
          )
      ).flatMap(a =>
        tokenService
          .getOne(TokenId(ticker, bn, a))
      ).attempt
        .map(_.leftMap(_ => StatusCode.NotFound))
    }

  private val getAllTokens: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(CALRoutes.getAllTokens) { _ =>
      tokenService.getAll.compile.toList.attempt.map(_.leftMap(_ => StatusCode.NotFound))

    }

  private val postDapps: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(CALRoutes.postDapps) { is =>
      {
        val tmp = better.files.File.temporaryFile()
        tmp
          .map(f => f.appendByteArray(is.readAllBytes()))
          .map(b =>
            dappService
              .bulkInsert(b)
              .attempt
              .map(_.leftMap(_ => StatusCode.ServiceUnavailable))
          )
      }.get()

    }

  private val getDapp: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(CALRoutes.getDapp) { case (chainId, name) =>
      IO(chainId.toInt)
        .flatMap(id =>
          dappService
            .getOne(DappId(id, name))
        )
        .attempt
        .map(_.leftMap(_ => StatusCode.NotFound))
    }

  private val getAllDapps: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(CALRoutes.getAllDapps) { _ =>
      dappService.getAll.compile.toList.attempt.map(_.leftMap(_ => StatusCode.NotFound))

    }

  val routes: HttpRoutes[IO] =
    postCoins <+> getCoin <+> getAllCoins <+> postTokens <+> getToken <+> getAllTokens <+> postDapps <+> getAllDapps <+> getDapp
}

object CALRoutes {

  private[api] val postCoins =
    endpoint.post
      .in("cal" / "coin" / "insert")
      .in(inputStreamBody)
      .errorOut(statusCode)
      .out(jsonBody[List[Coin]].example(List(Coin.example)))
      .description(
        "Batch insert coins from a directory. Each common.json file describing the coin should be on its own subdirectory"
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

  private[api] val postTokens =
    endpoint.post
      .in("cal" / "token" / "insert")
      .in(inputStreamBody)
      .errorOut(statusCode)
      .out(jsonBody[List[Token]].example(List(Token.example)))
      .description(
        "Batch insert tokens from a directory. Each common.json file describing the token should be on its own subdirectory"
      )

  private[api] val getToken =
    endpoint.get
      .in(
        "cal" / "token" / path[String]("ticker") / path[String]("blockchainName") / path[String](
          "contractAddress"
        )
      )
      .errorOut(statusCode)
      .out(jsonBody[Token].example(Token.example))
      .description("Get a token from its ticker, blockchain name and contract address")

  private[api] val getAllTokens =
    endpoint.get
      .in("cal" / "tokens")
      .errorOut(statusCode)
      .out(jsonBody[List[Token]].example(List(Token.example)))
      .description("Get all tokens")

  private[api] val postDapps =
    endpoint.post
      .in("cal" / "dapp" / "insert")
      .in(inputStreamBody)
      .errorOut(statusCode)
      .out(jsonBody[List[Dapp]].example(List(Dapp.example)))
      .description(
        "Batch insert dapps from a directory. Each b2c.json file describing the token should be on its own subdirectory"
      )

  private[api] val getDapp =
    endpoint.get
      .in(
        "cal" / "dapp" / path[String]("chainId") / path[String]("name")
      )
      .errorOut(statusCode)
      .out(jsonBody[Dapp].example(Dapp.example))
      .description("Get a dapp from its chain id and name")

  private[api] val getAllDapps =
    endpoint.get
      .in("cal" / "dapps")
      .errorOut(statusCode)
      .out(jsonBody[List[Dapp]].example(List(Dapp.example)))
      .description("Get all dapps")

  val endpoints = List(
    CALRoutes.postCoins,
    CALRoutes.getCoin,
    CALRoutes.getAllCoins,
    CALRoutes.postTokens,
    CALRoutes.getToken,
    CALRoutes.getAllTokens,
    CALRoutes.postDapps,
    CALRoutes.getDapp,
    CALRoutes.getAllDapps
  )
}
