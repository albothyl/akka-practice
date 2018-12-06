package com.qqq.akka.cluster.transformation.frontend;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import com.qqq.akka.cluster.transformation.TransformationMessages;

import java.util.ArrayList;
import java.util.List;

public class TransformationFrontend extends AbstractActor {

	List<ActorRef> backends = new ArrayList<>();
	int jobCounter = 0;

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(TransformationMessages.TransformationJob.class, job -> backends.isEmpty(), job -> {
				getSender().tell(
					new TransformationMessages.JobFailed("Service unavailable, try again later", job),
					getSender());
			})
			.match(TransformationMessages.TransformationJob.class, job -> {
				jobCounter++;
				backends.get(jobCounter % backends.size())
					.forward(job, getContext());
			})
			.matchEquals(TransformationMessages.BACKEND_REGISTRATION, x -> {
				getContext().watch(getSender());
				backends.add(getSender());
			})
			.match(Terminated.class, terminated -> {
				backends.remove(terminated.getActor());
			})
			.build();
	}

}
