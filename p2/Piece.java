import java.util.*;

abstract public class Piece {
    private Color color;
    private static Map<Character, PieceFactory> pieceFactories = new HashMap<>();

    public Piece(Color c) {
        this.color = c;
    }

    public static void registerPiece(PieceFactory pf) {
        pieceFactories.put(pf.symbol(), pf);
    }

    public static Piece createPiece(String name) {
        if (name.length() != 2) {
            throw new IllegalArgumentException("Invalid piece name: " + name);
        }
        char colorChar = name.charAt(0);
        char pieceChar = name.charAt(1);
        Color color;
        switch (colorChar) {
            case 'w':
                color = Color.WHITE;
                break;
            case 'b':
                color = Color.BLACK;
                break;
            default:
                throw new IllegalArgumentException("Invalid color: " + colorChar);
        }
        PieceFactory pf = pieceFactories.get(pieceChar);
        if (pf == null) {
            throw new IllegalArgumentException("Invalid piece type: " + pieceChar);
        }
        return pf.create(color);
    }

    public Color color() {
        return this.color;
    }

    abstract public String toString();

    abstract public List<String> moves(Board b, String loc);
}
