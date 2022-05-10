import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class BoardUtils {
    public static int boardRows = 8;
    public static int boardCols = 8;

    public static Integer[] numToCoord(Integer pos) {
        Integer[] coord = new Integer[2];
        coord[0] = pos / boardCols;
        coord[1] = pos % boardCols;
        return coord;
    }

    public static Integer coordToNum(int x, int y) {
        if (x >= 0 && x < boardRows && y >= 0 && y < boardCols) {
            return boardCols * x + y;
        } else {
            return null;
        }
    }

    public static double evaluateBoard(Board board) {
        // turn of the player who will take over after the proposed move.
        double total = 0;
        for (Entry<Integer, Piece> ele : board.pieces.entrySet()) {
            Piece piece = ele.getValue();
            int dir = (piece.color == Players.White ? 1 : -1);
            if (piece.type == PieceTypes.Pawn) {
                total += 1 * dir;
            } else if (piece.type == PieceTypes.Bishop || piece.type == PieceTypes.Knight) {
                total += 3 * dir;
            } else if (piece.type == PieceTypes.Rook) {
                total += 5 * dir;
            } else if (piece.type == PieceTypes.Queen) {
                total += 9 * dir;
            } else {
                total += 1000 * dir;
            }
        }
        total += Math.random() / 10;
        // Penalize repetition
        Board curr = board.state.lastMove != null ? board.state.lastMove.prevBoard : null;
        while (curr != null) {
            if (curr.pieces.size() != board.pieces.size()) {
                break;
            }
            if (board.pieces.equals(curr.pieces)) {
                total += (board.state.turn == Players.White ? -1 : 1);
            }
            curr = curr.state.lastMove != null ? curr.state.lastMove.prevBoard : null;
        }
        return total;
    }

    public static ScoreMove minimax(Board board, int depth, int maxDepth, boolean isMaximizingPlayer, Double alpha,
            Double beta) {
        if (depth == 0) {
            return new ScoreMove(evaluateBoard(board), null);
        }

        if (isMaximizingPlayer) {
            double bestVal = Double.NEGATIVE_INFINITY;
            Move bestMove = null;
            boolean foundMove = false;
            for (Entry<Integer, Piece> ele : board.pieces.entrySet()) {
                ArrayList<Move> moves = calculateMoves(board, ele.getKey(), true);
                for (Move move : moves) {
                    foundMove = true;
                    double value = minimax(move.board, depth - 1, maxDepth, false, alpha, beta).score;
                    if (value > bestVal) {
                        bestVal = value;
                        bestMove = move;
                    }
                    alpha = Math.max(alpha, bestVal);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            if (depth == maxDepth) {
                /*
                System.out.println(bestVal);
                System.out.println(bestMove.toString());
                */
                return new ScoreMove(bestVal, bestMove);
            }
            if (!foundMove) {
                double score = isAttacked(board,
                        board.state.turn == Players.White ? board.state.White_KingPos : board.state.Black_KingPos,
                        board.state.turn == Players.White ? Players.Black : Players.White)
                                ? (board.state.turn == Players.White ? -1 * (10000 + depth) : 10000 + depth)
                                : 0;
                return new ScoreMove(score, null);
            }
            return new ScoreMove(bestVal, null);
        } else {
            double bestVal = Double.POSITIVE_INFINITY;
            Move bestMove = null;
            boolean foundMove = false;
            for (Entry<Integer, Piece> ele : board.pieces.entrySet()) {
                ArrayList<Move> moves = calculateMoves(board, ele.getKey(), true);
                for (Move move : moves) {
                    foundMove = true;
                    double value = minimax(move.board, depth - 1, maxDepth, true, alpha, beta).score;
                    if (value < bestVal) {
                        bestVal = value;
                        bestMove = move;
                    }
                    beta = Math.min(beta, bestVal);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            if (depth == maxDepth) {
                /*
                System.out.println(bestVal);
                System.out.println(bestMove.toString());
                */
                return new ScoreMove(bestVal, bestMove);
            }
            if (!foundMove) {
                double score = isAttacked(board,
                        board.state.turn == Players.White ? board.state.White_KingPos : board.state.Black_KingPos,
                        board.state.turn == Players.White ? Players.Black : Players.White)
                                ? (board.state.turn == Players.White ? -1 * (10000 + depth) : 10000 + depth)
                                : 0;
                return new ScoreMove(score, null);
            }
            return new ScoreMove(bestVal, null);
        }
    }

    public static boolean isAttacked(Board board, int index, Players color) {
        Integer[] coord = numToCoord(index);
        Integer x = coord[0];
        Integer y = coord[1];

        // Knight moves
        for (int xMagnitude : new int[] { 1, 2 }) {
            for (int xDirection : new int[] { -1, 1 }) {
                for (int yDirection : new int[] { -1, 1 }) {
                    Integer pos = coordToNum(x + xMagnitude * xDirection, y + (xMagnitude == 1 ? 2 : 1) * yDirection);
                    Piece piece = board.pieces.get(pos);
                    if (piece != null && piece.type == PieceTypes.Knight && piece.color == color) {
                        return true;
                    }
                }
            }
        }

        // Rook moves
        for (int dir : new int[] { -1, 1 }) {
            for (int upDown : new int[] { 1, 0 }) {
                for (int offset = 1; offset < boardRows; offset++) {
                    Integer pos = coordToNum(x + offset * dir * upDown, y + offset * dir * (upDown - 1));
                    if (pos == null) {
                        break;
                    } else if (board.pieces.get(pos) != null) {
                        Piece piece = board.pieces.get(pos);
                        if ((piece.type == PieceTypes.Rook || piece.type == PieceTypes.Queen) && piece.color == color) {
                            return true;
                        }
                        break;
                    }
                }
            }
        }

        // Bishop moves
        for (int xDir : new int[] { -1, 1 }) {
            for (int yDir : new int[] { -1, 1 }) {
                for (int offset = 1; offset < boardRows; offset++) {
                    Integer pos = coordToNum(x + offset * xDir, y + offset * yDir);
                    if (pos == null) {
                        break;
                    } else if (board.pieces.get(pos) != null) {
                        Piece piece = board.pieces.get(pos);
                        if ((piece.type == PieceTypes.Bishop || piece.type == PieceTypes.Queen)
                                && piece.color == color) {
                            return true;
                        }
                        break;
                    }
                }
            }
        }

        // Pawn moves
        int direction = color == Players.White ? 1 : -1;
        for (int yOffset : new int[] { -1, 1 }) {
            Integer pos = coordToNum(x + direction, y + yOffset);
            Piece piece = board.pieces.get(pos);
            if (piece != null && piece.type == PieceTypes.Pawn && piece.color == color) {
                return true;
            }
        }

        // King moves
        for (int xOffset : new int[] { -1, 0, 1 }) {
            for (int yOffset : new int[] { -1, 0, 1 }) {
                if (xOffset != 0 || yOffset != 0) {
                    Integer pos = coordToNum(x + xOffset, y + yOffset);
                    Piece piece = board.pieces.get(pos);
                    if (piece != null && piece.type == PieceTypes.King && piece.color == color) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static ArrayList<Move> calculateMoves(Board board, Integer fromPos, boolean autoPromote) {
        ArrayList<Move> moves = new ArrayList<Move>();

        Integer[] coord = numToCoord(fromPos);
        Integer x = coord[0];
        Integer y = coord[1];

        Piece movingPiece = board.pieces.get(fromPos);
        if (movingPiece != null && board.state.turn == movingPiece.color) {
            if (movingPiece.type == PieceTypes.Knight) {
                for (int xMagnitude : new int[] { 1, 2 }) {
                    for (int xDirection : new int[] { -1, 1 }) {
                        for (int yDirection : new int[] { -1, 1 }) {
                            Integer pos = coordToNum(x + xMagnitude * xDirection,
                                    y + (xMagnitude == 1 ? 2 : 1) * yDirection);
                            if (pos != null) {
                                Piece piece = board.pieces.get(pos);
                                if (piece == null || piece.color != movingPiece.color) {
                                    HashMap<Integer, Piece> newPieces = new HashMap<Integer, Piece>(board.pieces);
                                    newPieces.remove(fromPos);
                                    newPieces.put(pos, movingPiece);
                                    BoardState newState = new BoardState(board.state);
                                    newState.turn = board.state.turn == Players.White ? Players.Black : Players.White;
                                    Board newBoard = new Board(newPieces, newState);
                                    Move newMove = new Move(MoveTypes.Normal, fromPos, pos, board, newBoard);
                                    newBoard.state.lastMove = newMove;
                                    if (movingPiece.color == Players.White) {
                                        if (!isAttacked(newBoard, newBoard.state.White_KingPos, Players.Black)) {
                                            moves.add(newMove);
                                        }
                                    } else {
                                        if (!isAttacked(newBoard, newBoard.state.Black_KingPos, Players.White)) {
                                            moves.add(newMove);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (movingPiece.type == PieceTypes.Rook) {
                for (int dir : new int[] { -1, 1 }) {
                    for (int upDown : new int[] { 1, 0 }) {
                        for (int offset = 1; offset < boardRows; offset++) {
                            Integer pos = coordToNum(x + offset * dir * upDown, y + offset * dir * (upDown - 1));
                            if (pos != null) {
                                Piece piece = board.pieces.get(pos);
                                if (piece == null || piece.color != movingPiece.color) {
                                    HashMap<Integer, Piece> newPieces = new HashMap<Integer, Piece>(board.pieces);
                                    newPieces.remove(fromPos);
                                    newPieces.put(pos, movingPiece);
                                    BoardState newState = new BoardState(board.state);
                                    newState.turn = board.state.turn == Players.White ? Players.Black : Players.White;
                                    // 0,7,56,63 Only need to change this state variable when a new rook moves into
                                    // a place a rook starts. If this ever happens, we know castling is impossible
                                    // from that position.
                                    if (pos == 0) {
                                        newState.Black_LeftRookMoved = true;
                                    } else if (pos == 7) {
                                        newState.Black_RightRookMoved = true;
                                    } else if (pos == 56) {
                                        newState.White_LeftRookMoved = true;
                                    } else if (pos == 63) {
                                        newState.White_RightRookMoved = true;
                                    }
                                    Board newBoard = new Board(newPieces, newState);
                                    Move newMove = new Move(MoveTypes.Normal, fromPos, pos, board, newBoard);
                                    newBoard.state.lastMove = newMove;
                                    if (movingPiece.color == Players.White) {
                                        if (!isAttacked(newBoard, newBoard.state.White_KingPos, Players.Black)) {
                                            moves.add(newMove);
                                        }
                                    } else {
                                        if (!isAttacked(newBoard, newBoard.state.Black_KingPos, Players.White)) {
                                            moves.add(newMove);
                                        }
                                    }
                                }
                                if (piece != null) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            } else if (movingPiece.type == PieceTypes.Bishop) {
                for (int xDir : new int[] { -1, 1 }) {
                    for (int yDir : new int[] { -1, 1 }) {
                        for (int offset = 1; offset < boardRows; offset++) {
                            Integer pos = coordToNum(x + offset * xDir, y + offset * yDir);
                            if (pos != null) {
                                Piece piece = board.pieces.get(pos);
                                if (piece == null || piece.color != movingPiece.color) {
                                    HashMap<Integer, Piece> newPieces = new HashMap<Integer, Piece>(board.pieces);
                                    newPieces.remove(fromPos);
                                    newPieces.put(pos, movingPiece);
                                    BoardState newState = new BoardState(board.state);
                                    newState.turn = board.state.turn == Players.White ? Players.Black : Players.White;
                                    Board newBoard = new Board(newPieces, newState);
                                    Move newMove = new Move(MoveTypes.Normal, fromPos, pos, board, newBoard);
                                    newBoard.state.lastMove = newMove;
                                    if (movingPiece.color == Players.White) {
                                        if (!isAttacked(newBoard, newBoard.state.White_KingPos, Players.Black)) {
                                            moves.add(newMove);
                                        }
                                    } else {
                                        if (!isAttacked(newBoard, newBoard.state.Black_KingPos, Players.White)) {
                                            moves.add(newMove);
                                        }
                                    }
                                    if (piece != null) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (movingPiece.type == PieceTypes.Queen) {
                for (int dir : new int[] { -1, 1 }) {
                    for (int upDown : new int[] { 1, 0 }) {
                        for (int offset = 1; offset < boardRows; offset++) {
                            Integer pos = coordToNum(x + offset * dir * upDown, y + offset * dir * (upDown - 1));
                            if (pos != null) {
                                Piece piece = board.pieces.get(pos);
                                if (piece == null || piece.color != movingPiece.color) {
                                    HashMap<Integer, Piece> newPieces = new HashMap<Integer, Piece>(board.pieces);
                                    newPieces.remove(fromPos);
                                    newPieces.put(pos, movingPiece);
                                    BoardState newState = new BoardState(board.state);
                                    newState.turn = board.state.turn == Players.White ? Players.Black : Players.White;
                                    Board newBoard = new Board(newPieces, newState);
                                    Move newMove = new Move(MoveTypes.Normal, fromPos, pos, board, newBoard);
                                    newBoard.state.lastMove = newMove;
                                    if (movingPiece.color == Players.White) {
                                        if (!isAttacked(newBoard, newBoard.state.White_KingPos, Players.Black)) {
                                            moves.add(newMove);
                                        }
                                    } else {
                                        if (!isAttacked(newBoard, newBoard.state.Black_KingPos, Players.White)) {
                                            moves.add(newMove);
                                        }
                                    }
                                    if (piece != null) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
                for (int xDir : new int[] { -1, 1 }) {
                    for (int yDir : new int[] { -1, 1 }) {
                        for (int offset = 1; offset < boardRows; offset++) {
                            Integer pos = coordToNum(x + offset * xDir, y + offset * yDir);
                            if (pos != null) {
                                Piece piece = board.pieces.get(pos);
                                if (piece == null || piece.color != movingPiece.color) {
                                    HashMap<Integer, Piece> newPieces = new HashMap<Integer, Piece>(board.pieces);
                                    newPieces.remove(fromPos);
                                    newPieces.put(pos, movingPiece);
                                    BoardState newState = new BoardState(board.state);
                                    newState.turn = board.state.turn == Players.White ? Players.Black : Players.White;
                                    Board newBoard = new Board(newPieces, newState);
                                    Move newMove = new Move(MoveTypes.Normal, fromPos, pos, board, newBoard);
                                    newBoard.state.lastMove = newMove;
                                    if (movingPiece.color == Players.White) {
                                        if (!isAttacked(newBoard, newBoard.state.White_KingPos, Players.Black)) {
                                            moves.add(newMove);
                                        }
                                    } else {
                                        if (!isAttacked(newBoard, newBoard.state.Black_KingPos, Players.White)) {
                                            moves.add(newMove);
                                        }
                                    }
                                    if (piece != null) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (movingPiece.type == PieceTypes.King) {
                for (int xOffset : new int[] { -1, 0, 1 }) {
                    for (int yOffset : new int[] { -1, 0, 1 }) {
                        if (xOffset != 0 || yOffset != 0) {
                            Integer pos = coordToNum(x + xOffset, y + yOffset);
                            if (pos != null) {
                                Piece piece = board.pieces.get(pos);
                                if (piece == null || piece.color != movingPiece.color) {
                                    HashMap<Integer, Piece> newPieces = new HashMap<Integer, Piece>(board.pieces);
                                    newPieces.remove(fromPos);
                                    newPieces.put(pos, movingPiece);
                                    BoardState newState = new BoardState(board.state);
                                    newState.turn = board.state.turn == Players.White ? Players.Black : Players.White;
                                    if (movingPiece.color == Players.White) {
                                        newState.White_KingPos = pos;
                                        newState.White_KingMoved = true;
                                    } else {
                                        newState.Black_KingPos = pos;
                                        newState.Black_KingMoved = true;
                                    }
                                    Board newBoard = new Board(newPieces, newState);
                                    Move newMove = new Move(MoveTypes.Normal, fromPos, pos, board, newBoard);
                                    newBoard.state.lastMove = newMove;
                                    if (movingPiece.color == Players.White) {
                                        if (!isAttacked(newBoard, newBoard.state.White_KingPos, Players.Black)) {
                                            moves.add(newMove);
                                        }
                                    } else {
                                        if (!isAttacked(newBoard, newBoard.state.Black_KingPos, Players.White)) {
                                            moves.add(newMove);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (movingPiece.color == Players.White) {
                    for (int rookOffset : new int[] { -4, 3 }) {
                        Integer rookIndex = fromPos + rookOffset;
                        boolean isLeftCastle = rookOffset < 0;
                        Piece rookPiece = board.pieces.get(rookIndex);
                        if (!board.state.White_KingMoved && !board.state.White_LeftRookMoved && rookPiece != null
                                && rookPiece.type == PieceTypes.Rook) {
                            boolean canCastle = true;
                            for (int i = isLeftCastle ? rookIndex + 1 : fromPos + 1; i < (isLeftCastle ? fromPos
                                    : rookIndex); i++) {
                                if (board.pieces.get(i) != null) {
                                    canCastle = false;
                                    break;
                                }
                            }
                            for (int i = isLeftCastle ? fromPos - 2 : fromPos; i <= (isLeftCastle ? fromPos
                                    : fromPos + 2); i++) {
                                if (isAttacked(board, i, Players.Black)) {
                                    canCastle = false;
                                    break;
                                }
                            }
                            if (canCastle) {
                                HashMap<Integer, Piece> newPieces = new HashMap<Integer, Piece>(board.pieces);
                                newPieces.remove(fromPos);
                                newPieces.put(fromPos + (isLeftCastle ? -2 : 2), movingPiece);
                                newPieces.remove(rookIndex);
                                newPieces.put(fromPos + (isLeftCastle ? -1 : 1), rookPiece);
                                BoardState newState = new BoardState(board.state);
                                newState.turn = board.state.turn == Players.White ? Players.Black : Players.White;
                                newState.White_KingPos = fromPos + (isLeftCastle ? -2 : 2);
                                newState.White_KingMoved = true;
                                Board newBoard = new Board(newPieces, newState);
                                Move newMove = new Move(MoveTypes.Castle, fromPos, fromPos + (isLeftCastle ? -2 : 2),
                                        board, newBoard);
                                newBoard.state.lastMove = newMove;
                                moves.add(newMove);
                            }
                        }
                    }
                } else {
                    for (int rookOffset : new int[] { -4, 3 }) {
                        Integer rookIndex = fromPos + rookOffset;
                        boolean isLeftCastle = rookOffset < 0;
                        Piece rookPiece = board.pieces.get(rookIndex);
                        if (!board.state.Black_KingMoved && !board.state.Black_LeftRookMoved && rookPiece != null
                                && rookPiece.type == PieceTypes.Rook) {
                            boolean canCastle = true;
                            for (int i = isLeftCastle ? rookIndex + 1 : fromPos + 1; i < (isLeftCastle ? fromPos
                                    : rookIndex); i++) {
                                if (board.pieces.get(i) != null) {
                                    canCastle = false;
                                    break;
                                }
                            }
                            for (int i = isLeftCastle ? fromPos - 2 : fromPos; i <= (isLeftCastle ? fromPos
                                    : fromPos + 2); i++) {
                                if (isAttacked(board, i, Players.Black)) {
                                    canCastle = false;
                                    break;
                                }
                            }
                            if (canCastle) {
                                HashMap<Integer, Piece> newPieces = new HashMap<Integer, Piece>(board.pieces);
                                newPieces.remove(fromPos);
                                newPieces.put(fromPos + (isLeftCastle ? -2 : 2), movingPiece);
                                newPieces.remove(rookIndex);
                                newPieces.put(fromPos + (isLeftCastle ? -1 : 1), rookPiece);
                                BoardState newState = new BoardState(board.state);
                                newState.turn = board.state.turn == Players.White ? Players.Black : Players.White;
                                newState.Black_KingPos = fromPos + (isLeftCastle ? -2 : 2);
                                newState.Black_KingMoved = true;
                                Board newBoard = new Board(newPieces, newState);
                                Move newMove = new Move(MoveTypes.Castle, fromPos, fromPos + (isLeftCastle ? -2 : 2),
                                        board, newBoard);
                                newBoard.state.lastMove = newMove;
                                moves.add(newMove);
                            }
                        }
                    }
                }
            } else if (movingPiece.type == PieceTypes.Pawn) {
                int xDir = movingPiece.color == Players.Black ? 1 : -1;
                for (int i = 1; i <= 2; i++) {
                    for (int yOffset : new int[] { -1, 0, 1 }) {
                        if (i == 1 || (yOffset == 0 && x == (movingPiece.color == Players.Black ? 1 : 6))) {
                            Integer pos = coordToNum(x + xDir * i, y + yOffset);
                            Piece piece = board.pieces.get(pos);
                            if (pos != null) {
                                if (((piece == null && yOffset == 0)
                                        || (yOffset != 0 && piece != null && piece.color != movingPiece.color))
                                        && (i != 2 || board.pieces.get(coordToNum(x + xDir, y + yOffset)) == null)) {
                                    HashMap<Integer, Piece> newPieces = new HashMap<Integer, Piece>(board.pieces);
                                    newPieces.remove(fromPos);
                                    newPieces.put(pos, movingPiece);
                                    BoardState newState = new BoardState(board.state);
                                    newState.turn = board.state.turn == Players.White ? Players.Black : Players.White;
                                    Board newBoard = new Board(newPieces, newState);
                                    Move newMove = new Move(MoveTypes.Normal, fromPos, pos, board, newBoard);
                                    newBoard.state.lastMove = newMove;
                                    // Handle promotions
                                    if (pos < boardCols || pos >= 7 * boardCols) {
                                        if (autoPromote) {
                                            newPieces.put(pos, new Piece(PieceTypes.Queen, movingPiece.color));
                                        } else {
                                            // Nobody's turn to prevent inputs other than completing promotion.
                                            newState.turn = null;
                                            // Create promotion piece type, which is a placeholder to signal displaying
                                            // the
                                            // promotion choices.
                                            newPieces.put(pos, new Piece(PieceTypes.Promotion, movingPiece.color));
                                        }
                                    }
                                    if (movingPiece.color == Players.White) {
                                        if (!isAttacked(newBoard, newBoard.state.White_KingPos, Players.Black)) {
                                            moves.add(newMove);
                                        }
                                    } else {
                                        if (!isAttacked(newBoard, newBoard.state.Black_KingPos, Players.White)) {
                                            moves.add(newMove);
                                        }
                                    }
                                } else if (pos != null && i == 1 && yOffset != 0 && piece == null) {
                                    Integer passant_pos = pos - xDir * boardCols;
                                    Piece passant_piece = board.pieces.get(passant_pos);
                                    Piece pos_piece_before = (board.state.lastMove != null
                                            ? board.state.lastMove.prevBoard.pieces.get(pos)
                                            : null);
                                    Piece passant_piece_before = board.state.lastMove != null
                                            ? board.state.lastMove.prevBoard.pieces.get(passant_pos)
                                            : null;
                                    if (passant_piece != null && passant_piece.color != movingPiece.color
                                            && passant_piece.type == PieceTypes.Pawn && passant_piece_before == null
                                            && pos_piece_before == null) {
                                        HashMap<Integer, Piece> newPieces = new HashMap<Integer, Piece>(board.pieces);
                                        newPieces.remove(fromPos);
                                        newPieces.remove(passant_pos);
                                        newPieces.put(pos, movingPiece);
                                        BoardState newState = new BoardState(board.state);
                                        newState.turn = board.state.turn == Players.White ? Players.Black
                                                : Players.White;
                                        Board newBoard = new Board(newPieces, newState);
                                        Move newMove = new Move(MoveTypes.Normal, fromPos, pos, board, newBoard);
                                        newBoard.state.lastMove = newMove;
                                        if (movingPiece.color == Players.White) {
                                            if (!isAttacked(newBoard, newBoard.state.White_KingPos, Players.Black)) {
                                                moves.add(newMove);
                                            }
                                        } else {
                                            if (!isAttacked(newBoard, newBoard.state.Black_KingPos, Players.White)) {
                                                moves.add(newMove);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }
}
