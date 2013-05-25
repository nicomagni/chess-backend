package controllers.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.dispatch.Future;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.util.Duration;
import akka.util.Timeout;
import org.codehaus.jackson.JsonNode;
import play.libs.Akka;
import play.mvc.WebSocket;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

/**
 * Created with IntelliJ IDEA.
 * User: jorgelorenzon
 * Date: 12/7/12
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayersManagerActor extends UntypedActor {

    public static ActorRef playersManager = Akka.system().actorOf(new Props(PlayersManagerActor.class), "playersManager");

    Map<String, ActorRef> players;

    public PlayersManagerActor() {
        players = new HashMap<String, ActorRef>();

        Akka.system().scheduler().schedule(Duration.create(5, TimeUnit.SECONDS), Duration.create(5, TimeUnit.SECONDS), getSelf(), "SendPing");
    }

    @Override
    public void onReceive(final Object o) throws Exception {
        if (o instanceof PlayerConnected) {

            final String userId = ((PlayerConnected) o).getUserId();
            
            System.out.println("userId = "+userId);

            if (players.containsKey(userId)) {
                players.get(userId).tell(new PlayerActor.ReplaceWebSocket(((PlayerConnected) o).getWebSocketOut()));
            } else {
//                final String nuserId = "1";
                ActorRef playerActor = getContext().actorOf(
                        new Props(
                                new UntypedActorFactory() {
                                    public UntypedActor create() {
                                        return new PlayerActor(userId, ((PlayerConnected) o).getWebSocketOut());
                                    }
                                }), userId.toString());

                players.put(userId, playerActor);

                ActorRef pa = getPlayerActorRef(userId);
                
                pa.tell(new PlayerActor.Connected());
            }
        }
    }

    public static class PlayerConnected {
        private String userId;
        private WebSocket.Out<JsonNode> webSocketOut;

        public PlayerConnected(String userId, WebSocket.Out<JsonNode> webSocketOut) {
            this.userId = userId;
            this.webSocketOut = webSocketOut;
        }

        public String getUserId() {
            return userId;
        }

        public WebSocket.Out<JsonNode> getWebSocketOut() {
            return webSocketOut;
        }
    }

    public static ActorRef getPlayerActorRef(String id) {
        return Akka.system().actorFor(playersManager.path().child(id));
    }
}