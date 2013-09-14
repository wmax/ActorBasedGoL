package wmax.ActorBasedGol;

public class CellsCurrentState {
	public final boolean alive;
	public final int[] pos;
	
	public CellsCurrentState(boolean isAlive, int[] aPos) {
		alive = isAlive;
		pos = aPos;
	}
}
