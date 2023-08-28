package ch.david.board;

import java.util.ArrayList;

import ch.david.board.Piece.PieceType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Board {
	
	private Piece[][] piecePosOnBoard;
	private ArrayList<String> moves;
	
	private boolean turn; // white = true;
	
	public Board() {
		
		moves = new ArrayList<>();
		turn = true; // white starts
		
		// Creating the standard 8x8 Chess Board with all the Pieces.
		boolean color = true;
		int heavyPiecesRow = 0;
		int pawnRow = 1;
		piecePosOnBoard = new Piece[8][8];
		for(int s = 0; s <= 1; s++) {
			piecePosOnBoard[heavyPiecesRow][0] = new Piece(color, PieceType.ROOK);
			piecePosOnBoard[heavyPiecesRow][1] = new Piece(color, PieceType.KNIGHT);
			piecePosOnBoard[heavyPiecesRow][2] = new Piece(color, PieceType.BISHOP);
			piecePosOnBoard[heavyPiecesRow][3] = new Piece(color, PieceType.QUEEN);
			piecePosOnBoard[heavyPiecesRow][4] = new Piece(color, PieceType.KING);
			piecePosOnBoard[heavyPiecesRow][5] = new Piece(color, PieceType.BISHOP);
			piecePosOnBoard[heavyPiecesRow][6] = new Piece(color, PieceType.KNIGHT);
			piecePosOnBoard[heavyPiecesRow][7] = new Piece(color, PieceType.ROOK);
			
			for(int i = 0; i <= 7; i++) {
				piecePosOnBoard[pawnRow][i] = new Piece(color, PieceType.PAWN);
			}
			
			color = !color;
			heavyPiecesRow = 7;
			pawnRow = 6;
		}
	}
	
	public Piece[][] getPiecePosOnBoard() {
		return piecePosOnBoard;
	}
	
	public void tryToMove(int startSquareX, int startSquareY, int endSquareX, int endSquareY) {
		if(piecePosOnBoard[startSquareX][startSquareY] != null) {
			if(moves.contains(Integer.toString(endSquareX) + Integer.toString(endSquareY)) && piecePosOnBoard[startSquareX][startSquareY].getColor() == turn) {
				
				// Move piece
				piecePosOnBoard[endSquareX][endSquareY] = piecePosOnBoard[startSquareX][startSquareY];
				piecePosOnBoard[startSquareX][startSquareY] = null;
				piecePosOnBoard[endSquareX][endSquareY].addOneToMoveCounter();

				turn = !turn;
				clearMovesInCache();
				testForEndOfGame(piecePosOnBoard[endSquareX][endSquareY].getColor());
			}
		}
	}
	
	public void testForEndOfGame(boolean didColorLose) { // white = true
		
		// If a king is captured the game ends
		boolean didLose = true;
		
		for(int i = 0; i <= 7; i++) {
			for(int s = 0; s <= 7; s++) {
				if(piecePosOnBoard[i][s] != null) {
					if(piecePosOnBoard[i][s].getColor() == didColorLose && piecePosOnBoard[i][s].getPieceType() == "KING") {
						didLose = false;
					}
				}			
			}
		}
		
		if(didLose) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Spiel beendet");
			if(didColorLose) {
				alert.setHeaderText("Weisser König wurde geschlagen!");
				alert.setContentText("Schwarz hat gewonnen");
			} else {
				alert.setHeaderText("Schwarzer König wurde geschlagen!");
				alert.setContentText("Weiss hat gewonnen");
			}

			alert.showAndWait();
		}
	}
	
	public void clearMovesInCache() {
		moves.clear();
	}
	
	public ArrayList<String> getMoves() {
		return moves;
	}
	
	public boolean getTurnColor() {
		return turn;
	}
	
	public void getLegalMovesOf(int pieceSquareX, int pieceSquareY) {
		// Saves all legal moves in the moves list 
		if(piecePosOnBoard[pieceSquareX][pieceSquareY] != null)
		switch(piecePosOnBoard[pieceSquareX][pieceSquareY].getPieceType()) {
		case "KING":
			int[][] kingMoves = {
				    {1, 0}, {1, 1}, {1, -1}, {0, 1},
				    {0, -1}, {-1, 0}, {-1, 1}, {-1, -1}
				};

			
			for (int[] move : kingMoves) {
				int PosX = pieceSquareX + move[0];
			    int PosY = pieceSquareY + move[1];

			    if(PosX >= 0 && PosX <= 7 && PosY >= 0 && PosY <= 7) {
			        if(piecePosOnBoard[PosX][PosY] == null || piecePosOnBoard[PosX][PosY].getColor() != piecePosOnBoard[pieceSquareX][pieceSquareY].getColor()) {
			            moves.add(Integer.toString(PosX) + Integer.toString(PosY));
			        }
			    }
			}
			
			// Castles not implemented
			// Check not implemented
			
			break;
		case "BISHOP":
			int[][] bishopDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
			moveGeneration(bishopDirections, pieceSquareX, pieceSquareY);
			break;
			
		case "KNIGHT":
			int[][] knightMoves = {
				    {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
				    {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
				};

				for (int[] move : knightMoves) {
				    int PosX = pieceSquareX + move[0];
				    int PosY = pieceSquareY + move[1];

				    if (PosX >= 0 && PosX <= 7 && PosY >= 0 && PosY <= 7) {
				        if (piecePosOnBoard[PosX][PosY] == null || piecePosOnBoard[PosX][PosY].getColor() != piecePosOnBoard[pieceSquareX][pieceSquareY].getColor()) {
				            moves.add(Integer.toString(PosX) + Integer.toString(PosY));
				        }
				    }
				}
			break;
		case "ROOK":
			int[][] rookDirections = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
			moveGeneration(rookDirections, pieceSquareX, pieceSquareY);
			break;
			
		case "PAWN":
			int direction = piecePosOnBoard[pieceSquareX][pieceSquareY].getColor() ? 1 : -1; // 1 for white, -1 for black

		    if (pieceSquareX + 1 * direction >= 0 && pieceSquareX + 1 * direction <= 7) {
		    	// Straight Moves
		        if (piecePosOnBoard[pieceSquareX +  1 * direction][pieceSquareY] == null) {
		            moves.add(Integer.toString(pieceSquareX + 1 * direction) + Integer.toString(pieceSquareY));

		            
		            if (piecePosOnBoard[pieceSquareX + 2 * direction][pieceSquareY] == null && piecePosOnBoard[pieceSquareX][pieceSquareY].getMoveCounter() == 0) {
		                moves.add(Integer.toString(pieceSquareX + 2 * direction) + Integer.toString(pieceSquareY));
		            }
		        }
		        
		     // Capture Moves
			    for (int dyP : new int[] {-1, 1}) {
			        int PosY = pieceSquareY + dyP;
			        if (PosY >= 0 && PosY <= 7 && pieceSquareX <= 5) {
			            Piece potentialCapture = piecePosOnBoard[pieceSquareX + direction][PosY];
			            if (potentialCapture != null && potentialCapture.getColor() != piecePosOnBoard[pieceSquareX][pieceSquareY].getColor()) {
			                moves.add(Integer.toString(pieceSquareX + direction) + Integer.toString(PosY));
			            }
			        }
			    }
		    }			
			// En passant not implemented
		    // Promoting not implemented
			break;
		case "QUEEN":
			int[][] QueenDirections = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
			moveGeneration(QueenDirections, pieceSquareX, pieceSquareY);
			break;
		}
	}
	
	public void moveGeneration(int[][] directions, int pieceSquareX, int pieceSquareY) {
		for (int[] direction : directions) {
		    for (int i = 1; i < 8; i++) {
		        int PosX = pieceSquareX + direction[0] * i;
		        int PosY = pieceSquareY + direction[1] * i;

		        if (PosX >= 0 && PosX <= 7 && PosY >= 0 && PosY <= 7) {
		            if (piecePosOnBoard[PosX][PosY] == null) {
		                moves.add(Integer.toString(PosX) + Integer.toString(PosY));
		            } else if (piecePosOnBoard[pieceSquareX][pieceSquareY].getColor() != piecePosOnBoard[PosX][PosY].getColor()) {
		                moves.add(Integer.toString(PosX) + Integer.toString(PosY));
		                break;
		            } else {
		                break;
		            }
		        }
		    }
		}
	}
	
}
