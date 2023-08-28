package ch.david.board;

public class Piece {
	
	private boolean color; // white = true
	private PieceType pieceType;
	private int moveCounter;
	
	public Piece(boolean color, PieceType pieceType) {
		this.color = color;
		this.pieceType = pieceType;
		moveCounter = 0;
	}
	
	enum PieceType {
	    KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN;
	}
	
	public String getPieceType() {
		return pieceType.toString();
	}
	
	public boolean getColor() {
		return color;
	}
	
	public void addOneToMoveCounter() {
		moveCounter++;
	}
	
	public int getMoveCounter() {
		return moveCounter;
	}
}
