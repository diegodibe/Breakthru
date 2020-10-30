package boardgame;

public class Board {
	/*
	 * int matrix, 0 for empty tiles, > 0 for yellow and < 0 for blue
	 * zobrist hash is stored as well and is used in alphabeta
	 * boolean game over that specify if the board is in a game over situation
	 */
	public final static int TILES_ROW_COL = 11;
	private int zobristHash;
	private int[][] board;
	private boolean isGameOver;
	
	public Board() {
		this.board = new int[Board.TILES_ROW_COL][Board.TILES_ROW_COL];
		this.isGameOver = false;
		setUpBoard();
	}

	private void setUpBoard() {
		for(int i = 0; i < TILES_ROW_COL; i++) {
			for(int j = 0; j < TILES_ROW_COL; j++) {
				if(((i == 1 || i == 9) && (j > 2 && j < 8)) | ((j == 1 || j == 9) && (i > 2 && i < 8))) 
					board[i][j] = -4;
				else if(((i == 3 || i == 7) && (j > 3 && j < 7)) | ((j == 3 || j == 7) && (i > 3 && i < 7)))
					board[i][j] = 6;
				else if(i == 5 && j == 5)
					board[i][j] = 8;
				else 
					board[i][j] = 0;
			}
		}
	}
	
	public void setZobristHash(int hash) {
		this.zobristHash = hash;
	}
	
	public void setValue(int value, Coordinate in) {
		board[in.getX()][in.getY()] = value;
	}
	
	public void gameIsOver() {
		this.isGameOver = true;
	}
	
	public void gameIsNotOver() {
		this.isGameOver = false;
	}
	
	public boolean isGameOver() {
		return this.isGameOver;
	}
	
	public int getZobristHash() {
		return this.zobristHash;
	}
	
	public int getValue(int x, int y) {
		return board[x][y];
	}

	public int getValue(Coordinate from) {
		return board[from.getX()][from.getY()];
	}
	
	public int[][] getBoard(){
		return board;
	}
	
	public Boolean sameAlliance(int x, int y, int x1, int y1) {
		if(getValue(x, y) < 0 && getValue(x1, y1) < 0 || getValue(x, y) > 0 && getValue(x1, y1) > 0 )
				return true;
		else 
			return false;
	}
	
	public String toString() {
		String s = "";
		String[] boardNotation = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};
		for(int i = 0; i < TILES_ROW_COL; i++) {
			if(TILES_ROW_COL - i < 10)
				s += String.valueOf(TILES_ROW_COL - i) + "  ";
			else
				s += String.valueOf(TILES_ROW_COL - i) + " ";
			for(int j = 0; j < TILES_ROW_COL; j++) {
				switch(board[i][j]) {
				case 0 : s += "-";
				break;
				case -4: s += "X";
				break;
				case 6: s += "O";
				break;
				case 8: s += "H";
				break;
				}
				s += " ";
			}
			s += "\n";
		}
		s += "  ";
		for(int i = 0; i < TILES_ROW_COL; i++ )
			s += " " + boardNotation[i];
		s += "\n";
		return s;
	}
}
