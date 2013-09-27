package wmax.ActorBasedGol;

public class SimulateLine {
	public final boolean[][] matrix;
	public final int lineNumber;
	public final int[] size;
	
	public SimulateLine(boolean[][] mat, int line, int[] size) {
		matrix = mat;
		lineNumber = line;
		this.size = size;
	}
}
