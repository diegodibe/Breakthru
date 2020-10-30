package players;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import boardgame.Board;
import boardgame.Coordinate;
import boardgame.Move;

public class Gui {
	/*
	 * three different layers, in the main frame i create listener. Inside the main frame i create the panel of the board
	 * inside the panel 122 panel for the tiles, where the shared listener is added
	 */
	private final JFrame visualGame;
	private final BoardPanel boardPanel;
	
	private final static Dimension FINAL_DIMENSION = new Dimension (600, 700);

	public Gui(Board board, Move move) {
		this.visualGame = new JFrame("Breakthru");
		this.visualGame.setLayout(new BorderLayout());
		this.visualGame.setSize(FINAL_DIMENSION);
		boolean alliance = setAlliance();
		BoardListener  boardListener = new BoardListener(this, move, board, alliance);
		this.boardPanel = new BoardPanel(board, move, alliance, boardListener);
		this.boardPanel.setPreferredSize(new Dimension(610,600));
		this.visualGame.add(this.boardPanel, BorderLayout.CENTER);
		this.visualGame.setVisible(true);
	}
	
	//option dialog to set the alliance
	private boolean setAlliance() {
		String[] alliances = {"Yellow", "Blue"};
		boolean[] allianceValue = {true,false};
		int index = JOptionPane.showOptionDialog(null, "choose which alliance you want to play with",
                "choose alliance",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, alliances, alliances[0]);
		return allianceValue[index];
	}
	
	private Gui getSuperGui() {
		return this;
	}
	
	public void gameOver(boolean winner) {
		System.out.println("game over");
		String string = "";
		string += (winner)? "Human": "Ai";
		string += " player won!";
		JOptionPane.showMessageDialog(null,
			    string,
			    "Breakthru",
			    JOptionPane.PLAIN_MESSAGE);
	}
	
	public class BoardPanel extends JPanel{
		/*
		 * board panel whit 122 tiles panel
		 * when a move is done updateBoard is invoked to switch the tile values
		 */
		final TilePanel[][] boardTile;
		
		BoardPanel(Board board, Move humanMove, boolean alliance, BoardListener boardListener){
			super(new GridLayout(Board.TILES_ROW_COL + 1,Board.TILES_ROW_COL +1));
			this.boardTile = new TilePanel[Board.TILES_ROW_COL + 1][Board.TILES_ROW_COL + 1];
			for(int i = 0; i <= Board.TILES_ROW_COL; i++) {
				for(int j = 0; j <= Board.TILES_ROW_COL; j++) {
					TilePanel tilePanel;
					if(i < Board.TILES_ROW_COL && j < Board.TILES_ROW_COL )
						tilePanel = new TilePanel(i, j, board.getValue(i, j), boardListener); // check if in tile is necessary having the board
					else
						tilePanel = new TilePanel(i, j);
					this.boardTile[i][j] = tilePanel;
					add(tilePanel);
				}
			}
			boardListener.setBoard(boardTile);
		}
		
		private void updateBoard(Board board, Coordinate from, Coordinate to) {
			boardTile[from.getX()][from.getY()].setValue(board.getValue(from));; 
			boardTile[from.getX()][from.getY()].drawTile();
			boardTile[to.getX()][to.getY()].setValue(board.getValue(to));
			boardTile[to.getX()][to.getY()].drawTile();
			this.repaint();
		}
	}
	
	public class TilePanel extends JPanel{
		/*
		 * tile panel of the board, pgn of the pieces are loaded using the int value, which is taken from the board
		 * chess board notation is added as tiles as well
		 */
		private Coordinate tileCoordinate;
		private int valueTile;
		
		private final Dimension TILE_DIMENSION = new Dimension(10,10);
		
		TilePanel(int x, int y, int value, BoardListener boardListener){
			this.tileCoordinate = new Coordinate(x, y);
			this.valueTile = value;
			this.setPreferredSize(TILE_DIMENSION);
			this.drawTile();
			this.addMouseListener(boardListener);
		}
		
		public TilePanel(int i, int j) { 
			this.setPreferredSize(TILE_DIMENSION);
			String[] boardNotation = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};			
			JLabel label = null;
			if(i != j) {
				if(i == Board.TILES_ROW_COL && j < Board.TILES_ROW_COL)
					label = new JLabel(boardNotation[j]);
				else if(j == Board.TILES_ROW_COL && i < Board.TILES_ROW_COL)
					label = new JLabel(Integer.toString((Board.TILES_ROW_COL) - i));
				this.add(label, BorderLayout.CENTER);
			}
		}

		public void drawTile() {
			this.removeAll();
			if(tileCoordinate.getX() > 2 && tileCoordinate.getY() > 2 && tileCoordinate.getX() < 8 && tileCoordinate.getY() < 8)
				setBorder(BorderFactory.createLineBorder(Color.white,4));
			else
				setBorder(BorderFactory.createLineBorder(Color.white));
			setBackground(Color.black);
			if(this.valueTile != 0) {
				/*URL path = null;
				switch(valueTile) {
					//case -4: path = this.getClass().getClassLoader().getResource("blue.png");
					break;
					//case 6: path = this.getClass().getClassLoader().getResource("yellow.png");
					break;
					//case 8: path = this.getClass().getClassLoader().getResource("ship1.png");
					break;
				}
				ImageIcon imageIcon = new ImageIcon(new ImageIcon(path.getPath()).getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT));
				JLabel label = new JLabel();
				label.setIcon(imageIcon);
				this.add(label, BorderLayout.CENTER);
				this.validate();*/
				switch(valueTile) {
				case -4: setBackground(Color.blue);
				break;
				case 6: setBackground(Color.yellow);
				break;
				case 8: setBackground(Color.gray);
				break;
				}
			}
		}
		
		public void updateBoard(Board board, Coordinate to) {
			((BoardPanel) boardPanel).updateBoard(board, tileCoordinate, to);
		}

		public Coordinate getCoordinate() {
			return this.tileCoordinate;
		}

		public int getValueTile() {
			return this.valueTile;
		}
		
		public Gui getGui() {
			return getSuperGui();
		}
		
		public void setValue(int value) {
			this.valueTile = value;
		}
	}
}
