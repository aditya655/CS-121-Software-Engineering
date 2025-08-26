import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Chess {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Chess layout moves");
            System.exit(1);
        }

        Piece.registerPiece(new KingFactory());
        Piece.registerPiece(new QueenFactory());
        Piece.registerPiece(new KnightFactory());
        Piece.registerPiece(new BishopFactory());
        Piece.registerPiece(new RookFactory());
        Piece.registerPiece(new PawnFactory());

        Board.theBoard().registerListener(new Logger());

        try {
            setupBoard(args[0]);
            executeMoves(args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Leave the following code at the end of the simulation:
        System.out.println("Final board:");
        Board.theBoard().iterate(new BoardPrinter());
    }

    private static void setupBoard(String layoutFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(layoutFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue; // Ignore comments
                }
                String[] parts = line.split("=");
                String position = parts[0];
                String pieceCode = parts[1];

                Piece piece = Piece.createPiece(pieceCode);
                Board.theBoard().addPiece(piece, position);
            }
        }
    }

    private static void executeMoves(String movesFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(movesFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue; // Ignore comments
                }
                String[] parts = line.split("-");
                String from = parts[0];
                String to = parts[1];

                Board.theBoard().movePiece(from, to);
            }
        }
    }
}
