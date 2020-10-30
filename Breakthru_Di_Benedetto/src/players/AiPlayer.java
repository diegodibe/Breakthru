package players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import boardgame.Board;
import boardgame.Coordinate;
import boardgame.Move;
import game.Breakthru;
import players.KillerMoves.KillerMoveCell;
import players.TranspositionTable.Cell;

public class AiPlayer {
	private TranspositionTable tTable;
	private KillerMoves killerMoves;
	private final Boolean aiAlliance;
	private Move aiMove;
	private long timeLimit;
	private boolean importantMove;
	private GameInfo gameInfo;

	public AiPlayer(Board board, Move move, Boolean alliance) {
		this.tTable = new TranspositionTable(3, Board.TILES_ROW_COL * Board.TILES_ROW_COL, 2);
		this.aiAlliance = alliance;
		this.aiMove = move;
		this.importantMove = false;
		this.gameInfo = new GameInfo();
	}
	
	public int aiTurn(Board board) {
		System.out.println("----\nthinking ..");
		this.gameInfo.resetMoveInfo();
		this.killerMoves = new KillerMoves(3);
		long start = System.currentTimeMillis();   
		this.timeLimit = start + 5000;
		while(start < timeLimit & !board.isGameOver()) { //when is over it just not stop, still iterate thru all the nodes and then stop
			this.gameInfo.increasePlies();
			this.gameInfo.setMoveScore(alphaBeta(start, board, this.gameInfo.getPlies(), -10, 10, aiAlliance));
			System.out.println(this.gameInfo.gameInfoPly() + "\n--\n");
			//System.out.println(this.killerMoves);
			start = System.currentTimeMillis();
		}
		System.out.println(board + "\n" + this.gameInfo.gameInfoMove());
		int flagTurn = aiMove.makeMove(board, this.gameInfo.getBestMoveFrom(), this.gameInfo.getBestMoveTo(), aiAlliance, 0);
		if(this.gameInfo.getBestSecondMoveTo() != null) {
			flagTurn += aiMove.makeMove(board, this.gameInfo.getBestSecondMoveFrom(), this.gameInfo.getBestSecondMoveTo(), aiAlliance, 0);
		}
		return flagTurn;
	}
	
