package com.packt.akka

import akka.actor.{Actor, ActorIdentity, Identify}
import akka.actor.Actor.Receive

class Watcher extends Actor{

  val selection = context.actorSelection("/user/counter")

  selection ! Identify(None)

  override def receive: Receive = {
    case ActorIdentity(_, Some(ref)) =>
      println(s"Actor Reference for counter is ${ref}")
    case ActorIdentity(_, None) =>
      println("Actor selection for actor doesn't live :(")
  }
}
