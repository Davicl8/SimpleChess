package ch.david.application;

import java.util.ArrayList;
import java.util.Map;

import ch.david.board.Board;
import ch.david.board.Piece;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {
    private final int TILE_SIZE = 90;
    private final int BOARD_SIZE = 8;
    
    private Rectangle[][] squares = new Rectangle[8][8];
    private Rectangle lastClickedSquare;
    private int lastClickedSquarePosX;
    private int lastClickedSquarePosY;
    private ArrayList<ImageView> pieceImagesOnBoard;

    Board board;
    GridPane visualBoard;
    
    Map<String, Image> whitePiecesMap;
    Map<String, Image> blackPiecesMap;
    
    Image whiteKing = new Image(getClass().getResourceAsStream("/images/white_king.png"));
    Image whiteQueen = new Image(getClass().getResourceAsStream("/images/white_queen.png"));
    Image whiteBishop = new Image(getClass().getResourceAsStream("/images/white_bishop.png"));
    Image whiteKnight = new Image(getClass().getResourceAsStream("/images/white_knight.png"));
    Image whiteRook = new Image(getClass().getResourceAsStream("/images/white_rook.png"));
    Image whitePawn = new Image(getClass().getResourceAsStream("/images/white_pawn.png"));
    
    Image blackKing = new Image(getClass().getResourceAsStream("/images/black_king.png"));
    Image blackQueen = new Image(getClass().getResourceAsStream("/images/black_queen.png"));
    Image blackBishop = new Image(getClass().getResourceAsStream("/images/black_bishop.png"));
    Image blackKnight = new Image(getClass().getResourceAsStream("/images/black_knight.png"));
    Image blackRook = new Image(getClass().getResourceAsStream("/images/black_rook.png"));
    Image blackPawn = new Image(getClass().getResourceAsStream("/images/black_pawn.png"));

    @Override
    public void start(Stage primaryStage) {
        try {

        	board = new Board();
        	visualBoard = new GridPane();
        	
        	whitePiecesMap = Map.of(
                "KING", whiteKing,
                "QUEEN", whiteQueen,
                "BISHOP", whiteBishop,
                "KNIGHT", whiteKnight,
                "ROOK", whiteRook,
                "PAWN", whitePawn
            );

            blackPiecesMap = Map.of(
                "KING", blackKing,
                "QUEEN", blackQueen,
                "BISHOP", blackBishop,
                "KNIGHT", blackKnight,
                "ROOK", blackRook,
                "PAWN", blackPawn
            );

            // Creating the squares on the Board
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int s = 0; s < BOARD_SIZE; s++) {
                    Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                    Color color;

                    if ((s + i) % 2 == 0) {
                        color = Color.LIGHTGRAY;
                    } else {
                        color = Color.DARKGRAY;
                    }
                    
                    tile.setFill(color);
                    visualBoard.add(tile, s, i);
                    
                    squares[i][s] = tile;
                }
            }
            
            // Adding the Pieces on the board as ImageViews
            ImageView imageView;
            Piece piece;
            pieceImagesOnBoard = new ArrayList<ImageView>();
            for (int i = 0; i < 8; i++) {
                for (int s = 0; s < 8; s++) {
                    piece = board.getPiecePosOnBoard()[i][s];
                    if (piece != null) {
                        Image pieceImage = piece.getColor() ? whitePiecesMap.get(piece.getPieceType()) : blackPiecesMap.get(piece.getPieceType());
                        if (pieceImage != null) {
                        	imageView = createPiece(pieceImage);
                            visualBoard.add(imageView, s, i);
                            pieceImagesOnBoard.add(imageView);
                        }
                    }
                }
            }
            visualBoard.setOnMouseClicked(event -> handleMouseClick(event));
            
            Scene scene = new Scene(visualBoard, TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Chessboard");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private ImageView createPiece(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        return imageView;
    }
    
    private void handleMouseClick(MouseEvent event) {
        int PosY = (int)(event.getX() / TILE_SIZE);
        int PosX = (int)(event.getY() / TILE_SIZE);

        repaintBoard();
       
        if (lastClickedSquare != null) {
            board.getLegalMovesOf(lastClickedSquarePosX, lastClickedSquarePosY);
            board.tryToMove(lastClickedSquarePosX, lastClickedSquarePosY, PosX, PosY);
            board.clearMovesInCache();
        }
        
        squares[PosX][PosY].setFill(Color.LIGHTBLUE);
        lastClickedSquarePosX = PosX;
        lastClickedSquarePosY = PosY;
        lastClickedSquare = squares[PosX][PosY];
        
        if(board.getPiecePosOnBoard()[PosX][PosY] != null)
        if(board.getPiecePosOnBoard()[PosX][PosY].getColor() == board.getTurnColor()) {
        	markPossibleMoves();
        }
        UpdatePiecePosition();
        
    }
    
    private void UpdatePiecePosition() {
    	
    	removeAllImagesFromBoard();
    	
    	Piece piece;
        for (int i = 0; i < 8; i++) {
            for (int s = 0; s < 8; s++) {
                piece = board.getPiecePosOnBoard()[i][s];
                if (piece != null) {
                    Image pieceImage = piece.getColor() ? whitePiecesMap.get(piece.getPieceType()) : blackPiecesMap.get(piece.getPieceType());
                    if (pieceImage != null) {
                        visualBoard.add(createPiece(pieceImage), s, i);
                    }
                }
            }
        }
    }
    
    private void removeAllImagesFromBoard() {
        ArrayList<Node> nodesToRemove = new ArrayList<>();

        for (Node node : visualBoard.getChildren()) {
            if (node instanceof ImageView) {
                nodesToRemove.add(node);
            }
        }

        visualBoard.getChildren().removeAll(nodesToRemove);
    }
    
    private void markPossibleMoves() {
    	if(lastClickedSquare != null) {
    		board.getLegalMovesOf(lastClickedSquarePosX, lastClickedSquarePosY);
        	ArrayList<String> squaresToColor = new ArrayList<String>();
        	
        	squaresToColor = board.getMoves();
        	
        	for (String square : squaresToColor) {
    			squares[Character.getNumericValue(square.charAt(0))][Character.getNumericValue(square.charAt(1))].setFill(Color.LIGHTCYAN);
    		}
    	} 	
    }
    
    private void repaintBoard() {
    	for (int i = 0; i < BOARD_SIZE; i++) {
            for (int s = 0; s < BOARD_SIZE; s++) {
            	
                if ((s + i) % 2 == 0) {
                	squares[i][s].setFill(Color.LIGHTGRAY);
                } else {
                	squares[i][s].setFill(Color.DARKGRAY);
                }
                
            }
        }
    }
}