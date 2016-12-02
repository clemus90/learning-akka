package com.packt.akka

import akka.actor.Actor

class Worker extends Actor{

  import Worker._

  override def receive: Receive = {
    case msg: Work =>
      println(s"I received Work message and My ActorRef: ${self}")
  }
}

object Worker{
  case class Work()
}
