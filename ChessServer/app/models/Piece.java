package models;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;

/**
 * Created with IntelliJ IDEA.
 * User: jorgelorenzon
 * Date: 4/8/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Piece {

    public final static int PAWN = 0;
    public final static int KNIGHT = 1;
    public final static int BISHOP = 2;
    public final static int TOWER = 3;
    public final static int QUEEN = 4;
    public final static int KING = 5;

    int type;
    boolean white;

    public Piece(int type, boolean white) {
        this.type = type;
        this.white = white;
    }

    public JsonNode json() {
        ObjectNode node = Json.newObject();
        node.put("type", type);
        node.put("white", white);
        return node;
    }
}
