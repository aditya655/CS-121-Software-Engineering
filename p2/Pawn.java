import java.util.*;

public class Pawn extends Piece {
    public Pawn(Color c) {
        super(c);
    }

    @Override
    public String toString() {
        return (color() == Color.WHITE ? "w" : "b") + "p";
    }

    @Override
    public List<String> moves(Board b, String loc) {
        List<String> legalMoves = new ArrayList<>();
        int row = 8 - Character.getNumericValue(loc.charAt(1));
        int col = loc.charAt(0) - 'a';

        int direction = (color() == Color.WHITE) ? -1 : 1;
        int startRow = (color() == Color.WHITE) ? 6 : 1;

        // Move forward one square
        if (row + direction >= 0 && row + direction < 8) {
            String newLoc = "" + (char) ('a' + col) + (8 - (row + direction));
            if (b.getPiece(newLoc) == null) {
                legalMoves.add(newLoc);
            }
        }

        // Move forward two squares from starting position
        if (row == startRow) {
            String newLoc = "" + (char) ('a' + col) + (8 - (row + direction));
            if (b.getPiece(newLoc) == null) {
                newLoc = "" + (char) ('a' + col) + (8 - (row + 2 * direction));
                if (b.getPiece(newLoc) == null) {
                    legalMoves.add(newLoc);
                }
            }
        }

        // Capture diagonally
        if (row + direction >= 0 && row + direction < 8) {
            if (col - 1 >= 0) {
                String newLoc = "" + (char) ('a' + col - 1) + (8 - (row + direction));
                Piece target = b.getPiece(newLoc);
                if (target != null && target.color() != this.color()) {
                    legalMoves.add(newLoc);
                }
            }
            if (col + 1 < 8) {
                String newLoc = "" + (char) ('a' + col + 1) + (8 - (row + direction));
                Piece target = b.getPiece(newLoc);
                if (target != null && target.color() != this.color()) {
                    legalMoves.add(newLoc);
                }
            }
        }

        return legalMoves;
    }
}
