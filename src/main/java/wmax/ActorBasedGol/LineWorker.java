package wmax.ActorBasedGol;

import akka.actor.UntypedActor;

public class LineWorker extends UntypedActor {

	@Override
	public void onReceive(Object msg) throws Exception {
		if( msg instanceof SimulateLine) {
			SimulateLine line = (SimulateLine)msg;
			
			boolean[] state = new boolean[line.size[1]];
			
			for(int h = 0; h < line.size[1]; h++) {
				int[] pos = {line.lineNumber, h};
				boolean[][] roi = getRoi(line.matrix, line.size, pos);
				
				int livingNeighbours = roi[1][1] ? -1 : 0;
				for(int i = 0; i < 3; i++)
					for(int j = 0; j < 3; j++) {
						if(roi[i][j])
							livingNeighbours += 1;
					}
				
				boolean isAlive = line.matrix[pos[0]][pos[1]];
				if(livingNeighbours < 2 || livingNeighbours > 3)
					isAlive = false;
				
				if(livingNeighbours == 3)
					isAlive = true;
				
				state[h] = isAlive;
			}
			
			getSender().tell(new LinesCurrentState(state, line.lineNumber), getSelf());
			
		}
	}
	
	private boolean[][] getRoi(boolean[][] matrix, int[] size, int[] pos) {
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
