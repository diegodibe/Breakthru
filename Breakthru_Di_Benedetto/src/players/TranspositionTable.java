package players;

import boardgame.Board;
import boardgame.Coordinate;

public class TranspositionTable {
	/*
	 * array of the possible combination of the boards, initially random values.
	 * zobrist hash of the board is used as an index when a cell need to be retrieved/updated
	 */
	public class Cell{
		protected boolean player;
		protected int value;
		protected Boolean[] typeValue = {false, false, false}; //exact lower upper
		protected Coordinate[] bestMove = new Coordinate[4];
		protected int depth;
		protected int hashKey;
		
		private Cell() {
			this.depth = -1;
			this.hashKey = -1;
		}
	}
	private Cell[] table;
	
	public TranspositionTable(int pieces, int positions, int players) {
        table = new Cell[pieces * positions * players];
        for (int i = 0; i < table.length; i++) {
        	table[i] = new Cell();
            table[i].hashKey = (int) (((long) (Math.random() * Long.MAX_VALUE)) & 0xFFFFFFFF);
        }
    }
    
    public int computeHash(Board board) {
        int hash = 0;
    	for(int i = 0; i < Board.TILES_ROW_COL; i++) {
    		for(int j = 0; j < Board.TILES_ROW_COL; j++) {
    			if(board.getValue(i,j) != 0) {
    				int index = -1;
    				switch(board.getValue(i,j)) {
    				case -4: index = 0;
    				break;
    				case 6: index = 1;
    				break;
    				case 8: index = 2;
    				break;
    				}
    				hash = hash ^ table[index * (i * Board.TILES_ROW_COL) + j].hashKey;
    			}
    		}
    	}
        return hash; 
    }
    
    public Cell getCell(int hash) {
    	return table[Math.abs(hash % table.length)];  
    }
    
    public void setCellValues(int hash, int value,int depth, boolean player, Board board) {
    	int index = hash & (table.length - 1);
    	table[index].value = value;
    	table[index].depth = depth;
    	table[index].player = player;
    }         
    
    public void setCellTypeValue(int hash, int index) {
    	for(int i = 0; i < table[0].typeValue.length; i++)
    		if(i != index)
    			table[hash & (table.length - 1)].typeValue[i] = false;
    		table[hash & (table.length - 1)].typeValue[index] = true;
    }
    
    public void setCellMove(int hash, Coordinate from, Coordinate to, Coordinate from2, Coordinate to2) {
    	int index = hash & (table.length - 1);
    	table[index].bestMove[0] = from;
    	table[index].bestMove[1] = to;
    	table[index].bestMove[2] = from2;
    	table[index].bestMove[3] = to2;
    }
    
    public void changeHash(Board board, int piece, Coordinate from, Coordinate to) {
    	board.setZobristHash(table[piece * from.getX() * Board.TILES_ROW_COL + from.getY()].hashKey ^ board.getZobristHash());
    	board.setZobristHash(table[piece * to.getX() * Board.TILES_ROW_COL + to.getY()].hashKey ^ board.getZobristHash());
    }
}
