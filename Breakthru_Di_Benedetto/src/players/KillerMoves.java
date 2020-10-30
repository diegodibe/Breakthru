package players;

import java.util.ArrayList;

import boardgame.Coordinate;

public class KillerMoves {
	/*
	 * killer moves are shifted on the right of the array when a new killer move is added
	 * sortByKillerMove checks is in the list of most there is a killer move, in that case the killer
	 * move is placed in front 
	 */
	public class KillerMoveCell{
		protected Coordinate from;
		protected Coordinate to;
		protected Coordinate secondFrom;
		protected Coordinate secondTo;
		protected int value;
		
		KillerMoveCell(Coordinate from, Coordinate to, Coordinate secondFrom, Coordinate secondTo, int value){
			this.from = from;
			this.to = to;
			this.secondFrom = secondFrom;
			this.secondTo = secondTo;
			this.value = value;
		}
		
		protected boolean isCellEmpty() {
			if(this.from == null)
				return true;
			else
				return false;
		}
	}
	KillerMoveCell[][] killerMoves;
	
	public KillerMoves(int ply) {
		killerMoves = new KillerMoveCell[ply][2];
		for(int i = 0; i < killerMoves.length; i++) {
			for(int j = 0; j < killerMoves[i].length; j++) {
				killerMoves[i][j] = null;
			}
		}
	}
	
	public void addKillerMove(int ply, Coordinate from, Coordinate to, Coordinate secondFrom, Coordinate secondTo, int value) {
		for (int i = killerMoves[ply].length - 2; i >= 0; i--) 
		    killerMoves[ply][i + 1] = killerMoves[ply][i];
		killerMoves[ply][0] = new KillerMoveCell(from, to, secondFrom, secondTo, value);
	}
	
	public ArrayList<Coordinate> sortByKillerMove(GameInfo gameInfo, int ply, Coordinate from, Coordinate to, Coordinate secondFrom, ArrayList<Coordinate> allowedMoves) {
		for(int i = 0; i < allowedMoves.size(); i ++) {
			if(isKillerMove(ply, from, to, secondFrom, allowedMoves.get(i))) {
				allowedMoves.set(0, allowedMoves.get(i));
				gameInfo.increaseKiller();
			}
		}
		return allowedMoves;
	}
	
	private boolean isKillerMove(int ply, Coordinate from, Coordinate to, Coordinate secondFrom, Coordinate secondTo) {
		for (int i = 0; i < killerMoves[ply].length; i++) {
		   if(killerMoves[ply][i] != null && killerMoves[ply][i].from == from && killerMoves[ply][i].to == to && 
		    		killerMoves[ply][i].secondFrom == secondFrom && killerMoves[ply][i].secondTo == secondTo)
		    	return true;
		}
		return false;
	}
	
	public String toString() {
		String s = "";
		for(int i = 0; i < killerMoves.length; i++) {
			s += "	ply: " + i + "\n";
			for(int j = 0; j < killerMoves[i].length; j++) {
				if(killerMoves[i][j] != null)
				s += "   " + killerMoves[i][j].from + "--" + killerMoves[i][j].to + "\n"
				+ "   " + killerMoves[i][j].secondFrom + "--" + killerMoves[i][j].secondTo + "\n";
			}
		}
		return s;
	}
}
