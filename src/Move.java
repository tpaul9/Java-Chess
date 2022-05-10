
public class Move {
  public MoveTypes type;
  public int from;
  public int to;
  public Board prevBoard;
  public Board board;

  public Move(MoveTypes type, int from, int to, Board prevBoard, Board board) {
    this.type = type;
    this.from = from;
    this.to = to;
    this.prevBoard = prevBoard;
    this.board = board;
  }
  public String toString(){
    return String.format("{type: %s, from: %s, to: %s, prevBoard: %s, board: %s}",this.type,Integer.toString(this.from),Integer.toString(this.to),this.prevBoard.toString(),this.board.toString());
  }
}
