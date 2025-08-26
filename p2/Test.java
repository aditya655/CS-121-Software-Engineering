import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Test {

    // Run "java -ea Test" to run with assertions enabled (If you run
    // with assertions disabled, the default, then assert statements
    // will not execute!)

    public static void test1() {
	Board b = Board.theBoard();
	Piece.registerPiece(new PawnFactory());
	Piece p = Piece.createPiece("bp");
	b.addPiece(p, "a3");
	assert b.getPiece("a3") == p;
    }

    
    // Test piece creation
    public static void testPieceCreation() {
        Piece.registerPiece(new KingFactory());
        Piece.registerPiece(new QueenFactory());
        Piece.registerPiece(new KnightFactory());
        Piece.registerPiece(new BishopFactory());
        Piece.registerPiece(new RookFactory());
        Piece.registerPiece(new PawnFactory());

        Piece p1 = Piece.createPiece("wk");
        assert p1 instanceof King;
        assert p1.color() == Color.WHITE;

        Piece p2 = Piece.createPiece("bq");
        assert p2 instanceof Queen;
        assert p2.color() == Color.BLACK;

        Piece p3 = Piece.createPiece("wn");
        assert p3 instanceof Knight;
        assert p3.color() == Color.WHITE;

        Piece p4 = Piece.createPiece("bb");
        assert p4 instanceof Bishop;
        assert p4.color() == Color.BLACK;

        Piece p5 = Piece.createPiece("wr");
        assert p5 instanceof Rook;
        assert p5.color() == Color.WHITE;

        Piece p6 = Piece.createPiece("bp");
        assert p6 instanceof Pawn;
        assert p6.color() == Color.BLACK;
    }

    // Test board setup
    public static void testBoardSetup() {
        Board b = Board.theBoard();
        b.clear();

        Piece.registerPiece(new PawnFactory());
        Piece p = Piece.createPiece("wp");
        b.addPiece(p, "a2");
        assert b.getPiece("a2") == p;

        try {
            b.addPiece(p, "a2");
            assert false; // Should throw exception
        } catch (IllegalArgumentException e) {
            assert true; // Expected
        }
    }

    // Test piece movement
    public static void testPieceMovement() {
        Board b = Board.theBoard();
        b.clear();

        Piece.registerPiece(new PawnFactory());
        Piece p = Piece.createPiece("wp");
        b.addPiece(p, "a2");
        assert b.getPiece("a2") == p;

        b.movePiece("a2", "a3");
        assert b.getPiece("a3") == p;
        assert b.getPiece("a2") == null;

        try {
            b.movePiece("a2", "a4");
            assert false; // Should throw exception
        } catch (IllegalArgumentException e) {
            assert true; // Expected
        }
    }

    // Test piece capture
    public static void testPieceCapture() {
        Board b = Board.theBoard();
        b.clear();

        Piece.registerPiece(new PawnFactory());
        Piece wp = Piece.createPiece("wp");
        Piece bp = Piece.createPiece("bp");

        b.addPiece(wp, "a2");
        b.addPiece(bp, "b3");

        b.movePiece("a2", "b3");
        assert b.getPiece("b3") == wp;
        assert b.getPiece("a2") == null;
    }

    // Test observer pattern
    public static void testObserverPattern() {
        Board b = Board.theBoard();
        b.clear();

        Logger logger = new Logger();
        b.registerListener(logger);

        Piece.registerPiece(new PawnFactory());
        Piece p = Piece.createPiece("wp");
        b.addPiece(p, "a2");

        // The following code should trigger the logger
        b.movePiece("a2", "a3");

        // Test capture logging
        Piece bp = Piece.createPiece("bp");
        b.addPiece(bp, "b4");
        b.movePiece("a3", "b4");  // Valid pawn capture

        b.removeListener(logger);

        // This move should not trigger the logger
        b.movePiece("b4", "b5");
    }

    // Test all pieces movement
    public static void testAllPiecesMovement() {
        Board b = Board.theBoard();
        b.clear();

        Piece.registerPiece(new KingFactory());
        Piece.registerPiece(new QueenFactory());
        Piece.registerPiece(new KnightFactory());
        Piece.registerPiece(new BishopFactory());
        Piece.registerPiece(new RookFactory());
        Piece.registerPiece(new PawnFactory());

        // King movement
        Piece king = Piece.createPiece("wk");
        b.addPiece(king, "e1");
        assert king.moves(b, "e1").contains("d1");
        assert king.moves(b, "e1").contains("f1");
        assert king.moves(b, "e1").contains("d2");
        assert king.moves(b, "e1").contains("e2");
        assert king.moves(b, "e1").contains("f2");

        // Queen movement
        Piece queen = Piece.createPiece("wq");
        b.addPiece(queen, "d4");  // Ensure clear path
        assert queen.moves(b, "d4").contains("d5");
        assert queen.moves(b, "d4").contains("d6");
        assert queen.moves(b, "d4").contains("d7");
        assert queen.moves(b, "d4").contains("e5");
        assert queen.moves(b, "d4").contains("c5");

        // Knight movement
        Piece knight = Piece.createPiece("wn");
        b.addPiece(knight, "b1");
        assert knight.moves(b, "b1").contains("a3");
        assert knight.moves(b, "b1").contains("c3");

        // Bishop movement
        Piece bishop = Piece.createPiece("wb");
        b.addPiece(bishop, "c1");
        assert bishop.moves(b, "c1").contains("d2");
        assert bishop.moves(b, "c1").contains("e3");

        // Rook movement
        Piece rook = Piece.createPiece("wr");
        b.addPiece(rook, "a1");
        assert rook.moves(b, "a1").contains("a2");
        assert rook.moves(b, "a1").contains("a3");

        // Pawn movement
        Piece pawn = Piece.createPiece("wp");
        b.addPiece(pawn, "a2");
        assert pawn.moves(b, "a2").contains("a3");
        assert pawn.moves(b, "a2").contains("a4");
    }

    // Test all pieces capture
    public static void testAllPieceCapture() {
        Board b = Board.theBoard();
        b.clear();

        Piece.registerPiece(new KingFactory());
        Piece.registerPiece(new QueenFactory());
        Piece.registerPiece(new KnightFactory());
        Piece.registerPiece(new BishopFactory());
        Piece.registerPiece(new RookFactory());
        Piece.registerPiece(new PawnFactory());

        Logger logger = new Logger();
        b.registerListener(logger);

        // King capture
        Piece king = Piece.createPiece("wk");
        Piece bp = Piece.createPiece("bp");
        b.addPiece(king, "e1");
        b.addPiece(bp, "d2");
        b.movePiece("e1", "d2");
        assert b.getPiece("d2") == king;

        // Queen capture
        Piece queen = Piece.createPiece("wq");
        bp = Piece.createPiece("bp");
        b.addPiece(queen, "d1");
        b.addPiece(bp, "d3"); // Ensure clear path for capture
        b.movePiece("d1", "d3");
        assert b.getPiece("d3") == queen;

        // Knight capture
        Piece knight = Piece.createPiece("wn");
        bp = Piece.createPiece("bp");
        b.addPiece(knight, "b1");
        b.addPiece(bp, "a3");
        b.movePiece("b1", "a3");
        assert b.getPiece("a3") == knight;

        // Bishop capture
        Piece bishop = Piece.createPiece("wb");
        bp = Piece.createPiece("bp");
        b.addPiece(bishop, "c1");
        b.addPiece(bp, "d2");
        b.movePiece("c1", "d2");
        assert b.getPiece("d2") == bishop;

        // Rook capture
        Piece rook = Piece.createPiece("wr");
        bp = Piece.createPiece("bp");
        b.addPiece(rook, "a1");
        b.addPiece(bp, "a3");
        b.movePiece("a1", "a3");
        assert b.getPiece("a3") == rook;

        // Pawn capture
        Piece pawn = Piece.createPiece("wp");
        Piece bn = Piece.createPiece("bn");
        b.addPiece(pawn, "a2");
        b.addPiece(bn, "b3");
        b.movePiece("a2", "b3");
        assert b.getPiece("b3") == pawn;
    }
    // Test all board setup
    public static void testAllBoardSetup() {
        Board b = Board.theBoard();
        b.clear();

        Piece.registerPiece(new KingFactory());
        Piece.registerPiece(new QueenFactory());
        Piece.registerPiece(new KnightFactory());
        Piece.registerPiece(new BishopFactory());
        Piece.registerPiece(new RookFactory());
        Piece.registerPiece(new PawnFactory());

        Piece king = Piece.createPiece("wk");
        Piece queen = Piece.createPiece("wq");
        Piece knight = Piece.createPiece("wn");
        Piece bishop = Piece.createPiece("wb");
        Piece rook = Piece.createPiece("wr");
        Piece pawn = Piece.createPiece("wp");

        b.addPiece(king, "e1");
        b.addPiece(queen, "d1");
        b.addPiece(knight, "b1");
        b.addPiece(bishop, "c1");
        b.addPiece(rook, "a1");
        b.addPiece(pawn, "a2");

        assert b.getPiece("e1") == king;
        assert b.getPiece("d1") == queen;
        assert b.getPiece("b1") == knight;
        assert b.getPiece("c1") == bishop;
        assert b.getPiece("a1") == rook;
        assert b.getPiece("a2") == pawn;
    }

    // Test based on layout1 and moves1 files
    public static void testFromFiles() {
        Board b = Board.theBoard();
        b.clear();

        // Register pieces
        Piece.registerPiece(new KingFactory());
        Piece.registerPiece(new QueenFactory());
        Piece.registerPiece(new KnightFactory());
        Piece.registerPiece(new BishopFactory());
        Piece.registerPiece(new RookFactory());
        Piece.registerPiece(new PawnFactory());

        // Setup board from layout1
        try (BufferedReader reader = new BufferedReader(new FileReader("layout1"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue; // Skip comments and empty lines
                }
                String[] parts = line.split("=");
                String loc = parts[0];
                String pieceCode = parts[1];
                Piece p = Piece.createPiece(pieceCode);
                b.addPiece(p, loc);
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false; // Fail the test if there is an error reading the file
        }

        // Execute moves from moves1
        try (BufferedReader reader = new BufferedReader(new FileReader("moves1"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue; // Skip comments and empty lines
                }
                String[] parts = line.split("-");
                String from = parts[0];
                String to = parts[1];
                b.movePiece(from, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false; // Fail the test if there is an error reading the file
        }
    }
    
    public static void main(String[] args) {
        testPieceCreation();
        testBoardSetup();
        testPieceMovement();
        testPieceCapture();
        testObserverPattern();
        testFromFiles();
        test1();
        testAllBoardSetup();
        testAllPieceCapture();
        testAllPiecesMovement();
        System.out.println("All tests passed!");
	     
    }

}