package com.packt.akka.cluster


import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import com.typesafe.config.ConfigFactory
import com.packt.akka.commons._

import scala.util.Random

class Frontend extends Actor {
  var backends = IndexedSeq.empty[ActorRef]

  override def receive = {
    case Add if backends.isEmpty =>
      println("Service unavailable, cluster doesn't have backend node.")
    case addOp: Add =>
      println("Frontend: I'll forward add operation to backend node to handle it.")
      backends(Random.nextInt(backends.size)) forward addOp
    case BackendRegistration if !(backends.contains(sender())) =>
      backends = backends :+ sender()
      context watch(sender())
    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
  }
}

object Frontend {

  private var _frontend: ActorRef = _frontend

  def initiate() = {
    val config = ConfigFactory.load().getConfig("Frontend")

    val system = ActorSystem("ClusterSystem", config)

    _frontend = system.actorOf(Props[Frontend], name = "frontend")
  }

  def getFrontend = _frontend
}
