package boardgame;

public class Coordinate {
	/*
	 * to simplify the notation of the positions in board, coordinate store the value x,y
	 * to string uses the y value as an index to an alphabet array. Used for the chess notation
	 */
	private int x;
	private int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Boolean equals(Coordinate target) {
		if(this.x == target.x && this.y == target.y)
			return true;
		else
			return false;
	}

	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;	
	}
	
	public String toString() {
		String[] alphabet = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};
		String coordinate = new String("["+ alphabet[this.y] + " - " + (Board.TILES_ROW_COL - this.x) + "]");
		return coordinate;
	}
}
