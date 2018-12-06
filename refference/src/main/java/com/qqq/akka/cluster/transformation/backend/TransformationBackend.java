package com.qqq.akka.cluster.transformation.backend;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import com.qqq.akka.cluster.transformation.TransformationMessages;
import com.qqq.akka.cluster.transformation.TransformationMessages.TransformationJob;

import static com.qqq.akka.cluster.transformation.TransformationMessages.BACKEND_REGISTRATION;

public class TransformationBackend extends AbstractActor {

	Cluster cluster = Cluster.get(getContext().getSystem());

	//subscribe to cluster changes, MemberUp
	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), MemberUp.class);
	}

	//re-subscribe when restart
	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(TransformationJob.class, job -> {
				getSender().tell(new TransformationMessages.TransformationResult(job.getText().toUpperCase()),
					getSelf());
			})
			.match(CurrentClusterState.class, state -> {
				for (Member member : state.getMembers()) {
					if (member.status().equals(MemberStatus.up())) {
						register(member);
					}
				}
			})
			.match(MemberUp.class, mUp -> {
				register(mUp.member());
			})
			.build();
	}

	void register(Member member) {
		if (member.hasRole("frontend"))
			getContext().actorSelection(member.address() + "/user/frontend").tell(BACKEND_REGISTRATION, getSelf());
	}
}
