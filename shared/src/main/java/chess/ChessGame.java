package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        else {
            return piece.pieceMoves(board, startPosition);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("That's not a real move!");
        }
        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("That's not a real move!");

        }
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        //remove piece form start
        board.addPiece(start, null);
        //place piece at end pos
        board.addPiece(end, piece);
        //switch turns
        if (turn == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
        }
        else {
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        Collection<ChessMove> other_team_moves = new ArrayList<>();
        ChessPosition king_position = null;

        for (int current_row = 1; current_row <= 8; current_row++) {
            for (int current_col = 1; current_col <= 8; current_col++) {
                var current_position = new ChessPosition(current_row, current_col);
                var current_piece = board.getPiece(current_position);
                if (current_piece == null) {
                    continue;
                }
                // find the right king
                if (current_piece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (current_piece.getTeamColor() == teamColor) {
                        king_position = (new ChessPosition(current_row,current_col));
                    }
                }
                // get the other team moves
                if (current_piece.getTeamColor() != teamColor) {
                    other_team_moves.addAll(current_piece.pieceMoves(board, current_position));

                }
            }
        }
        for (ChessMove move : other_team_moves) {
            // see if == works or .equals()
            if (move.getEndPosition().equals(king_position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        //call reset board from ChessBoard to reset all positions to start
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

}