	private int alphaBeta(long time, Board board, int depth, int alpha, int beta, boolean player) {
		this.gameInfo.increaseNode();
		/*
		 * Transposition table
		 */
		board.setZobristHash(this.tTable.computeHash(board));
		int oldAlpha = alpha;
		Cell boardTable = tTable.getCell(board.getZobristHash());
		if(boardTable.depth >= this.gameInfo.getPlies() - depth && boardTable.player == player) {
			this.gameInfo.increaseTT();
			if(boardTable.typeValue[0]) {
				this.gameInfo.increaseTtUsed();
				if(depth == this.gameInfo.getPlies()) {
					this.gameInfo.setBestMoveFrom(boardTable.bestMove[0]); 
					this.gameInfo.setBestMoveTo(boardTable.bestMove[1]);
					this.gameInfo.setBestSecondMoveFrom(boardTable.bestMove[2]);
					this.gameInfo.setBestSecondMoveFrom(boardTable.bestMove[3]);
				}
				return boardTable.value;// do i have to make the move? 
			}else if(boardTable.typeValue[1]) 
				alpha = (alpha >= boardTable.value)? alpha: boardTable.value;
			else if(boardTable.typeValue[2])
				beta = (beta <= boardTable.value)? beta: boardTable.value;
			if(alpha >= beta)
				return boardTable.value;
		}
		/*
		 * leaf node, evaluation function or quiescence search
		 */
		if(board.isGameOver() || depth == 0  || time > timeLimit) {
			if(this.importantMove) 
				return -quiescenceSearch(System.currentTimeMillis() - 5000, board, depth, 2, alpha, beta, player);
			else {
				if(board.isGameOver())
					this.gameInfo.increaseGameOver();
				return evaluate(board, player, depth);
			}
		}
		/*
		 *search, start storing in a list of all the pieces 
		 */
		int score = Integer.MIN_VALUE;
		ArrayList <Coordinate> playerPieces = new ArrayList <Coordinate> ();
		for(int i = 0; i < Board.TILES_ROW_COL; i++) { 
			for(int j = 0; j < Board.TILES_ROW_COL; j++) {
				if(board.getValue(i, j) == 8 && player) {  //move ordering, ship first position
					playerPieces.add(0, new Coordinate(i, j));
				}if((board.getValue(i, j) == 6 && player) || (board.getValue(i, j) < 0 && !player)) 
					playerPieces.add(new Coordinate(i, j));
			}
		}
		/*
		 * checking all the moves for each piece, if is a capturing or a ship move, just one move is allowed, if it is a transition move
		 * two moves are allowed
		 */
		for(int i = 0; i < playerPieces.size(); i++) {
			ArrayList <Coordinate> allowedMoves = aiMove.calculateAllowedAttackMoves(board, playerPieces.get(i)); //attack moves in the beginning
			int attackingMoves = allowedMoves.size();
			allowedMoves.addAll(aiMove.calculateAllowedMoves(board, playerPieces.get(i)));
			for(int j = 0; j < allowedMoves.size(); j++) { 
				if(j < attackingMoves || board.getValue(playerPieces.get(i)) == 8) { //attacking/ ship move, just one move
					/*
					 * capturing or ship move
					 */
					int capturedPiece = board.getValue(allowedMoves.get(j)); 
					aiMove.transition(board, playerPieces.get(i), allowedMoves.get(j), 0);
					if(this.gameInfo.getPlies() > 1 && depth == 1)//if is the move before a leaf node, use quiescence search
						this.importantMove = true;//capturing/ship moves are threat as important 
					int value = -alphaBeta(System.currentTimeMillis(), board, depth - 1, - beta, - alpha, !player);
					aiMove.transition(board, allowedMoves.get(j), playerPieces.get(i), capturedPiece);
					this.importantMove = false;
					if(value > score) {
						score = value;
						if(depth == this.gameInfo.getPlies()) {
							this.gameInfo.setBestMoveFrom(playerPieces.get(i));
							this.gameInfo.setBestMoveTo(allowedMoves.get(j));
							this.gameInfo.setBestSecondMoveFrom(null);
							this.gameInfo.setBestSecondMoveTo(null);
						}
					}if(score >= alpha)
						alpha = score;					
					if(score >= beta) {
						this.gameInfo.increasePrunings();
						break;
					}
					storeInTT(false, board, score, value, oldAlpha, beta, depth, player, playerPieces.get(i), allowedMoves.get(j), null, null);
				}else {
					/*
					 * normal move
					 */
					aiMove.transition(board, playerPieces.get(i), allowedMoves.get(j), 0);
					for(int k = 0; k < playerPieces.size(); k++) {
						if(playerPieces.get(k) != playerPieces.get(i) && board.getValue(playerPieces.get(k)) != 8) {
							ArrayList <Coordinate> allowedSecondMoves = aiMove.calculateAllowedMoves(board, playerPieces.get(k));
							/*
							 * after have created all the possible second moves, check if one of them is in the killerMove table
							 * in that case the killer move is placed as the first move in the list
							 */
							allowedSecondMoves = this.killerMoves.sortByKillerMove(this.gameInfo, this.gameInfo.getPlies() - depth, playerPieces.get(i), allowedMoves.get(j), playerPieces.get(k), allowedSecondMoves);	
							for(int z = 0; z < allowedSecondMoves.size(); z++) { 
								aiMove.transition(board, playerPieces.get(k), allowedSecondMoves.get(z), 0);
								int value = -alphaBeta(System.currentTimeMillis(), board, depth - 1, - beta, - alpha, !player);
								aiMove.transition(board, allowedSecondMoves.get(z), playerPieces.get(k), 0);
								if(value > score) {
									score = value;
									if(depth == this.gameInfo.getPlies()) { 
										this.gameInfo.setBestMoveFrom(playerPieces.get(i));
										this.gameInfo.setBestMoveTo(allowedMoves.get(j));
										this.gameInfo.setBestSecondMoveFrom(playerPieces.get(k));
										this.gameInfo.setBestSecondMoveTo(allowedSecondMoves.get(z));
									}
								}if(score >= alpha)
									alpha = score;					
								if(score >= beta) {
									this.gameInfo.increasePrunings();
									this.gameInfo.increaseKillerStoredCounter();
									this.killerMoves.addKillerMove(this.gameInfo.getPlies() - depth, playerPieces.get(i), allowedMoves.get(j), playerPieces.get(k), allowedSecondMoves.get(z), value);
									break;
								}
								aiMove.transition(board, allowedMoves.get(j), playerPieces.get(i), 0);
								storeInTT(false, board, score, value, oldAlpha, beta, depth, player, playerPieces.get(i), allowedMoves.get(j), playerPieces.get(k), allowedSecondMoves.get(z));
								aiMove.transition(board, playerPieces.get(i), allowedMoves.get(j), 0);
							}
						}
					}
					aiMove.transition(board, allowedMoves.get(j), playerPieces.get(i), 0);
				}
			}
		}
		return score;
	}
	
