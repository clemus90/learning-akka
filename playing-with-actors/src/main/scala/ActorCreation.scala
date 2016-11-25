package com.packt.akka

import akka.actor.{Actor, ActorSystem, Props}
import com.packt.akka.MusicController.{Play, Stop}
import com.packt.akka.MusicPlayer.{StartMusic, StopMusic}

object MusicController {
  sealed trait ControllerMsg
  case object Play extends ControllerMsg
  case object Stop extends ControllerMsg

  def props = Props[MusicController]
}

class MusicController extends Actor {
  override def receive = {
    case Play =>
      println("Music Started ...")
    case Stop =>
      println("Music Stopped")
  }
}

object MusicPlayer {
  sealed trait PlayMsg
  case object StopMusic extends PlayMsg
  case object StartMusic extends PlayMsg
}

class MusicPlayer extends Actor{
  override def receive = {
    case StopMusic =>
      println("I don't want to stop ")
    case StartMusic =>
      val controller = context.actorOf(MusicController.props, "controller")
      controller ! Play
    case _ =>
      println("Unkown Message")
  }
}

//object Creation extends App {
//
//  // Create the 'creation' actor system
//  val system = ActorSystem("creation")
//
//  // Create the 'Music Player' actor
//  val player = system.actorOf(Props[MusicPlayer], "player")
//  //send StartMusic Message to actor
//
//  player ! StartMusic
//  //send StopMusic Message to actor
//}