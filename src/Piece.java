
public class Piece {
    public PieceTypes type;
    public Players color;
    public Piece(PieceTypes type, Players color){
        this.type=type;
        this.color=color;
    }

    public String toString(){
        return String.format("{type: %s, color: %s}\n",this.type,this.color);
    }
}
