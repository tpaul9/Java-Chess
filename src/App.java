public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        ChessBoard gui = new ChessBoard(new Board());
        gui.renderBoard();
    }
}
