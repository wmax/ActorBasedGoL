package wmax.ActorBasedGol;

import akka.actor.UntypedActor;

public class Greeter extends UntypedActor {
	private final String greet;
	
	public Greeter(String aGreet, String anotherGreet) {
		greet = aGreet + anotherGreet;
	}
	
	public static enum Msg {
		GREET, DONE;
	}

	@Override
	public void onReceive(Object msg) {
		if (msg == Msg.GREET) {
			System.out.println(getSelf().path().name() +
": Hello World! " + greet);

			getSender().tell(Msg.DONE, getSelf());
		} else
			unhandled(msg);
	}
}
