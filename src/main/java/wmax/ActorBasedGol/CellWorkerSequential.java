package wmax.ActorBasedGol;

public class CellWorkerSequential {

	public CellsCurrentState simulate(Simulate sim) {
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
		
		CellsCurrentState result = new CellsCurrentState(isAlive,sim.pos);
		return result;
	}
}
