package controllers.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.WebSocket;

/**
 * Created with IntelliJ IDEA.
 * User: jorgelorenzon
 * Date: 12/6/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerActor extends UntypedActor {

    String userId;
    WebSocket.Out<JsonNode> webSocketOut;
    int unansweredPings;


    public PlayerActor(String userId, WebSocket.Out<JsonNode> webSocketOut) {
        this.userId = userId;
        this.webSocketOut = webSocketOut;
    }


    @Override
    public void onReceive(Object o) throws Exception {

        if (o instanceof WriteToSocket) {
            writeToSocket(((WriteToSocket) o).getMessage());
        }
        if (o instanceof ReplaceWebSocket) {
            webSocketOut = ((ReplaceWebSocket) o).getWebSocketOut();
            ObjectNode connectedNode = Json.newObject();
            connectedNode.put("Command", "Connected");
            webSocketOut.write(connectedNode);
            unansweredPings = 0;
        }
        if (o instanceof Connected) {
            ObjectNode connectedNode = Json.newObject();
            connectedNode.put("Command", "Connected");
            webSocketOut.write(connectedNode);
        }
    }

    void disconnectPlayer() {
        webSocketOut.close();
        webSocketOut = null;
    }

    public static class ReplaceWebSocket {
        private WebSocket.Out<JsonNode> webSocketOut;

        public ReplaceWebSocket(WebSocket.Out<JsonNode> webSocketOut) {
            this.webSocketOut = webSocketOut;
        }

        public WebSocket.Out<JsonNode> getWebSocketOut() {
            return webSocketOut;
        }
    }

    public static class WriteToSocket {
        private JsonNode message;

        public WriteToSocket(JsonNode message) {
            this.message = message;
        }

        public JsonNode getMessage() {
            return message;
        }
    }

    private void writeToSocket(JsonNode message) {
        try {
            webSocketOut.write(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class GetData {
    }

    public static class GetMatches {

    }

    public static class Connected {}
}
