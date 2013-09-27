package wmax.ActorBasedGol;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class BootstrapGoL {
	public static void main(String[] args) {
		boolean shouldRender = false;
		boolean useAkka = false;
		boolean benchMode = false;
		
		for(String arg : args) {
			switch (arg) {
			case "useAkka":
				useAkka = true;
				break;
				
			case "doRender":
				shouldRender = true;
				break;
				
			case "doBenchmark":
				benchMode = true;
				break;
				
			case "help":
				System.out.println("call example(all arg-combs possible): java -jar ActorBasedGol.jar [ useAkka | doRender | doBenchmark ] ");
				return;
		
			default:
				break;
			}
		}
		
		ActorSystem system = ActorSystem.create("GoL-System");
//		system.actorOf(Props.create(GoL.class, shouldRender, useAkka, benchMode));
		system.actorOf(Props.create(GoL.class, true, true, false));

	}
}
