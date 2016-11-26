import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}

class Ares(athena: ActorRef) extends Actor {

  override def preStart() = {
    context.watch(athena)
  }

  override def postStop(): Unit = {
    println("Ares postStop...")
  }

  override def receive = {
    case Terminated =>
      context.stop(self)
  }
}

class Athena extends Actor {
  override def receive = {
    case msg =>
      println(s"Athena received $msg")
      context.stop(self)
  }
}

object Monitoring extends App {
  //Create the 'supervisor' actor system
  val system = ActorSystem("monitoring")

  //Create Ares Actor
  val athena = system.actorOf(Props[Athena], "athena")
  val ares = system.actorOf(Props(classOf[Ares], athena), "ares")

  athena ! "Hi"

  system.terminate()
}