package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
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
        // grabs moves collection from pieceMoves that we already made that has all the correct moves
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        // new array of valid moves for the piece we are looking at, at the start position
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : moves) {
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            // piece we want to move to
            var target = board.getPiece(end);

            // we add the move as a test and see if it works out
            //remove piece form start
            board.addPiece(start, null);
            //place piece at end pos
            board.addPiece(end, piece);

            // add move only if not put king in check
            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
            // always undo the move
            board.addPiece(start, piece);
            board.addPiece(end, target);

        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var piece = board.getPiece(move.getStartPosition());
        var target = board.getPiece(move.getEndPosition());
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        // 1) throw if no piece
        if (piece == null) {
            throw new InvalidMoveException("There's no piece there!");
        }
        // 2) throw if other team
        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("That's the wrong team!");

        }
        // 3) check if move is legal move for that piece
        // we get moves from the valid moves collection
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if (moves == null || !moves.contains(move)) {
            throw new InvalidMoveException("That's not a real move!");
        }

        // 4) cant take own piece
        if (target != null) {
            if (piece.getTeamColor() == target.getTeamColor()) {
                throw new InvalidMoveException("You can't take your own piece.");
            }
        }
        //remove piece form start
        board.addPiece(start, null);
        //place piece at end pos
        board.addPiece(end, piece);
        // 5) if piece is a pawn and on right squares, it can promote
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if ((end.getRow() == 1 || end.getRow() == 8)) {
                ChessPiece.PieceType promotion = move.getPromotionPiece();
                ChessPiece promoted_piece = new ChessPiece(piece.getTeamColor(), promotion);
                board.addPiece(end, promoted_piece);
            }
        }
        // 6) cant move if puts king in check
        if (isInCheck(turn)) {
            board.addPiece(start, piece);
            board.addPiece(end, target);
            throw new InvalidMoveException("That puts the king in check!");
        }

        // 7) switch turns
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
        // loop through all positions and get the moves of other pieces and see if it puts your king in check
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
            // .equals works and == doesn't here so take note, something to do with memory addresses maybe?
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
            // loop through all pieces, check moves of your team's pieces to get out of the check
            for (int current_row = 1; current_row <= 8; current_row++) {
                for (int current_col = 1; current_col <= 8; current_col++) {
                    var current_position = new ChessPosition(current_row, current_col);
                    var current_piece = board.getPiece(current_position);

                    if (current_piece == null) {
                        continue;
                    }
                    if (current_piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> moves = current_piece.pieceMoves(board, current_position);
                        for (ChessMove move : moves) {
                            var start = move.getStartPosition();
                            var end = move.getEndPosition();
                            var captured = board.getPiece(end);
                            board.addPiece(end, current_piece);
                            board.addPiece(start, null);

                            if (!isInCheck(teamColor)) {
                                return false;
                            }

                            board.addPiece(start, current_piece);
                            board.addPiece(end, captured);
                        }
                    }
                }
            }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        if (isInCheck(teamColor)) {
            return false;
        }
        // loop through all pieces on your team
        for (int current_row = 1; current_row <= 8; current_row++) {
            for (int current_col = 1; current_col <= 8; current_col++) {
                var current_position = new ChessPosition(current_row, current_col);
                var current_piece = board.getPiece(current_position);

                if (current_piece == null) {
                    continue;
                }
                // get moves of your team
                if (current_piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = current_piece.pieceMoves(board, current_position);
                    for (ChessMove move : moves) {
                        var start = move.getStartPosition();
                        var end = move.getEndPosition();
                        var captured = board.getPiece(end);
                        board.addPiece(end, current_piece);
                        board.addPiece(start, null);

                        if (!isInCheck(teamColor)) {
                            return false;
                        }

                        board.addPiece(start, current_piece);
                        board.addPiece(end, captured);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        //call reset board from ChessBoard to reset all positions to start
        this.board = board;
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
