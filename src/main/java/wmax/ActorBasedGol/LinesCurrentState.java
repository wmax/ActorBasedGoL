package wmax.ActorBasedGol;

public class LinesCurrentState {
	public final boolean[] line;
	public final int lineNum;

	public LinesCurrentState(boolean[] line, int lineNum) {
		this.line = line;
		this.lineNum = lineNum;
	}
}
