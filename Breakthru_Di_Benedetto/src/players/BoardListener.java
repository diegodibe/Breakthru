package players;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import boardgame.Board;
import boardgame.Coordinate;
import boardgame.Move;
import players.Gui.TilePanel;

public class BoardListener implements MouseListener{
	/*
	 * class that actually implements the game, turns between human and ai player are switched here 
	 */
	private Gui gui;
	private AiPlayer aiPlayer;
	private TilePanel[][] boardTile;
	private Move move;
	private Board board;
	private boolean alliance;
	private TilePanel firstClickOnTile;
	private TilePanel secondClickOnTile;
	private ArrayList<Coordinate> allowedMoves;
	private int flag;
	private boolean turn; 
	
	BoardListener(Gui gui, Move move, Board board, boolean alliance){
		this.gui = gui;
		this.aiPlayer = new AiPlayer(board, move, !alliance);
		this.move = move;
		this.board = board;
		this.alliance = alliance;
		this.allowedMoves = new ArrayList<Coordinate>();
		this.flag = 0;
		if(alliance)
			this.turn = true;
		else {
			aiPlayer.aiTurn(board);
			this.turn = true;
		}
	}
	
	public void setBoard(TilePanel[][] boardTile) {
		this.boardTile = boardTile;
	}
	
	/*
	 * pressing on a tile invokes the listener, if it is the first tile pressed, on that tile the program calculate the allowed moves
	 * which are colored in green. pressing on the second tile will update the board configuration. if it is an illegal move nothing happens
	 */
	public void mouseClicked(MouseEvent e) {
		if(!gameOver()) {
			if(SwingUtilities.isLeftMouseButton(e) && turn){
				if(firstClickOnTile == null) {// first click 
					firstClickOnTile = (TilePanel) e.getComponent();
					if(board.getValue(firstClickOnTile.getCoordinate()) > 0 && alliance || board.getValue(firstClickOnTile.getCoordinate()) < 0 && !alliance){ 
						boardTile[firstClickOnTile.getCoordinate().getX()][firstClickOnTile.getCoordinate().getY()].setBackground(Color.lightGray);
						if(firstClickOnTile.getValueTile() != 0 || ((firstClickOnTile.getValueTile() == 8) && (flag != 1))) {
							this.allowedMoves = move.calculateAllowedMoves(board, firstClickOnTile.getCoordinate()); //change in coordinate
							this.allowedMoves.addAll(move.calculateAllowedAttackMoves(board, firstClickOnTile.getCoordinate()));
						}
						for(int i = 0; i < this.allowedMoves.size(); i++) {
							boardTile[this.allowedMoves.get(i).getX()][this.allowedMoves.get(i).getY()].setBackground(Color.green);
						}
					}else {
						System.out.println("this piece is not yours");
						firstClickOnTile = null;
					}
				}else if((TilePanel) e.getComponent() != firstClickOnTile && allowedMoves != null) { //second click
					secondClickOnTile = (TilePanel) e.getComponent();
					if(secondClickOnTile.getBackground() == Color.green) {
						setFlag(move.makeMove(board, firstClickOnTile.getCoordinate(), secondClickOnTile.getCoordinate(), alliance, flag));
						boardTile[firstClickOnTile.getCoordinate().getX()][firstClickOnTile.getCoordinate().getY()].updateBoard(board, secondClickOnTile.getCoordinate());
						if(!turn) {//if turn is done, is the aiPlayer's turn
							setFlag(aiPlayer.aiTurn(board));
							for(int i = 0; i < Board.TILES_ROW_COL; i++) {
								for(int j = 0; j < Board.TILES_ROW_COL; j++) {
									if(board.getValue(i, j) != boardTile[i][j].getValueTile()) {
										boardTile[i][j].setValue(board.getValue(i, j));
										boardTile[i][j].drawTile();
									}
								}
							}
						}
					}for(int i = 0; i < allowedMoves.size(); i++) {
						boardTile[allowedMoves.get(i).getX()][allowedMoves.get(i).getY()].drawTile();
					}
					firstClickOnTile.drawTile();
					allowedMoves = null;
					firstClickOnTile = null;
					secondClickOnTile = null;
				}
			}
		}
	}
	
	private void setFlag(int value) {
		this.flag = value;
		if(flag == 2) {
			this.flag = 0;
			turn = !turn;
		}
	}
	
	private boolean gameOver() {
		if(flag > 100 || flag < -100){
			System.out.println(".......");
			gui.gameOver(turn);
			return true;
		}else
			return false;
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
