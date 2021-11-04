package co.ledger.cal.model.token

import doobie.util.Read
import doobie.util.Write
import doobie.util.meta.Meta
import io.circe.Decoder
import io.circe.Encoder
import io.estatico.newtype.macros.newtype
import sttp.tapir.CodecFormat.TextPlain
import sttp.tapir.Codec
import sttp.tapir.Schema

object Contract {
  @newtype case class Address(value: String) {
    def toLowerCase: Address = Address(value.toLowerCase)
  }
  object Address {
    implicit val decoder: Decoder[Address]                = deriving
    implicit val encoder: Encoder[Address]                = deriving
    implicit val meta: Meta[Address]                      = deriving
    implicit val read: Read[Address]                      = deriving
    implicit val write: Write[Address]                    = deriving
    implicit val schema: Schema[Address]                  = deriving
    implicit val codec: Codec[String, Address, TextPlain] = Codec.string.map(Address(_))(_.value)
  }
}
