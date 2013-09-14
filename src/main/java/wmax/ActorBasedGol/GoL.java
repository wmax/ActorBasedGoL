package wmax.ActorBasedGol;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

public class GoL extends UntypedActor {
	
	private ActorRef cells;
	
	private boolean[][] matrix;
	private int[] size = {300, 300};
	private double chance = 0.5;

	private int nrOfCellWorkers = 1;
	private int nrOfSimulationsDone = 0;
	
	@Override
	public void preStart() {
		matrix = new boolean[size[0]][size[1]];
		randomize();
		
		cells = getContext().actorOf(Props.create(CellWorker.class).withRouter(new RoundRobinRouter(nrOfCellWorkers)));
		simulateNextStep();
	}

	private void simulateNextStep() {
		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j < size[1]; j++) {
				int[] pos = {i,j};
				cells.tell(new Simulate(getRoi(pos), matrix[i][j], pos), getSelf());
			}		
	}

	@Override
	public void onReceive(Object msg) throws InterruptedException {
		if(msg instanceof CellsCurrentState) {
			CellsCurrentState state = (CellsCurrentState) msg;
			matrix[state.pos[0]][state.pos[1]] = state.alive;
			nrOfSimulationsDone += 1;
			
			if(nrOfSimulationsDone == size[0]*size[1]) {
				
				for(int i = 0; i < size[0]; i++) {
					for(int j = 0; j < size[1]; j++) {
						
						System.err.print(matrix[i][j] ? 'x' : ' ');
					}
					System.err.println(" ");
				}
				
				System.err.println("-------------------");
				nrOfSimulationsDone = 0;
				simulateNextStep();
				Thread.sleep(100);
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