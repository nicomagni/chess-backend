package models;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;

/**
 * Created with IntelliJ IDEA.
 * User: jorgelorenzon
 * Date: 4/8/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Game {

    public final static int UP = 0;
    public final static int UP_LEFT = 1;
    public final static int UP_RIGHT = 2;
    public final static int LEFT = 3;
    public final static int RIGHT = 4;
    public final static int DOWN = 5;
    public final static int DOWN_LEFT = 6;
    public final static int DOWN_RIGHT = 7;

    public Piece[] board;

    public boolean whitesMove;

    public Game() {
        whitesMove = true;
        board = new Piece[64];

        Piece kingW = new Piece(Piece.KING, true);
        Piece queenW = new Piece(Piece.QUEEN, true);
        Piece bishop1W = new Piece(Piece.BISHOP, true);
        Piece bishop2W = new Piece(Piece.BISHOP, true);
        Piece knight1W = new Piece(Piece.KNIGHT, true);
        Piece knight2W = new Piece(Piece.KNIGHT, true);
        Piece tower1W = new Piece(Piece.TOWER, true);
        Piece tower2W = new Piece(Piece.TOWER, true);

        board[60] = kingW;
        board[59] = queenW;
        board[58] = bishop1W;
        board[61] = bishop2W;
        board[57] = knight1W;
        board[62] = knight2W;
        board[56] = tower1W;
        board[63] = tower2W;
        for (int i = 48; i < 56; ++i) {
            Piece pawn = new Piece(Piece.PAWN, true);
            board[i] = pawn;
        }


        Piece kingB = new Piece(Piece.KING, false);
        Piece queenB = new Piece(Piece.QUEEN, false);
        Piece bishop1B = new Piece(Piece.BISHOP, false);
        Piece bishop2B = new Piece(Piece.BISHOP, false);
        Piece knight1B = new Piece(Piece.KNIGHT, false);
        Piece knight2B = new Piece(Piece.KNIGHT, false);
        Piece tower1B = new Piece(Piece.TOWER, false);
        Piece tower2B = new Piece(Piece.TOWER, false);

        board[4] = kingB;
        board[3] = queenB;
        board[2] = bishop1B;
        board[5] = bishop2B;
        board[1] = knight1B;
        board[6] = knight2B;
        board[0] = tower1B;
        board[7] = tower2B;
        for (int i = 7; i < 15; ++i) {
            Piece pawn = new Piece(Piece.PAWN, false);
            board[i] = pawn;
        }
    }

    public Piece getPieceAt(int pos) {
        return board[pos];
    }

    public static int getRow(int pos) {
        return pos / 8;
    }

    public static int getColumn(int pos) {
        return pos % 8;
    }

    public void movePieceTo(Piece piece, int to) {
        for (Piece p : board) {
            if (p == piece) {
                p = null;
                break;
            }
        }

        board[to] = piece;
        whitesMove = !whitesMove;
    }

    public JsonNode json() {
        ArrayNode boardNode = JsonNodeFactory.instance.arrayNode();
        for (Piece p : board) {
            if (p == null) {
                boardNode.addNull();
            } else {
                boardNode.add(p.json());
            }
        }
        return boardNode;
    }
}
