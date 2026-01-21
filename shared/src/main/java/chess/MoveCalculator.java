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

    private void knight_helper(Collection<ChessMove> moves, int specific_row, int specific_col) {
        var knight_move = new ChessPosition(specific_row, specific_col);
        if (out_of_bounds(knight_move)) {
            return;
        }
        if (board.getPiece(knight_move) == null) {
            moves.add(new ChessMove(position, knight_move, null));
        }
        if (board.getPiece(knight_move) != null && board.getPiece(knight_move).getTeamColor() != piece.getTeamColor()) {
            moves.add(new ChessMove(position, knight_move, null));
        }
    }

    private void pawn_helper(Collection<ChessMove> moves, int specific_row, int specific_col) {
            var pawn_move = new ChessPosition(specific_row, specific_col);
            if (out_of_bounds(pawn_move)) {
                return;
            }
            if (board.getPiece(pawn_move) != null && board.getPiece(pawn_move).getTeamColor() != piece.getTeamColor()) {
                if (nextRow == 8 || nextRow == 1) {
                    PromotionMoves(moves, pawn_move);
                } else {
                    moves.add(new ChessMove(position, pawn_move, null));
                }
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
        pawn_helper(moves, row + direction, col + 1);
        pawn_helper(moves, row + direction, col - 1);

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
        rookMoves(moves);
        bishopMoves(moves);
    }
    private void kingMoves(Collection<ChessMove> moves) {
       queenMoves(moves);
    }
    private void knightMoves(Collection<ChessMove> moves) {
        // knight move forward 2 and then left or right
        knight_helper(moves, row + 2, col + 1);
        knight_helper(moves, row + 2, col - 1);
        knight_helper(moves, row - 2, col + 1);
        knight_helper(moves, row - 2, col - 1);
        knight_helper(moves, row + 1, col + 2);
        knight_helper(moves, row + 1, col - 2);
        knight_helper(moves, row - 1, col + 2);
        knight_helper(moves, row - 1, col - 2);
    }
    }