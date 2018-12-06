package com.qqq.akka.host;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class HostActorRunner {

	public static void main(String[] args) {
		startup(new String[] { "2551", "2552", "0" });
	}

	public static void startup(String[] ports) {
		for (String port : ports) {
			Config config = ConfigFactory
				.parseString("akka.remote.netty.tcp.port=" + port)
				.withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]"))
				.withFallback(ConfigFactory.load("application"));

			ActorSystem system = ActorSystem.create("ClusterSystem", config);

			system.actorOf(Props.create(HostActor.class), "hostService");
		}
	}
}
