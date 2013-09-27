package wmax.ActorBasedGol;

import akka.actor.UntypedActor;

public class CellWorker extends UntypedActor {
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof Simulate) {
			Simulate sim = (Simulate) msg;
			simulate(sim);
		}
	}

	public void simulate(Simulate sim) throws InterruptedException {
		int livingNeighbours = sim.roi[1][1] ? -1 : 0;
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++) {
				if(sim.roi[i][j])
					livingNeighbours += 1;
			}
		boolean isAlive = sim.alive;
		if(livingNeighbours < 2 || livingNeighbours > 3)
			isAlive = false;
		
		if(livingNeighbours == 3)
			isAlive = true;
		
		getSender().tell(new CellsCurrentState(isAlive,sim.pos), getSelf());
	}
}
