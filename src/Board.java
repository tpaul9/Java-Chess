import java.util.HashMap;
import java.util.Map.Entry;

public class Board {
    public HashMap<Integer,Piece> pieces;
    public BoardState state;

    public Board(){
        pieces=new HashMap<Integer,Piece>();
        pieces.put(0,new Piece(PieceTypes.Rook,Players.Black));
        pieces.put(1,new Piece(PieceTypes.Knight,Players.Black));
        pieces.put(2,new Piece(PieceTypes.Bishop,Players.Black));
        pieces.put(3,new Piece(PieceTypes.Queen,Players.Black));
        pieces.put(4,new Piece(PieceTypes.King,Players.Black));
        pieces.put(5,new Piece(PieceTypes.Bishop,Players.Black));
        pieces.put(6,new Piece(PieceTypes.Knight,Players.Black));
        pieces.put(7,new Piece(PieceTypes.Rook,Players.Black));
        pieces.put(8,new Piece(PieceTypes.Pawn,Players.Black));
        pieces.put(9,new Piece(PieceTypes.Pawn,Players.Black));
        pieces.put(10,new Piece(PieceTypes.Pawn,Players.Black));
        pieces.put(11,new Piece(PieceTypes.Pawn,Players.Black));
        pieces.put(12,new Piece(PieceTypes.Pawn,Players.Black));
        pieces.put(13,new Piece(PieceTypes.Pawn,Players.Black));
        pieces.put(14,new Piece(PieceTypes.Pawn,Players.Black));
        pieces.put(15,new Piece(PieceTypes.Pawn,Players.Black));

        pieces.put(48, new Piece(PieceTypes.Pawn,Players.White));
        pieces.put(49, new Piece(PieceTypes.Pawn,Players.White));
        pieces.put(50, new Piece(PieceTypes.Pawn,Players.White));
        pieces.put(51, new Piece(PieceTypes.Pawn,Players.White));
        pieces.put(52, new Piece(PieceTypes.Pawn,Players.White));
        pieces.put(53, new Piece(PieceTypes.Pawn,Players.White));
        pieces.put(54, new Piece(PieceTypes.Pawn,Players.White));
        pieces.put(55, new Piece(PieceTypes.Pawn,Players.White));
        pieces.put(56, new Piece(PieceTypes.Rook,Players.White));
        pieces.put(57, new Piece(PieceTypes.Knight,Players.White));
        pieces.put(58, new Piece(PieceTypes.Bishop,Players.White));
        pieces.put(59, new Piece(PieceTypes.Queen,Players.White));
        pieces.put(60, new Piece(PieceTypes.King,Players.White));
        pieces.put(61, new Piece(PieceTypes.Bishop,Players.White));
        pieces.put(62, new Piece(PieceTypes.Knight,Players.White));
        pieces.put(63, new Piece(PieceTypes.Rook,Players.White));
        state=new BoardState();
    }

    public Board(HashMap<Integer,Piece> pieces,BoardState state){
        this.pieces=pieces;
        this.state=state;
    }

    public String toString(){
        return this.pieces.toString();
    }
}
