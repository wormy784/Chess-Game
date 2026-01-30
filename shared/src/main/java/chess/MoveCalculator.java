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
    private void promotion_moves(Collection<ChessMove> moves, ChessPosition destination) {
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.KNIGHT));
    }
    // check out of bounds
    private boolean out_of_bounds(ChessPosition destination) {
        int row = destination.getRow();
        int col = destination.getColumn();
        return row < 1 || row > 8 || col < 1 || col > 8;
    }

    // piece move until stop, rook, bishop, queen
    private void move_until_stop(Collection<ChessMove> moves, int specific_row, int specific_col) {
        int r = row + specific_row;
        int c = col + specific_col;
        while (true) {
            var new_position = new ChessPosition(r, c);
            //stop if off board
            if (out_of_bounds(new_position)) {
                return;
            }
            if (board.getPiece(new_position) == null) {
                moves.add(new ChessMove(position, new_position, null));
            }
            if (board.getPiece(new_position) != null) {
                if (board.getPiece(new_position).getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, new_position, null));
                }
                return;
            }
            // if it is a king don't do the while loop
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                return;
            }
            //update so we don't just check the same square over and over
            r += specific_row;
            c += specific_col;
        }
    }
    // knight helper function
    private void knight_helper(Collection<ChessMove> moves, int specific_row, int specific_col) {
        int r = row + specific_row;
        int c = col + specific_col;
        var next_position = new ChessPosition(r, c);
        if (out_of_bounds(next_position)) {
            return;
        }
        if (board.getPiece(next_position) == null) {
            moves.add(new ChessMove(position, next_position, null));
        }
        if (board.getPiece(next_position) != null) {
            if (board.getPiece(next_position).getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(position, next_position, null));
            }
        }
    }
    // pawn helper function
    private void pawn_helper(Collection<ChessMove> moves, int specific_row, int specific_col) {
        int r = row + specific_row;
        int c = col + specific_col;
        var next_position = new ChessPosition(r, c);
        if (out_of_bounds(next_position)) {
            return;
        }
        if (board.getPiece(next_position) != null) {
            if (board.getPiece(next_position).getTeamColor() != piece.getTeamColor()) {
                if (nextRow == 1 || nextRow == 8) {
                    promotion_moves(moves, next_position);
                }
                else {
                    moves.add(new ChessMove(position, next_position, null));
                }
            }
        }
    }
    private void pawnMoves(Collection<ChessMove> moves) {
        // move forward
        var forward = new ChessPosition(row + direction, col);
        if (board.getPiece(forward) == null) {
            if (nextRow == 8 || nextRow == 1) {
                promotion_moves(moves, forward);
            } else {
                moves.add(new ChessMove(position, forward, null));
            }
        }
        // move forward 2 on first turn
        if (row == 2 && direction == 1) {
            var forward_2 = new ChessPosition(4, col);
            if (board.getPiece(forward) == null && board.getPiece(forward_2) == null) {
                moves.add(new ChessMove(position, forward_2, null));
            }
        } else if (row == 7 && direction == -1) {
            var forward_2 = new ChessPosition(5, col);
            if (board.getPiece(forward) == null && board.getPiece(forward_2) == null) {
                moves.add(new ChessMove(position, forward_2, null));
            }
        }
        // attacking diagonal
        pawn_helper(moves, direction, 1);
        pawn_helper(moves, direction, -1);

    }
    private void rookMoves(Collection<ChessMove> moves) {
        //up
        move_until_stop(moves, 1,0);
        // down
        move_until_stop(moves, -1, 0);
        // right
        move_until_stop(moves, 0, 1);
        //left
        move_until_stop(moves, 0, -1);
    }
    private void bishopMoves(Collection<ChessMove> moves) {
        // up right
        move_until_stop(moves, 1,1);
        // up left
        move_until_stop(moves, 1, -1);
        // down right
        move_until_stop(moves, -1, 1);
        // down left
        move_until_stop(moves, -1, -1);
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
        knight_helper(moves, 1, 2);
        knight_helper(moves, 1, -2);
        knight_helper(moves, -1, 2);
        knight_helper(moves, -1, -2);
        knight_helper(moves, 2, 1);
        knight_helper(moves, 2, -1);
        knight_helper(moves, -2, 1);
        knight_helper(moves, -2, -1);
    }
    }