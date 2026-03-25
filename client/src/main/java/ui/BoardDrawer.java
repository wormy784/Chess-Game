package ui;

public class BoardDrawer {


    private void getColor(int row) {
        if (row == 1 || row == 2) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        }
        if (row == 7 || row == 8) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        }
    }
    private String getPiece(int row, int col) {
        if (row == 1 && col == 1) {
            return EscapeSequences.WHITE_ROOK;
        }
        if (row == 1 && col == 2) {
            return EscapeSequences.WHITE_KNIGHT;
        }
        if (row == 1 && col == 3) {
            return EscapeSequences.WHITE_BISHOP;
        }
        if (row == 1 && col == 4) {
            return EscapeSequences.WHITE_QUEEN;
        }
        if (row == 1 && col == 5) {
            return EscapeSequences.WHITE_KING;
        }
        if (row == 1 && col == 6) {
            return EscapeSequences.WHITE_BISHOP;
        }
        if (row == 1 && col == 7) {
            return EscapeSequences.WHITE_KNIGHT;
        }
        if (row == 1 && col == 8) {
            return EscapeSequences.WHITE_ROOK;
        }
        // pawn
        if (row == 2) {
            if (col > 0 && col < 9) {
                return EscapeSequences.WHITE_PAWN;

            }
        }

        // black
        if (row == 8 && col == 1) {
            return EscapeSequences.BLACK_ROOK;
        }
        if (row == 8 && col == 2) {
            return EscapeSequences.BLACK_KNIGHT;
        }
        if (row == 8 && col == 3) {
            return EscapeSequences.BLACK_BISHOP;
        }
        if (row == 8 && col == 4) {
            return EscapeSequences.BLACK_QUEEN;
        }
        if (row == 8 && col == 5) {
            return EscapeSequences.BLACK_KING;
        }
        if (row == 8 && col == 6) {
            return EscapeSequences.BLACK_BISHOP;
        }
        if (row == 8 && col == 7) {
            return EscapeSequences.BLACK_KNIGHT;
        }
        if (row == 8 && col == 8) {
            return EscapeSequences.BLACK_ROOK;
        }
        // black pawn

        if (row == 7) {
            if (col > 0 && col < 9) {
                return EscapeSequences.BLACK_PAWN;
            }
        }

        return EscapeSequences.EMPTY;
    }

    private void drawRow(int i, int jBegin, int jEnd, int jInc) {
        System.out.print(i + " ");
        for (int j = jBegin; jInc > 0 ? j <= jEnd : j >= jEnd; j += jInc) {
            if ((i + j) % 2 == 0) {
                System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
            } else {
                System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
            }
            getColor(i);
            System.out.print(getPiece(i, j));
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        }
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(" "+ i);
        System.out.print("\n");
    }
    public void drawBoard(boolean whitePerspective) {
        if (whitePerspective) {
            System.out.print("   a  b  c  d  e  f  g  h  \n");
            for (int i = 8; i >= 1; i--) {
                drawRow(i, 1, 8 , 1);
            }
            System.out.print("   a  b  c  d  e  f  g  h  \n");

        } else {
            System.out.print("   h  g  f  e  d  c  b  a  \n");
            for (int i = 1; i <= 8; i++) {
                drawRow(i, 8, 1, -1);
            }
            System.out.print("   h  g  f  e  d  c  b  a  \n");
        }
    }
}