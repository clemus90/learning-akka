package com.packt.akka

import akka.actor.ActorLogging
import akka.persistence._

object Counter{
  sealed trait Operation{
    val count: Int

  }

  case class Increment(override val count: Int) extends Operation
  case class Decrement(override val count: Int) extends Operation

  case class Cmd(op: Operation)
  case class Evt(op: Operation)

  case class State(count: Int)
}

class Counter extends PersistentActor with ActorLogging {
  import Counter._

  println("Starting .......................")

  //Persistent Identifier
  override def persistenceId: String = "counter-example"

  var state: State = State(count=0)

  def updateState(evt: Evt): Unit = evt match {
    case Evt(Increment(count)) =>
      state = State(count = state.count + count)
      takeSnapshot

    case Evt(Decrement(count)) =>
      state = State(count = state.count - count)
      takeSnapshot
  }
  //Persistent receive on recovery mood
  override def receiveRecover: Receive = {
    case evt: Evt =>
      println(s"Counter receive ${evt} on recovering mood")
      updateState(evt)
    case SnapshotOffer(_, snapshot: State) =>
      println(s"Counter receive snapshot with data:  ${snapshot} on recovery mood")
      state = snapshot
    case RecoveryCompleted =>
      println(s"Recovery Complete and Now I'll switch to receviving mode :)")

  }

  //Persistent receive on normal mood
  override def receiveCommand: Receive = {
    case cmd @ Cmd(op) =>
      println(s"Counter receive ${cmd}")
      persist(Evt(op)) { evt =>
        updateState(evt)
      }

    case "print" =>
      println(s"The Current state of counter is ${state}")

    case SaveSnapshotSuccess(metadata) =>
      println(s"save sanapshot succeed")

    case SaveSnapshotFailure(metadata, cause) =>
      println(s"save sanapshot failed and failure is  ${cause}")
  }

  def takeSnapshot = {
    if(state.count % 5 == 0){
      saveSnapshot(state)
    }
  }

  //override def recovery: Recovery = Recovery.none
}