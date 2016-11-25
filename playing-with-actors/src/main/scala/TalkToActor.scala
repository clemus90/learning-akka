package com.packt.akka

import akka.pattern.ask
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.util.Timeout
import com.packt.akka.Checker.{BlackUser, CheckUser, WhiteUser}
import com.packt.akka.Recorder.NewUser
import com.packt.akka.Storage.AddUser
import scala.language.postfixOps
import scala.concurrent.duration._

case class User(username: String, email: String)

object Recorder {
  sealed trait RecorderMsg
  // Recorder messages
  case class NewUser(user: User) extends RecorderMsg

  def props(checker: ActorRef, storage: ActorRef) =
    Props(new Recorder(checker, storage))
}

object Checker {
  sealed trait CheckerMsg
  // Checker Messages
  case class CheckUser(user: User) extends CheckerMsg

  sealed trait CheckerResponse
  // Checker Responses
  case class BlackUser(user: User) extends CheckerResponse
  case class WhiteUser(user: User) extends CheckerResponse
}
object Storage {
  sealed trait StorageMsg
  //Storage Messages
  case class AddUser(user: User) extends StorageMsg
}

class Storage extends Actor {
  var users = List.empty[User]

  def receive = {
    case AddUser(user) =>
      println(s"Storage: $user added")
      users = user :: users
  }
}

class Checker extends  Actor{
  val blacklist = List(
    User("Adam", "adam@mail.com")
  )

  def receive = {
    case CheckUser(user) if blacklist.contains(user) =>
      println(s"Checker: $user in the blacklist")
      sender() ! BlackUser(user)
    case CheckUser(user) =>
      println(s"Checker: $user not in the blacklist")
      sender ! WhiteUser(user)
  }
}

class Recorder (checker: ActorRef, storage: ActorRef) extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(5 seconds)
  def receive = {
    case NewUser(user) =>
      checker ? CheckUser(user) map{
        case WhiteUser(user) =>
          storage ! AddUser(user)
        case BlackUser(user) =>
          println(s"Recorder: $user in the blacklist")
      }
  }
}

object TalkToActor extends App {

  //Create the 'talk-to-actor' system
  val system = ActorSystem("talk-to-actor")

  //Create the 'checker' actor
  val checker = system.actorOf(Props[Checker], "checker")

  //Create the 'Storage' actor
  val storage = system.actorOf(Props[Storage], "storage")

  //Create the 'recorder' actor
  val recorder = system.actorOf(Recorder.props(checker, storage), "recorder")

  //send NewUser Message to Recorder
  recorder ! Recorder.NewUser(User("Jon", "jon@email.com"))

  Thread.sleep(100)

  //shutdown system
  system.terminate()
}