	private void storeInTT(boolean quiscence, Board board, int score, int value, int oldAlpha, int beta, int depth, boolean player, Coordinate from, Coordinate to, Coordinate secondFrom, Coordinate secondTo) {
		tTable.setCellMove(board.getZobristHash(), from, to, secondFrom, secondTo);
		if(score <= oldAlpha)
			tTable.setCellTypeValue(board.getZobristHash(), 2);
		else if(score >= beta)
			tTable.setCellTypeValue(board.getZobristHash(), 1);
		else
			tTable.setCellTypeValue(board.getZobristHash(), 0);
		tTable.setCellValues(board.getZobristHash(), value, this.gameInfo.getPlies() + ((quiscence)? 2: 0) - depth, player, board);
	}
	
	private int quiescenceSearch(long time, Board board, int depth, int searchDepth, int alpha, int beta, boolean player) {
		/*
		 * search through ship moves, the function is invoked when an important move has been done(capturnig/ship move) 
		 */
		this.gameInfo.increaseNode();
		this.gameInfo.increaseQuiscence();
		int oldAlpha = alpha;
		boolean moveToWin = false;
		int score = evaluate(board, player, depth);
		alpha = (alpha >= score)? alpha: score;
		if(alpha >= beta || searchDepth == 0 || time > this.timeLimit || board.isGameOver()) {
			if(board.isGameOver())
				this.gameInfo.increaseGameOver();
			return score;
		}ArrayList <Coordinate> allowedShipMoves;
		Coordinate shipPosition = null;
		for(int i = 0; i < Board.TILES_ROW_COL; i++) {
			for(int j = 0; j < Board.TILES_ROW_COL; j++) {
				if(board.getValue(i, j) == 8) {
					shipPosition = new Coordinate(i, j);
					break;
				}
			}
		}
		/*
		 * if it is the yellow turn, i check for a breakthrough
		 */
		if(player) {
			allowedShipMoves = aiMove.calculateAllowedMoves(board, shipPosition);
			for(int i = 0; i < allowedShipMoves.size(); i++) {
				moveToWin = aiMove.transition(board, shipPosition, allowedShipMoves.get(i), 0);
				score = -quiescenceSearch(System.currentTimeMillis(), board, depth - 1, searchDepth - 1, - beta, - alpha, !player);
				aiMove.transition(board, allowedShipMoves.get(i), shipPosition, 0);
				alpha = (alpha >= score)? alpha: score;
				if(alpha >= beta) {
					this.gameInfo.increasePrunings();
					break;
				}
				storeInTT(true, board, score, score, oldAlpha, beta, depth - searchDepth, player, shipPosition, allowedShipMoves.get(i), null, null);
			}
		}else {
			/*
			 * if it is not the yellow turn, check if ship can be captured
			 */
			allowedShipMoves = aiMove.calculateAllowedAttackMoves(board, shipPosition);
			for(int i = 0; i < allowedShipMoves.size(); i++) {
				int capturedPiece = board.getValue(allowedShipMoves.get(i));
				moveToWin = aiMove.transition(board, shipPosition, allowedShipMoves.get(i), 0);
				score = -quiescenceSearch(System.currentTimeMillis(), board, depth - 1, searchDepth - 1, - beta, - alpha, !player);
				aiMove.transition(board, allowedShipMoves.get(i), shipPosition, capturedPiece);
				alpha = (alpha >= score)? alpha: score;
				if(alpha >= beta) {
					this.gameInfo.increasePrunings();
					break;
				}
				storeInTT(true, board, score, score, oldAlpha, beta, depth - searchDepth, player, shipPosition, allowedShipMoves.get(i), null, null);
			}
		}
		if(moveToWin)
			board.gameIsNotOver();
		return score;
	}

	private int evaluate(Board board, Boolean alliance, int depth) { 
		/*
		 * starting storing ship position, if it s not found(position = (-1, -1))
		 * or the ship is along the edges it is game over
		 */
		int[] ship = new int[] {-1, -1}; //store ship coordinate
		for(int i = 0; i < Board.TILES_ROW_COL; i++) {
			for(int j = 0; j < Board.TILES_ROW_COL; j++) {
				if(board.getValue(i, j) == 8) { //coordinate ship
					ship[0] = i;
					ship[1] = j;
				}
			}
		}
		int goldScore = 0;
		int blueScore = 0; 
		if(ship[0] == -1) { //false win
			blueScore = 999999999 - (this.gameInfo.getPlies() - depth); //
		}else if(ship[0] == 0 || ship[0] == Board.TILES_ROW_COL - 1 ||
					ship[1] == 0 || ship[1] == Board.TILES_ROW_COL - 1) {
			goldScore = 999999999 - (this.gameInfo.getPlies() - depth); //
		}
		/*
		 * calculate ship grey alliance score, it is gold turn, i return the subtraction
		 * if it is the grey turn i return the negation of the subtraction
		 */
		if(!board.isGameOver()) {
			goldScore += goldEvaluation(board, new Coordinate(ship[0], ship[1]), alliance);
			blueScore += blueEvaluation(board, new Coordinate(ship[0], ship[1]), alliance);
		}
		if(!alliance)
			return (goldScore - blueScore)*-1; 
		else
			return (goldScore - blueScore);	
	}

