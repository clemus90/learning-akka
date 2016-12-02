package com.packt.akka

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, Props}
import com.packt.akka.Worker.Work

class RouterPool extends Actor{

  var routees: List[ActorRef] = _

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    routees = List.fill(5)(
      context.actorOf(Props[Worker])
    )
  }

  override def receive: Receive = {
    case msg: Work =>
      println("I'm A Router and I received a Message ......")
      routees(util.Random.nextInt(routees.size)) forward msg
  }
}

class RouteGroup(routees: List[String]) extends Actor {
  override def receive: Receive = {
    case msg: Work =>
      println("I'm A Router and I received a Message ......")
      context.actorSelection(routees(util.Random.nextInt(routees.size))) forward msg
  }
}