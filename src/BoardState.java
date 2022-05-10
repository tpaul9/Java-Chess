public class BoardState {
    public Players turn;
    public Move lastMove;
    public boolean Black_LeftRookMoved;
    public boolean Black_RightRookMoved;
    public boolean Black_KingMoved;
    public boolean White_LeftRookMoved;
    public boolean White_RightRookMoved;
    public boolean White_KingMoved;
    public int Black_KingPos;
    public int White_KingPos;

    public BoardState(){
        this.turn=Players.White;
        this.lastMove=null;
        this.Black_LeftRookMoved=false;
        this.Black_RightRookMoved=false;
        this.Black_KingMoved=false;
        this.White_LeftRookMoved=false;
        this.White_RightRookMoved=false;
        this.White_KingMoved=false;
        this.Black_KingPos=4;
        this.White_KingPos=60;
    }

    public BoardState(BoardState other){
        this.turn=other.turn;
        this.lastMove=other.lastMove;
        this.Black_LeftRookMoved=other.Black_LeftRookMoved;
        this.Black_RightRookMoved=other.Black_RightRookMoved;
        this.Black_KingMoved=other.Black_KingMoved;
        this.White_LeftRookMoved=other.White_LeftRookMoved;
        this.White_RightRookMoved=other.White_RightRookMoved;
        this.White_KingMoved=other.White_KingMoved;
        this.Black_KingPos=other.Black_KingPos;
        this.White_KingPos=other.White_KingPos;
    }
}
