package boardgame;
import java.util.ArrayList;

public class Move {
	/*
	 * class used to calculate and perform moves. makeMoves actually perform the move only if it is allowed and return a flag that rapresents
	 * the state of the turn, so 1 capturing/ship move or two moves
	 * to switch the values on the board the transition function is used. It also set the eventual game over variable of the board
	 */
	public int makeMove(Board board, Coordinate from, Coordinate to, Boolean player, int flag) { //player true for alliance 1 and false for -1
		if(board.getValue(from) == 0)
			System.out.println("You have not selected a piece");
		else if(board.getValue(from) < 0 && player || board.getValue(from) > 0 && !player) //if selected piece is not a null one or selected piece is other player's
			System.out.println("You have selected another player's piece");
		else if(board.getValue(from) == 8 && flag != 0)
			System.out.println("You cannot move the ship after a move");
		else {
			if(from.getX() == to.getX() || from.getY() == to.getY()) { //normal move
				ArrayList <Coordinate> movesAllowed = calculateAllowedMoves(board, from);
				for(int i = 0; i < movesAllowed.size(); i++) {
					if(to.equals(movesAllowed.get(i))) { //if to is in allowed moves
						transition(board, from, to, 0);
						if(board.getValue(to) == 8 && flag == 0) { //if it's gold ship can move just once, if it touches bounday wins
							flag = flag + 1;
							if(to.getX() == 0 | to.getY() == 0 | to.getX() == Board.TILES_ROW_COL - 1 | to.getY() == Board.TILES_ROW_COL -1) {
								flag = Integer.MAX_VALUE;
								return flag;
							}
						}
						return flag + 1;
					}
				}
				System.out.println("This is not an allowed move");//move not found in allowed moves
			}else if(true && flag == 0) {//attack move
				ArrayList <Coordinate> AttackMovesAllowed = calculateAllowedAttackMoves(board, from);
				for(int i = 0; i < AttackMovesAllowed.size(); i++) {
					if(to.equals(AttackMovesAllowed.get(i))) { //if to is in allowed moves
						if(board.getValue(to) == 8){// if capturing the ship
							flag = Integer.MIN_VALUE;
						}transition(board, from, to, 0); //capturing
						flag = flag + 1;
						return flag + 1;
					}
				}
				System.out.println("this is not an allowed capturing move");
			}else {
				if(flag != 0) {
					System.out.println("You cannot capture after moving");
				}else {
					System.out.println("You can't do this move");
				}
			}
		}
		return flag;
	}
	
	public ArrayList<Coordinate> calculateAllowedMoves(Board board, Coordinate from){
		ArrayList <Coordinate> moveAllowed = new ArrayList <Coordinate> (); 
		for(int i = from.getY() + 1; i < Board.TILES_ROW_COL; i++) { //checking free tiles in row of from position
			if(board.getValue(from.getX(), i) == 0)
				if(board.getValue(from) == 8)
					moveAllowed.add(0, new Coordinate(from.getX(), i));
				else
					moveAllowed.add(new Coordinate(from.getX(), i));
			else
				break;
		}
		for(int i = from.getY() - 1; i >= 0 ; i--) { //checking free tiles in row of from position 
			if(board.getValue(from.getX(), i) == 0) 
				if(board.getValue(from) == 8)
					moveAllowed.add(0, new Coordinate(from.getX(), i));
				else
					moveAllowed.add(new Coordinate(from.getX(), i));
			else	
				break;
		}
		for(int i = from.getX() + 1; i < Board.TILES_ROW_COL; i++) {  //checking free tiles in column of from position
			if(board.getValue(i, from.getY()) == 0) 
				if(board.getValue(from) == 8)
					moveAllowed.add(0, new Coordinate(i, from.getY()));
				else
					moveAllowed.add(new Coordinate(i, from.getY()));
			else
				break;
		}
		for(int i = from.getX() - 1; i >= 0; i--) { //checking free tiles in column of from position 
			if(board.getValue(i, from.getY()) == 0)  
				if(board.getValue(from) == 8)
					moveAllowed.add(0, new Coordinate(i, from.getY()));
				else
					moveAllowed.add(new Coordinate(i, from.getY()));
			else
				break;
		}
		return moveAllowed;
	}
	
	public ArrayList<Coordinate> calculateAllowedAttackMoves(Board board, Coordinate from){
		ArrayList <Coordinate> attackmoveAllowed = new ArrayList <Coordinate> ();
		if(from.getX() > 0 && from.getY() > 0) { 
			if(board.getValue(from.getX() - 1, from.getY() - 1) != 0//if  is inside the board
					&& !board.sameAlliance(from.getX() - 1, from.getY() - 1, from.getX(), from.getY()))//and is the other alliance it is a capture move
				attackmoveAllowed.add(new Coordinate(from.getX() - 1, from.getY() - 1));
		}if(from.getX() > 0 && from.getY() < Board.TILES_ROW_COL - 1) {
			if(board.getValue(from.getX() - 1, from.getY() + 1) != 0
					&& !board.sameAlliance(from.getX() - 1, from.getY() + 1, from.getX(), from.getY()))
				attackmoveAllowed.add(new Coordinate(from.getX() - 1, from.getY() + 1));
		}if(from.getX() < Board.TILES_ROW_COL - 1 && from.getY() > 0) {
			if(board.getValue(from.getX() + 1, from.getY() - 1) != 0
					&& !board.sameAlliance(from.getX() + 1, from.getY() - 1, from.getX(), from.getY()))
				attackmoveAllowed.add(new Coordinate(from.getX() + 1, from.getY() - 1));
		}if(from.getX() < Board.TILES_ROW_COL - 1 && from.getY() < Board.TILES_ROW_COL - 1) {
			if(board.getValue(from.getX() + 1, from.getY() + 1) != 0
					&& !board.sameAlliance(from.getX() + 1, from.getY() + 1, from.getX(), from.getY()))
				attackmoveAllowed.add(new Coordinate(from.getX() + 1, from.getY() + 1));
		}
		return attackmoveAllowed;
	}
	
	public boolean transition(Board board, Coordinate from, Coordinate to, int undo) {
		if(board.getValue(to) == 8)
			board.gameIsOver();
		else if(board.getValue(from) == 8) {
			if(to.getX() == 0 || to.getX() == Board.TILES_ROW_COL - 1 || to.getY() == 0 || to.getY() == Board.TILES_ROW_COL - 1)
				board.gameIsOver();
		}else if(undo == 8 || board.isGameOver()) 
			board.gameIsNotOver();
		int temp = board.getValue(from);
		board.setValue(undo, from);
		board.setValue(temp, to);
		return board.isGameOver();
	}
}
