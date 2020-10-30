package players;

import boardgame.Coordinate;

public class GameInfo {
	/*
	 * class to store the counters during an ai turn 
	 */
	private int plies;
	private int nodes;
	private int prunings;
	private int transpositionTable;
	private int transpositionTableUsed;
	private int quiscence;
	private int killerMove;
	private int killerMoveStored;
	private int gameOver;
	private int moveScore;
	private Coordinate bestMoveFrom;
	private Coordinate bestMoveTo;
	private Coordinate bestSecondMoveTo;
	private Coordinate bestSecondMoveFrom;
	
	public void resetMoveInfo() {
		this.plies = 0;
		this.nodes = 0;
		this.prunings = 0;
		this.transpositionTable = 0;
		this.transpositionTableUsed = 0;
		this.quiscence = 0;
		this.killerMove = 0;
		this.killerMoveStored = 0;
		this.gameOver = 0;
		this.moveScore = 0;
		this.bestMoveFrom = null;
		this.bestMoveTo = null;
		this.bestSecondMoveFrom = null;
		this.bestSecondMoveTo = null;
	}
	
	public void increasePlies() {
		this.plies++;
	}
	
	public void increaseNode() {
		this.nodes++;
	}
	
	public void increasePrunings() {
		this.prunings++;
	}
	
	public void increaseTT() {
		this.transpositionTable++;
	}
	
	public void increaseTtUsed() {
		this.transpositionTableUsed++;
	}
	
	public void increaseQuiscence() {
		this.quiscence++;
	}
	
	public void increaseKiller() {
		this.killerMove++;
	}
	
	public void increaseKillerStoredCounter() {
		this.killerMoveStored++;
	}
	
	public void increaseGameOver() {
		this.gameOver++;
	}

	public void setMoveScore(int score) {
		this.moveScore = score;
	}
	
	public void setBestMoveFrom(Coordinate bestMoveFrom) {
		this.bestMoveFrom = bestMoveFrom;
	}
	
	public void setBestMoveTo(Coordinate bestMoveTo) {
		this.bestMoveTo = bestMoveTo;
	}
	
	public void setBestSecondMoveFrom(Coordinate bestSecondMoveFrom) {
		this.bestSecondMoveFrom = bestSecondMoveFrom;
	}
	
	public void setBestSecondMoveTo(Coordinate bestSecondMoveTo) {
		this.bestSecondMoveTo = bestSecondMoveTo;
	}
	
	public int getPlies() {
		return this.plies;
	}
	
	public int getMoveScore() {
		return this.moveScore;
	}
	
	public Coordinate getBestMoveFrom() {
		return this.bestMoveFrom;
	}
	
	public Coordinate getBestMoveTo() {
		return this.bestMoveTo;
	}
	
	public Coordinate getBestSecondMoveFrom() {
		return this.bestSecondMoveFrom;
	}
	
	public Coordinate getBestSecondMoveTo() {
		return this.bestSecondMoveTo;
	}
	
	public String gameInfoMove() {
		String s =  "score: " + this.moveScore + "\n"
				+ "move: \n" 
				+ this.bestMoveFrom.toString() + "-" + this.bestMoveTo.toString() + "\n";
		if(this.bestSecondMoveFrom != null) {
			s += this.bestSecondMoveFrom.toString() + "-" + this.bestSecondMoveTo.toString() + "\n"; 
		}
		return s;
	}
	
	public String gameInfoPly() {
		return  "plies: " + this.plies + "\n"
				+ "nodes: " + this.nodes + "\n"
				+ "prunings: " + this.prunings + "\n"
				+ "TT: " + this.transpositionTable + "\n"
				+ "TT exact value:" + this.transpositionTableUsed + "\n"
				+ "quiscence: " + this.quiscence + "\n"
				+ "killer used: " + this.killerMove + "\n"
				+ "killer stored: " + this.killerMoveStored + "\n"
				+ "gameOver: " + this.gameOver;
	}
}
