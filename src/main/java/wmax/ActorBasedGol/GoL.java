package wmax.ActorBasedGol;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

public class GoL extends UntypedActor {
	
	private ActorRef cells;
	
	private boolean[][] matrix;
	private int[] size = {600, 600};
	private double chance = 0.5;

	private int nrOfCellWorkers = 100;
	private int nrOfSimulationsDone = 0;
	
	private long timer, delta;
	private long steps = 0;
	
	private boolean useAkka = true;
	
	private CellWorkerSequential lonesomeWorker = new CellWorkerSequential();

	private RoundRobinRouter router;
	@Override
	public void preStart() {
		matrix = new boolean[size[0]][size[1]];
		randomize();
		router = new RoundRobinRouter(nrOfCellWorkers);
		
		if(useAkka) {
			cells = getContext().actorOf(Props.create(CellWorker.class).withRouter(router));
			simulateNextStep();
		} else 
			while(true)
				simulateNextStepSecuential();
	}

	private void simulateNextStep() {
		timer = System.currentTimeMillis();

		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j < size[1]; j++) {
				int[] pos = {i,j};
				cells.tell(new Simulate(getRoi(pos), matrix[i][j], pos), getSelf());
			}		
	}
	
	public void simulateNextStepSecuential() {
		timer = System.currentTimeMillis();

		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j < size[1]; j++) {
				int[] pos = {i,j};
				
				CellsCurrentState state = lonesomeWorker.simulate(new Simulate(getRoi(pos), matrix[i][j], pos));
				
				matrix[state.pos[0]][state.pos[1]] = state.alive;
				
				steps += 1;
				
				delta += (System.currentTimeMillis() - timer);
				System.err.println(delta/steps);

			}		
	}

	@Override
	public void onReceive(Object msg) throws InterruptedException {
		if(msg instanceof CellsCurrentState) {
			CellsCurrentState state = (CellsCurrentState) msg;
			matrix[state.pos[0]][state.pos[1]] = state.alive;
			nrOfSimulationsDone += 1;
			
			if(nrOfSimulationsDone == size[0]*size[1]) {
//				
				for(int i = 0; i < size[0]; i++) {
					for(int j = 0; j < size[1]; j++) {
						
						System.err.print(matrix[i][j] ? 'x' : ' ');
					}
					System.err.println(" ");
				}
				
				System.err.println("-------------------");
				nrOfSimulationsDone = 0;
				simulateNextStep();
//				Thread.sleep(100);
				steps += 1;
				
				delta += (System.currentTimeMillis() - timer);
				System.err.println(delta/steps);
				System.err.println("nr of routees: " + router.nrOfInstances());
			}
		}
	}

	private void randomize() {
		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j < size[1]; j++) {
				matrix[i][j] = Math.random() <= chance ? true : false;
			}		
	}
	
	private boolean[][] getRoi(int[] pos) {
		boolean[][] roi = new boolean[3][3];
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++) {
				
				if(	pos[0] + i -1 < 0 || pos[0] + i -1 == size[0]
					|| pos[1] + j - 1 < 0 || pos[1] + j - 1 == size[1]) {
					roi[i][j] = false;
				} else
					roi[i][j] = matrix[ pos[0] + i -1 ][ pos[1] + j - 1 ];
			}
		
		return roi;
	}
}