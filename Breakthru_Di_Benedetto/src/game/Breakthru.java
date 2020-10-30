package game;

import boardgame.Board;
import boardgame.Move;
import players.Gui;

public class Breakthru {
	
	public static void main(String[] args) {
		Board board = new Board();
		System.out.println(board);
		Move move = new Move();
		Gui gui = new Gui(board, move);
	}
}
