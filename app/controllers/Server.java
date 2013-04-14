package controllers;

import akka.actor.ActorRef;
import controllers.actors.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

import static akka.pattern.Patterns.ask;

/**
 * Created with IntelliJ IDEA.
 * User: jorgelorenzon
 * Date: 11/15/12
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
//@play.mvc.Security.Authenticated(Secured.class)
public class Server extends Controller {

    public static WebSocket<JsonNode> connectWS() {

        System.out.println("connectWS");

//        final Long userId = Long.parseLong(session("userId"));

        return new WebSocket<JsonNode>() {

            public void onReady(WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {

                // For each event received on the socket,
                in.onMessage(new F.Callback<JsonNode>() {

                    public void invoke (JsonNode event) {

                        String command = event.get("Command").asText();

                        if (command.equals("Connect")) {
                            System.out.println("connect");
                            PlayersManagerActor.playersManager.tell(new PlayersManagerActor.PlayerConnected(event.get("Id").asText(), out));
                        } else if (command.equals("FindMatch")) {
                            ActorRef playerActor = PlayersManagerActor.getPlayerActorRef(event.get("Id").asText());
                            MatchesManagerActor.matchesManager.tell(new MatchesManagerActor.FindMatch(playerActor, event.get("Id").asText()));
                        } else if (command.equals("GameMessage")) {
                            ActorRef matchActor = Akka.system().actorFor(MatchesManagerActor.matchesManager.path().child(String.valueOf(event.get("MatchId").asLong())));
                            matchActor.tell(new MatchActor.GameMessage(event.get("Id").asText(), event));
                        }
                    }
                }

                );

                // When the socket is closed.
                in.onClose(new F.Callback0()

                {
                    public void invoke () {

                    }
                }

                );

            }


        }

                ;
    }
}
