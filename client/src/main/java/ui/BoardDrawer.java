package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardDrawer {

    private String getPiece(ChessBoard board, int row, int col) {
       var piece = board.getPiece(new ChessPosition(row, col));
       if (piece == null)
           return EscapeSequences.EMPTY;
       return switch (piece.getPieceType()) {
           case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                   EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
           case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                   EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
           case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                   EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
           case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                   EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
           case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                   EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
           case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                   EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
       };
    }

    private void drawRow(ChessBoard board, int i, int jBegin, int jEnd, int jInc) {
        System.out.print(i + " ");
        for (int j = jBegin; jInc > 0 ? j <= jEnd : j >= jEnd; j += jInc) {
            if ((i + j) % 2 == 0) {
                System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
            } else {
                System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
            }
            System.out.print(getPiece(board, i, j));
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        }
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(" "+ i);
        System.out.print("\n");
    }
    public void drawBoard(ChessBoard board, boolean whitePerspective) {
        if (whitePerspective) {
            System.out.print("   a  b  c  d  e  f  g  h  \n");
            for (int i = 8; i >= 1; i--) {
                drawRow(board, i, 1, 8 , 1);
            }
            System.out.print("   a  b  c  d  e  f  g  h  \n");

        } else {
            System.out.print("   h  g  f  e  d  c  b  a  \n");
            for (int i = 1; i <= 8; i++) {
                drawRow(board, i, 8, 1, -1);
            }
            System.out.print("   h  g  f  e  d  c  b  a  \n");
        }
    }
}