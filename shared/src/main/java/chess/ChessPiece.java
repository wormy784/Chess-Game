package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //create moves collection to return later
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        // pawn move set
        if (getPieceType() == PieceType.PAWN) {
            int direction = pawn_direction();
            int row = myPosition.getRow();
            int column = myPosition.getColumn();
            int nextRow = row + direction;

            // MOVE 1: create destination square one space ahead, if it is empty, the pawn can move there
            ChessPosition destination_square = new ChessPosition(nextRow, column);
//          // ask if there is a piece on that square
            if (board.getPiece(destination_square) == null) {
                // if no square, the make a chess move
                ChessMove pawn_move_one_forward = new ChessMove(myPosition, destination_square, null);
                // add the move
                moves.add(pawn_move_one_forward);
            }

            // MOVE 2: if it is pawns first move and two spaces ahead are clear it can move there
            // if white
            if (direction == 1) {
                if (myPosition.getRow() == 2) {
                    ChessPosition destination_square2 = new ChessPosition(4,column);
                    if (board.getPiece(destination_square2) == null && board.getPiece(destination_square) == null) {
                        ChessMove pawn_move_two_forward = new ChessMove(myPosition, destination_square2, null);
                        moves.add(pawn_move_two_forward);
                    }
                }
            }
            // if black
            if (direction == -1) {
                if (myPosition.getRow() == 7) {
                    ChessPosition destination_square2 = new ChessPosition(5,column);
                    if (board.getPiece(destination_square2) == null && board.getPiece(destination_square) == null) {
                        ChessMove pawn_move_two_forward = new ChessMove(myPosition, destination_square2, null);
                        moves.add(pawn_move_two_forward);
                    }
                }
            }

        }

        return moves;

    }

    //pawn direction helper function
    private int pawn_direction() {
        int direction = 0;
        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            direction = 1; }
        if (getTeamColor() == ChessGame.TeamColor.BLACK) {
            direction = -1; }
        return direction;
    }
}
