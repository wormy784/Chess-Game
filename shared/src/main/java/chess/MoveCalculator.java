package chess;
import java.util.Collection;
import java.util.ArrayList;

public class MoveCalculator {
    private final ChessBoard board;
    private final ChessPiece piece;
    private final ChessPosition position;
    private int direction;
    private final int row;
    private final int col;
    private final int nextRow;

    public MoveCalculator(ChessBoard board, ChessPiece piece, ChessPosition position) {
        this.board = board;
        this.piece = piece;
        this.position = position;
        this.row = position.getRow();
        this.col = position.getColumn();

        // color direction initialization
        this.direction = 0;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            direction = 1;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            direction = -1;
        }
        this.nextRow = row + direction;
    }

    //create moves arraylist
    public Collection<ChessMove> calculateMoves() {
        Collection<ChessMove> moves = new ArrayList<>();
        switch (piece.getPieceType()) {
            case PAWN -> pawnMoves(moves);
            case ROOK -> rookMoves(moves);
            case BISHOP -> bishopMoves(moves);
            case QUEEN -> queenMoves(moves);
            case KING -> kingMoves(moves);
            case KNIGHT -> knightMoves(moves);
        }
        return moves;
    }
    // create pawn promotion moves
    private void promotionMoves(Collection<ChessMove> moves, ChessPosition destination) {
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.KNIGHT));
    }
    // check out of bounds
    private boolean outOfBounds(ChessPosition destination) {
        int row = destination.getRow();
        int col = destination.getColumn();
        return row < 1 || row > 8 || col < 1 || col > 8;
    }

    // piece move until stop, rook, bishop, queen
    private void moveUntilStop(Collection<ChessMove> moves, int specificRow, int specificCol) {
        int r = row + specificRow;
        int c = col + specificCol;
        while (true) {
            var newPosition = new ChessPosition(r, c);
            //stop if off board
            if (outOfBounds(newPosition)) {
                return;
            }
            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
            }
            if (board.getPiece(newPosition) != null) {
                if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
                return;
            }
            // if it is a king don't do the while loop
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                return;
            }
            //update so we don't just check the same square over and over
            r += specificRow;
            c += specificCol;
        }
    }
    // knight helper function
    private void knightHelper(Collection<ChessMove> moves, int specificRow, int specificCol) {
        int r = row + specificRow;
        int c = col + specificCol;
        var nextPosition = new ChessPosition(r, c);
        if (outOfBounds(nextPosition)) {
            return;
        }
        if (board.getPiece(nextPosition) == null) {
            moves.add(new ChessMove(position, nextPosition, null));
        }
        if (board.getPiece(nextPosition) != null) {
            if (board.getPiece(nextPosition).getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(position, nextPosition, null));
            }
        }
    }
    // pawn helper function
    private void pawnHelper(Collection<ChessMove> moves, int specificRow, int specificCol) {
        int r = row + specificRow;
        int c = col + specificCol;
        var nextPosition = new ChessPosition(r, c);
        if (outOfBounds(nextPosition)) {
            return;
        }
        if (board.getPiece(nextPosition) != null) {
            if (board.getPiece(nextPosition).getTeamColor() != piece.getTeamColor()) {
                if (nextRow == 1 || nextRow == 8) {
                    promotionMoves(moves, nextPosition);
                }
                else {
                    moves.add(new ChessMove(position, nextPosition, null));
                }
            }
        }
    }
    private void pawnMoves(Collection<ChessMove> moves) {
        // move forward
        var forward = new ChessPosition(row + direction, col);
        if (board.getPiece(forward) == null) {
            if (nextRow == 8 || nextRow == 1) {
                promotionMoves(moves, forward);
            } else {
                moves.add(new ChessMove(position, forward, null));
            }
        }
        // move forward 2 on first turn
        if (row == 2 && direction == 1) {
            var forwardTwo = new ChessPosition(4, col);
            if (board.getPiece(forward) == null && board.getPiece(forwardTwo) == null) {
                moves.add(new ChessMove(position, forwardTwo, null));
            }
        } else if (row == 7 && direction == -1) {
            var forwardTwo = new ChessPosition(5, col);
            if (board.getPiece(forward) == null && board.getPiece(forwardTwo) == null) {
                moves.add(new ChessMove(position, forwardTwo, null));
            }
        }
        // attacking diagonal
        pawnHelper(moves, direction, 1);
        pawnHelper(moves, direction, -1);

    }
    private void rookMoves(Collection<ChessMove> moves) {
        //up
        moveUntilStop(moves, 1,0);
        // down
        moveUntilStop(moves, -1, 0);
        // right
        moveUntilStop(moves, 0, 1);
        //left
        moveUntilStop(moves, 0, -1);
    }
    private void bishopMoves(Collection<ChessMove> moves) {
        // up right
        moveUntilStop(moves, 1,1);
        // up left
        moveUntilStop(moves, 1, -1);
        // down right
        moveUntilStop(moves, -1, 1);
        // down left
        moveUntilStop(moves, -1, -1);
    }
    private void queenMoves(Collection<ChessMove> moves) {
        rookMoves(moves);
        bishopMoves(moves);
    }
    private void kingMoves(Collection<ChessMove> moves) {
        queenMoves(moves);
    }
    private void knightMoves(Collection<ChessMove> moves) {
        // knight move forward 2 and then left or right
        knightHelper(moves, 1, 2);
        knightHelper(moves, 1, -2);
        knightHelper(moves, -1, 2);
        knightHelper(moves, -1, -2);
        knightHelper(moves, 2, 1);
        knightHelper(moves, 2, -1);
        knightHelper(moves, -2, 1);
        knightHelper(moves, -2, -1);
    }
}