	private int blueEvaluation(Board board, Coordinate shipPosition, Boolean alliance) {
		/*
		 * calculate number of pieces (material evaluation)
		 * distance to ship, the goal is to get closer
		 * if a piece can be captured, negative score 
		 * then checking if grey can capture the ship
		 */
		int greyPieces = 0;
		int distanceToShip = 0;
		int getCapturedPosition = 0;
		for(int i = 0; i < Board.TILES_ROW_COL; i++) {
			for(int j = 0; j < Board.TILES_ROW_COL; j++) {
				if(board.getValue(i, j) < 0) { 
					greyPieces ++;
					distanceToShip += (int)Math.sqrt(Math.pow(shipPosition.getX() - i, 2) + Math.pow(shipPosition.getY() - j, 2)) - 4; //pitagora for distance, 4 is the noremal distance
					//if(aiMove.calculateAllowedAttackMoves(board, new Coordinate(i, j)).size() > 0)
						//getCapturedPosition++;
				}
			}
		}
		greyPieces = (greyPieces - 20) * 3;
		int mate = 0;
		ArrayList <Coordinate> captureShip = aiMove.calculateAllowedAttackMoves(board, shipPosition);
		for(int i = 0; i < captureShip.size(); i++) {
			aiMove.transition(board, shipPosition, captureShip.get(i), 0);
			if(aiMove.calculateAllowedAttackMoves(board, captureShip.get(i)).size() > 0)// && !turn if ship captures back, game over
				mate += 1000;
			else
				mate += 50;
			aiMove.transition(board, captureShip.get(i), shipPosition, -4);
			if(aiMove.calculateAllowedAttackMoves(board, captureShip.get(i)).size() > 1)
				mate = (mate >= 1000)? mate/10 : -2;
		}
		return greyPieces + mate - distanceToShip - getCapturedPosition / 2;
	}

	private int goldEvaluation(Board board, Coordinate shipPosition, Boolean alliance) {
		/*
		 * material, bed position as grey evaluation, reward if the ship is covered diagonally
		 * than checking for possible breakthrough
		 */
		int goldPieces = 0;
		int getCapturedPosition = 0;
		int shipCovered = 0;
		for(int i = 0; i < Board.TILES_ROW_COL; i++) {
			for(int j = 0; j < Board.TILES_ROW_COL; j++) { 
				if(board.getValue(i, j) > 0 && board.getValue(i, j) != 8) {
					goldPieces ++;
					//if(aiMove.calculateAllowedAttackMoves(board, new Coordinate(i, j)).size() > 0)
						//getCapturedPosition++;
				}else if(board.getValue(i, j) == 8) {
					if(i > 0 && j > 0) { 
						if(board.getValue(i - 1, j - 1) > 0)
							shipCovered ++;
					}if(i > 0 && j < Board.TILES_ROW_COL - 1) {
						if(board.getValue(i - 1, j + 1) > 0)	
							shipCovered ++;
					}if(i < Board.TILES_ROW_COL - 1 && j > 0) {
						if(board.getValue(i + 1, j - 1) > 0)
							shipCovered ++;
					}if(i < Board.TILES_ROW_COL - 1 && j < Board.TILES_ROW_COL - 1) {
						if(board.getValue(i + 1, j + 1) > 0)
							shipCovered ++;
					}
				}
			}
		}
		if(alliance)
			shipCovered = shipCovered * 2;
		goldPieces = (goldPieces - 12) * 5;
		int breakthru = 0;
		ArrayList <Coordinate> shipMoves = aiMove.calculateAllowedMoves(board, shipPosition);
		for(int i = 0; i < shipMoves.size(); i++) {
			if(shipMoves.get(i).getX() == 0 || shipMoves.get(i).getX() == Board.TILES_ROW_COL - 1||
					shipMoves.get(i).getY() == 0 || shipMoves.get(i).getY() == Board.TILES_ROW_COL - 1)
				if(aiMove.calculateAllowedAttackMoves(board, shipPosition).size() == 0)
					breakthru += 1500;
		}
		return goldPieces + breakthru + shipCovered +(shipMoves.size() - 4) * 3 - getCapturedPosition / 2;
	}
}
