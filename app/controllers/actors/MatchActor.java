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
    String player1uid;
    String player2uid;

    public MatchActor(Long matchId, ActorRef player1, String player1uid, ActorRef player2, String player2uid) {
        this.matchActorData = new MatchActorData();
        this.matchActorData.matchId = matchId;
        this.player1 = player1;
        this.player1uid = player1uid;
        this.player2 = player2;
        this.player2uid = player2uid;
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
                
                //System.out.println(((GameMessage) message).getMessage().get("Game"));
                matchActorData.game.fromJson((JsonNode)((GameMessage) message).getMessage().get("Game"));
                               //Piece piece = matchActorData.game.getPieceAt(((GameMessage) message).getMessage().get("From").asInt());
                //matchActorData.game.movePieceTo(piece, ((GameMessage) message).getMessage().get("To").asInt());
                
                ObjectNode startNode = JsonNodeFactory.instance.objectNode();
                startNode.put("Command", "Moved");
                startNode.put("Game", matchActorData.game.json());
                startNode.put("turn",matchActorData.game.turn);
                        

                broadcastMessage(startNode);
            }
            else if(messageType.equals("Forfeit"))
            {
                System.out.println("Player has forfeited the game.");
                ObjectNode startNode = JsonNodeFactory.instance.objectNode();
                startNode.put("Command", "Forfeit");
                broadcastMessage(startNode);
            }
        }else if (message instanceof StartMatch) {

            matchActorData.game = new Game();

            ObjectNode startNode = JsonNodeFactory.instance.objectNode();
            startNode.put("Command", "StartMatch");
            startNode.put("MatchId", matchActorData.matchId);
            startNode.put("Game", matchActorData.game.json());
            startNode.put(player1uid, 1);
            startNode.put(player2uid, 0);
            
            System.out.println("Starting match with Id: " + matchActorData.matchId);
            broadcastMessage(startNode);
        }
    }

    // All messages

    public static class GameMessage {
        private String fromUserId;
        private JsonNode message;
        
        public GameMessage(){}

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
