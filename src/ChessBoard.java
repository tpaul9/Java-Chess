import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ChessBoard {
    JFrame gameFrame;
    ArrayList<JButton> tiles;
    public Integer selected = null;
    public Board board = null;
    public ArrayList<Move> moves = null;
    public Integer blackCheck = null;
    public Integer whiteCheck = null;
    public Integer blackCheckmate = null;
    public Integer whiteCheckmate = null;

    public ChessBoard(Board other) {
        this.board = other;
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setSize(600, 600);
        this.gameFrame.setLayout(new GridLayout(8, 8));
        this.tiles = new ArrayList<JButton>();
        for (int i = 0; i < 64; i++) {
            JButton tile = new JButton();
            tile.setBackground(((i / 8) % 2 + i % 2) % 2 == 0 ? Color.WHITE : Color.BLACK);
            ImageIcon icon = new ImageIcon(
                    new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
            tile.setIcon(icon);
            final int pos = i;
            tile.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Left Click
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        handleClick(pos);
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        selected = null;
                        moves = null;
                        renderBoard();
                    }
                }

                private void handleClick(int pos) {
                    System.out.println(pos);
                    if (selected == null && board.pieces.get(pos) != null) {
                        if (board.state.turn != board.pieces.get(pos).color) {
                            return;
                        }
                        selected = pos;
                        moves = BoardUtils.calculateMoves(board, pos, board.state.turn == Players.Black);
                        System.out.println(moves);
                        renderBoard();
                    } else if (board.pieces.get(selected) != null) {
                        Move move = null;
                        for (Move ele : moves) {
                            if (ele.to == pos) {
                                move = ele;
                            }
                        }
                        if (move != null) {
                            board = move.board;
                            moves = null;
                            selected = null;
                            blackCheck = null;
                            whiteCheck = null;
                            Integer kingPos = (board.state.turn == Players.White ? board.state.White_KingPos
                                    : board.state.Black_KingPos);
                            if (BoardUtils.isAttacked(board, kingPos,
                                    board.state.turn == Players.White ? Players.Black : Players.White)) {
                                boolean canMove = false;
                                for (Entry<Integer, Piece> ele : board.pieces.entrySet()) {
                                    Integer index = ele.getKey();
                                    Piece piece = ele.getValue();
                                    if (piece.color == board.state.turn
                                            && BoardUtils.calculateMoves(board, index, true).size() != 0) {
                                        canMove = true;
                                    }
                                }
                                if (!canMove && (board.state.turn == Players.White
                                        || board.state.turn == Players.Black)) {
                                    System.out.println(String.format("%s checkmates %s",
                                            board.state.turn == Players.White ? Players.Black : Players.White,
                                            board.state.turn == Players.White ? Players.White : Players.White));
                                    if (board.state.turn == Players.Black) {
                                        blackCheckmate = board.state.Black_KingPos;
                                    } else {
                                        whiteCheckmate = board.state.White_KingPos;
                                    }
                                } else {
                                    if (board.state.turn == Players.Black) {
                                        blackCheck = board.state.Black_KingPos;
                                    } else {
                                        whiteCheck = board.state.White_KingPos;
                                    }
                                    System.out.println(String.format("Check: %s to move", board.state.turn));
                                }
                            }

                            boolean canWin = false;
                            Piece piece1 = null;
                            Piece piece2 = null;
                            if (board.pieces.size() <= 4) {
                                for (Entry<Integer, Piece> ele : board.pieces.entrySet()) {
                                    Piece piece = ele.getValue();
                                    if (piece.type == PieceTypes.Queen || piece.type == PieceTypes.Queen
                                            || piece.type == PieceTypes.Rook) {
                                        canWin = true;
                                        break;
                                    } else if (piece.type == PieceTypes.Bishop || piece.type == PieceTypes.Knight) {
                                        if (piece1 == null) {
                                            piece1 = piece;
                                        } else {
                                            piece2 = piece;
                                        }
                                    }
                                }
                                if (piece1 != null && piece2 != null && piece1.color == piece2.color
                                        && (piece1.type == PieceTypes.Bishop || piece2.type == PieceTypes.Bishop)) {
                                    canWin = true;
                                }
                                if (!canWin) {
                                    board.state.turn = null;
                                    System.out.println("Draw by insufficient material.");
                                }
                            }
                            // AI logic
                            if (board.state.turn == Players.Black) {
                                int depth = 4;
                                if (board.pieces.size() < 10) {
                                    depth = 5;
                                }
                                if (board.pieces.size() < 5) {
                                    depth = 6;
                                }
                                Move blackMove = BoardUtils.minimax(board, depth, depth, board.state.turn == Players.White,
                                        Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).move;
                                if (blackMove != null) {
                                    //System.out.println(blackMove.toString());
                                }
                                if (blackMove != null) {
                                    handleClick(blackMove.from);
                                    handleClick(blackMove.to);
                                }
                            }
                        }
                    }
                    // System.out.println(board.toString());

                    renderBoard();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

            });
            this.tiles.add(tile);
            this.gameFrame.add(tile);
        }
        this.gameFrame.setVisible(true);
        renderBoard();
    }

    public void renderBoard() {
        for (int i = 0; i < 64; i++) {
            this.tiles.get(i).setBackground(((i / 8) % 2 + i % 2) % 2 == 0 ? Color.WHITE : Color.BLACK);
            Piece piece = board.pieces.get(i);
            ImageIcon icon;
            if (piece != null) {
                BufferedImage img = null;
                try {
                    img = ImageIO
                            .read(new File("images/" + piece.type.toString() + "_" + piece.color.toString() + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                icon = new ImageIcon(img);
            } else {
                icon = new ImageIcon(
                        new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
            }
            this.tiles.get(i).setIcon(icon);

        }
        if (selected != null) {
            this.tiles.get(selected).setBackground(Color.BLUE);
        }
        if(blackCheck!=null){
            this.tiles.get(board.state.Black_KingPos).setBackground(Color.RED);
        }
        if(whiteCheck!=null){
            this.tiles.get(board.state.White_KingPos).setBackground(Color.RED);
        }
        if(blackCheckmate!=null){
            this.tiles.get(board.state.Black_KingPos).setBackground(Color.RED);
        }
        if(whiteCheckmate!=null){
            this.tiles.get(board.state.White_KingPos).setBackground(Color.RED);
        }
        if (board.state.lastMove!=null){
            this.tiles.get(board.state.lastMove.from).setBackground(Color.ORANGE);
            this.tiles.get(board.state.lastMove.to).setBackground(Color.ORANGE);
        }
        if (moves != null) {
            for (Move move : moves) {
                this.tiles.get(move.to).setBackground(Color.YELLOW);
            }
        }
    }
    /*
     * ImageIcon icon;
     * BufferedImage img=null;
     * File directory = new File("./");
     * System.out.println(directory.getAbsolutePath());
     * try {
     * img = ImageIO.read(new File("images/rook_b.png"));
     * }catch (IOException e){
     * e.printStackTrace();
     * }
     * icon = new ImageIcon(img);
     * this.tiles.get(0).setIcon(icon);
     */
}