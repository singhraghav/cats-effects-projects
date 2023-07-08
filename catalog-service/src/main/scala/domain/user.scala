package domain

import domain.user.UserType.{Admin, SimpleUser}
import io.circe.{Decoder, Encoder, Json}
import doobie.Meta
import doobie.postgres.implicits._
import io.circe.generic.JsonCodec

import java.util.UUID

object user {

  @JsonCodec
  case class User(id: UUID, firstName: String, lastName: String, userType: UserType, email: String)

  @JsonCodec
  case class CreateUser(firstName: String, lastName: String, userType: UserType, email: String)

  sealed trait UserType {
    def isAdmin: Boolean = this.isInstanceOf[Admin.type]

    def isSimpleUser: Boolean = this.isInstanceOf[SimpleUser.type]

  }

  object UserType {

    implicit val encoder: Encoder[UserType]   = Encoder.instance(userType => Json.fromString(userType.toString))
    implicit val decoder: Decoder[UserType]   = Decoder[String].map(fromString)
    implicit val userTypeMeta: Meta[UserType] = pgEnumString("user_type", UserType.fromString, _.toString)

    def fromString(str: String): UserType = str.toLowerCase() match {
      case "admin"       => Admin
      case "simple_user" => SimpleUser
    }

    case object Admin extends UserType {
      override def toString: String = "admin"
    }

    case object SimpleUser extends UserType {
      override def toString: String = "simple_user"
    }

  }

}
