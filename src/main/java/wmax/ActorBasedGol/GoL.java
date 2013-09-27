package wmax.ActorBasedGol;

import java.io.IOException;
import java.util.ArrayList;

import wmax.ActorBasedGol.CellWorkerSequential;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

public class GoL extends UntypedActor {
	
	private ActorRef cells, lines;
	
	private boolean[][] matrix;
	private int[] size = {30,80};
	private double chance = 0.33;

	private int nrOfCellWorkers = 4;
	private int nrOfSimulationsDone = 0;
	
	private long timer, delta, avg;
	private long steps = 0, delay = 200;
	
	private boolean useAkka = true;
	
	private CellWorkerSequential lonesomeWorker = new CellWorkerSequential();

	private RoundRobinRouter router;

	public boolean render = true;
	
	public GoL(boolean shouldRender, boolean shouldUseAkka, boolean benchMode) {
		if(benchMode) {
			delay = 0;
			size[0] = 300;
			size[1] = 300;
		}
		
		render = shouldRender;
		useAkka = shouldUseAkka;
	}
	
	@Override
	public void preStart() throws IOException, InterruptedException {
		matrix = new boolean[size[0]][size[1]];
		randomize();
//		spawnGlider();
		router = new RoundRobinRouter(nrOfCellWorkers);
		
		if(useAkka) {
//			cells = getContext().actorOf(Props.create(CellWorker.class).withRouter(router));
			lines = getContext().actorOf(Props.create(LineWorker.class).withRouter(router));

			simulateNextStep();
		} else 
			while(true)
				simulateNextStepSecuential();
	}
	
	private void spawnGlider() {
		matrix[3][1] = true;
		matrix[3][2] = true;
		matrix[3][3] = true;
		matrix[2][3] = true;
		matrix[1][2] = true;		
	}

	public void simulateNextStepSecuential() throws IOException, InterruptedException {
		doRender();
		
		timer = System.currentTimeMillis();

		ArrayList<CellsCurrentState> states = new ArrayList<>();
		for(int i = 0; i < size[0]; i++)
			for(int j = 0; j < size[1]; j++) {
				int[] pos = {i,j};
				
				states.add(lonesomeWorker.simulate(new Simulate(getRoi(pos), matrix[i][j], pos)));
			}
		
		for(CellsCurrentState state : states)
			matrix[state.pos[0]][state.pos[1]] = state.alive;
				
		showStats();
		
		Thread.sleep(delay);
	}

	private void simulateNextStep() {
		boolean[][] copy = new boolean[size[0]][size[1]];
		copy = matrix.clone();
		
		timer = System.currentTimeMillis();

		for(int i = 0; i < size[0]; i++) {
//			for(int j = 0; j < size[1]; j++) {
//				int[] pos = {i,j};
//				cells.tell(new Simulate(getRoi(pos), matrix[i][j], pos), getSelf());
				lines.tell(new SimulateLine(copy, i, size), getSelf());
//			}
		}
	}
	
	@Override
	public void onReceive(Object msg) throws InterruptedException, IOException {
		if(msg instanceof CellsCurrentState) {
			CellsCurrentState state = (CellsCurrentState) msg;
			matrix[state.pos[0]][state.pos[1]] = state.alive;
			nrOfSimulationsDone += 1;
			
			if(nrOfSimulationsDone == size[0]*size[1]) {

				showStats(); // also measures end of the leap

				//--- this section is for whatever should happen between the leaps
				doRender();
				
				Thread.sleep(delay);

				nrOfSimulationsDone = 0;
				//----------------------------------------------------------------
				
				simulateNextStep(); // also measures start of the leap
			}
		} else if (msg instanceof LinesCurrentState) {
			LinesCurrentState state = (LinesCurrentState)msg;
			
			matrix[state.lineNum] = state.line;
			nrOfSimulationsDone += 1;
			
			if(nrOfSimulationsDone == size[0]) {
				
				showStats(); // also measures end of the leap

				//--- this section is for whatever should happen between the leaps
				doRender();
				
				Thread.sleep(delay);
				nrOfSimulationsDone = 0;
				
				simulateNextStep();
			}
		} else { System.err.println("unhandled msg"); }
	}

	private void showStats() throws IOException {
		delta = System.currentTimeMillis() - timer;
		steps += 1;
		avg += delta;
		System.err.println("last leap took: " + delta + "ms");
		System.err.print("one leap took aprox.: " + avg/steps + "ms\t");

		System.err.println(useAkka ? "nr of routees: " + router.nrOfInstances() : "\n");
		Runtime.getRuntime().exec("clear");
	}

	private void doRender() throws IOException {
		if(render ) {
			for(int i = 0; i < size[0]; i++) {
				for(int j = 0; j < size[1]; j++) {
					
					System.err.print(matrix[i][j] ? 'x' : ' ');
				}
				System.err.println(" ");
			}
			
//			System.err.println("-------------------");
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