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
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Akka;
import play.libs.Json;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

/**
 * Created with IntelliJ IDEA.
 * User: jorgelorenzon
 * Date: 11/12/12
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatchesManagerActor extends UntypedActor {

    public static ActorRef matchesManager = Akka.system().actorOf(new Props(MatchesManagerActor.class), "matchesManager");

    ActorRef player1;
    ActorRef player2;
    Long lastId;

    @Override
    public void preStart() {
        super.preStart();
        lastId = 1L;
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof FindMatch) {
            if (player1 == null) {
                player1 = ((FindMatch) message).getPlayer();
            } else if (player2 == null) {
                player2 = ((FindMatch) message).getPlayer();

                final Long matchId = lastId++;

                ActorRef match = getContext().actorOf(new Props(new UntypedActorFactory() {
                    public UntypedActor create() {
                        return new MatchActor(matchId, player1, player2);
                    }
                }), matchId.toString());

                match.tell(new MatchActor.StartMatch());

                player1 = null;
                player2 = null;
            }
        }
    }

    public static class FindMatch {
        private ActorRef player;
        private String userId;

        public FindMatch(ActorRef player, String userId) {
            this.player = player;
            this.userId = userId;
        }

        public ActorRef getPlayer() {
            return player;
        }

        public String getUserId() {
            return userId;
        }
    }
}
