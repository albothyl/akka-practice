package com.qqq.akka.guest;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.routing.FromConfig;

public class GuestActor extends AbstractActor {

	private Cluster cluster = Cluster.get(getContext().system());

	private final ActorRef hostActor = getContext().actorOf(FromConfig.getInstance().props(), "hostService");

	@Override
	public void preStart() {
		cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
	}

	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(String.class, message -> {
				if (null == hostActor) {
					System.out.println("hostActor not found");
					return;
				}
				System.out.println(hostActor.path());
				System.out.println("send message : " + message);
				hostActor.tell(message, ActorRef.noSender());
			})
			.build();
	}
}
