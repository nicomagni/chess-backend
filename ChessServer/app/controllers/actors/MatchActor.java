package controllers.actors;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.util.Duration;
import controllers.actors.data.MatchActorData;
import models.Game;
import models.Piece;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Akka;
import play.libs.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: jorgelorenzon
 * Date: 11/12/12
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatchActor extends UntypedActor {

    public MatchActorData matchActorData;
    ActorRef player1;
    ActorRef player2;

    public MatchActor(Long matchId, ActorRef player1, ActorRef player2) {
        this.matchActorData = new MatchActorData();
        this.matchActorData.matchId = matchId;
        this.player1 = player1;
        this.player2 = player2;
    }

    void broadcastMessage(JsonNode message) {
        PlayerActor.WriteToSocket writeToSocket = new PlayerActor.WriteToSocket(message);
        player1.tell(writeToSocket);
        player2.tell(writeToSocket);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof GameMessage) {
            String messageType = ((GameMessage) message).getMessage().get("MessageType").asText();
            if (messageType.equals("Move")) {

                Piece piece = matchActorData.game.getPieceAt(((GameMessage) message).getMessage().get("From").asInt());
                matchActorData.game.movePieceTo(piece, ((GameMessage) message).getMessage().get("To").asInt());

            }
        } else if (message instanceof StartMatch) {

            matchActorData.game = new Game();

            ObjectNode startNode = JsonNodeFactory.instance.objectNode();
            startNode.put("Command", "StartMatch");
            startNode.put("Game", matchActorData.game.json());

            broadcastMessage(startNode);
        }
    }

    // All messages

    public static class GameMessage {
        private String fromUserId;
        private JsonNode message;

        public GameMessage(String fromUserId, JsonNode message) {
            this.fromUserId = fromUserId;
            this.message = message;
        }

        public String getFromUserId() {
            return fromUserId;
        }

        public JsonNode getMessage() {
            return message;
        }
    }

    public static class StartMatch {}

}
