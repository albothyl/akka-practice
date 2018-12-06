package com.qqq.akka.guest;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

public class GuestActorRunner {

  public static void main(String[] args) {
    // note that client is not a compute node, role not defined
    ActorSystem system = ActorSystem.create("ClusterSystem", ConfigFactory.load("application"));
    ActorRef guestService = system.actorOf(Props.create(GuestActor.class), "guestService");

    while (true) {
      guestService.tell("wooooooooaaaaaa", ActorRef.noSender());
    }
  }
}
