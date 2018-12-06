package com.qqq.akka.host;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.ReachableMember;
import akka.cluster.ClusterEvent.UnreachableMember;

public class HostActor extends AbstractActor {

	private Cluster cluster = Cluster.get(getContext().system());

	@Override
	public void preStart() {
		cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
	}

	@Override
	public void postStop() {
		cluster.unsubscribe(self());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(String.class, message -> System.out.println("received message : " + message))
			.match(MemberUp.class, mUp -> {
				if (mUp.member().hasRole("compute"))
					System.out.println(" ========== MemberUp ========== " + mUp.member().address().toString());
			})
			.match(MemberEvent.class, other -> {
				System.out.println(" ========== MemberEvent ========== " + other.member().address().toString());
			})
			.match(UnreachableMember.class, unreachable -> {
				System.out.println(" ========== UnreachableMember ========== " + unreachable.member().address().toString());
			})
			.match(ReachableMember.class, reachable -> {
				if (reachable.member().hasRole("compute"))
					System.out.println(" ========== ReachableMember ========== " + reachable.member().address().toString());
			})
			.build();
	}
}
