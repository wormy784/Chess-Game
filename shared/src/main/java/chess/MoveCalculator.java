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
        // color direction initialization
        this.direction = 0;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            direction = 1;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            direction = -1;
        }
        this.row = position.getRow();
        this.col = position.getColumn();
        this.nextRow = row + direction;
    }


    //create moves arraylist
    public Collection<ChessMove> calculateMoves() {
        Collection<ChessMove> moves = new ArrayList<>();
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            pawnMoves(moves);
        }
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            rookMoves(moves);
        }
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            bishopMoves(moves);
        }
        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            queenMoves(moves);
        }
        return moves;
    }

    // create pawn promotion moves
    private void PromotionMoves(Collection<ChessMove> moves, ChessPosition destination) {
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(position, destination, ChessPiece.PieceType.KNIGHT));
    }
    // check out of bounds
    private boolean out_of_bounds(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
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
                    moves.add(new ChessMove( position, new_position, null));
                }
                return;
            }
            //update so we don't just check the same square over and over
            r += specific_row;
            c += specific_col;
        }
    }

    private void pawnMoves(Collection<ChessMove> moves) {
        // move forward
        var forward = new ChessPosition(row + direction, col);
        if (board.getPiece(forward) == null) {
            if (nextRow == 8 || nextRow == 1) {
                PromotionMoves(moves, forward);
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
        // check left
        var diag_left = new ChessPosition(row + direction, col -1);
        if (col > 1) {
            if (board.getPiece(diag_left) != null && board.getPiece(diag_left).getTeamColor() != piece.getTeamColor()) {
                if (nextRow == 8 || nextRow == 1) {
                    PromotionMoves(moves, diag_left);
                } else {
                    moves.add(new ChessMove(position, diag_left, null));
                }
            }
        }
        //check right
        var diag_right = new ChessPosition(row + direction, col +1);
        if (col < 8) {
            // if piece is opposite color
            if (board.getPiece(diag_right) != null && board.getPiece(diag_right).getTeamColor() != piece.getTeamColor()) {
                if (nextRow == 8 || nextRow == 1) {
                    PromotionMoves(moves, diag_right);
                } else {
                    moves.add(new ChessMove(position, diag_right, null));
                }
            }
        }
    }
    private void rookMoves(Collection<ChessMove> moves) {
        //up
        move_until_stop(moves, 1,0);
        // down
        move_until_stop(moves, -1, 0);
        // right
        move_until_stop(moves, 0, 1);
        //lef
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
        //up
        move_until_stop(moves, 1,0);
        // down
        move_until_stop(moves, -1, 0);
        // right
        move_until_stop(moves, 0, 1);
        //lef
        move_until_stop(moves, 0, -1);
        // up right
        move_until_stop(moves, 1,1);
        // up left
        move_until_stop(moves, 1, -1);
        // down right
        move_until_stop(moves, -1, 1);
        // down left
        move_until_stop(moves, -1, -1);
    }
}