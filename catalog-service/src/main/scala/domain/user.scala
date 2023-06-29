package domain

import cats.effect.IO
import domain.user.UserType.{Admin, SimpleUser}
import org.http4s.EntityDecoder

import java.util.UUID

object user {

  case class User(id: UUID, firstName: String, lastName: String, userType: UserType, email: String)

  object User {
    implicit val userDecoder: EntityDecoder[IO, User] = ???
  }

  case class CreateUser(firstName: String, lastName: String, userType: String, email: String) {
    def toUser(id: UUID): User = User(id, firstName, lastName, UserType.fromString(userType), email)
  }

  sealed trait UserType {
    def isAdmin: Boolean = this.isInstanceOf[Admin.type]

    def isSimpleUser: Boolean = this.isInstanceOf[SimpleUser.type]

  }

  object UserType {

    def fromString(str: String): UserType = str.toLowerCase() match {
      case "admin" => Admin
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


