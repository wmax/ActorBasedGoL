package wmax.ActorBasedGol;

public class Simulate {
	public final boolean[][] roi;
	public final boolean alive;
	public final int[] pos;
	
	public Simulate(boolean[][] aRoi, boolean isAlive, int[] aPos) {
		pos = aPos;
		roi = aRoi;
		alive = isAlive;
	}
}