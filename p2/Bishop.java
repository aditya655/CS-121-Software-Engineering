import java.util.*;

public class Bishop extends Piece {
    public Bishop(Color c) {
        super(c);
    }

    @Override
    public String toString() {
        return (color() == Color.WHITE ? "w" : "b") + "b";
    }

    @Override
    public List<String> moves(Board b, String loc) {
        List<String> legalMoves = new ArrayList<>();
        int row = 8 - Character.getNumericValue(loc.charAt(1));
        int col = loc.charAt(0) - 'a';

        int[] dx = {-1, -1, 1, 1};
        int[] dy = {-1, 1, -1, 1};

        for (int i = 0; i < 4; i++) {
            int newRow = row + dx[i];
            int newCol = col + dy[i];
            while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                String newLoc = "" + (char) ('a' + newCol) + (8 - newRow);
                Piece target = b.getPiece(newLoc);
                if (target == null) {
                    legalMoves.add(newLoc);
                } else {
                    if (target.color() != this.color()) {
                        legalMoves.add(newLoc);
                    }
                    break;
                }
                newRow += dx[i];
                newCol += dy[i];
            }
        }

        return legalMoves;
    }
}
