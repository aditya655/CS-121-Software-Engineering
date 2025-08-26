import java.util.ArrayList;
import java.util.List;

public class Board {
    private Piece[][] pieces = new Piece[8][8];
    private List<BoardListener> listeners = new ArrayList<>();
    private static Board instance;

    private Board() {
        // Initialize the board with null values
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j] = null;
            }
        }
    }

    public static Board theBoard() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }

    // Returns piece at given loc or null if no such piece exists
    public Piece getPiece(String loc) {
        int row = 8 - Character.getNumericValue(loc.charAt(1));
        int col = loc.charAt(0) - 'a';
        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            throw new IllegalArgumentException("Invalid board location: " + loc);
        }
        return pieces[row][col];
    }

    public void addPiece(Piece p, String loc) {
        int row = 8 - Character.getNumericValue(loc.charAt(1));
        int col = loc.charAt(0) - 'a';
        if (pieces[row][col] != null) {
            throw new IllegalArgumentException("Location already occupied: " + loc);
        }
        pieces[row][col] = p;
    }

    public void movePiece(String from, String to) {
        Piece piece = getPiece(from);
        if (piece == null) {
            throw new IllegalArgumentException("No piece at location: " + from);
        }

        List<String> validMoves = piece.moves(this, from);
        if (!validMoves.contains(to)) {
            throw new IllegalArgumentException("Invalid move from " + from + " to " + to);
        }

        int fromRow = 8 - Character.getNumericValue(from.charAt(1));
        int fromCol = from.charAt(0) - 'a';
        int toRow = 8 - Character.getNumericValue(to.charAt(1));
        int toCol = to.charAt(0) - 'a';

        Piece captured = pieces[toRow][toCol];
        pieces[toRow][toCol] = piece;
        pieces[fromRow][fromCol] = null;

        for (BoardListener listener : listeners) {
            listener.onMove(from, to, piece);
            if (captured != null) {
                listener.onCapture(piece, captured);
            }
        }
    }

    public void clear() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j] = null;
            }
        }
    }

    public void registerListener(BoardListener bl) {
        listeners.add(bl);
    }

    public void removeListener(BoardListener bl) {
        listeners.remove(bl);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public void iterate(BoardInternalIterator bi) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String loc = "" + (char) ('a' + col) + (8 - row);
                bi.visit(loc, pieces[row][col]);
            }
        }
    }
}
