package server;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.JsonUtils;
import model.state.State;
import referee.Player;
import referee.Referee;
import util.Tuple;
import util.Util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * The Server serves as the main entry point for players to connect to the game. It accepts connections, creating
 * proxy players for the players that connect. It then passes them to a new referee to run the game and returns the winners and kicked.
 * The signup task of this component is not possible to close, so every call to run must be followed by a `system.exit` call for
 * a graceful shutdown.
 */
public class Server {

  private static final int BOARD_WIDTH = 7;
  private static final int BOARD_HEIGHT = 7;
  private static final int MAX_NUM_CONNECTED_PLAYERS = 6;

  private static final int SIGNUP_WAIT_TIME_SECS = 20;
  private static final int NAME_WAIT_TIME_MILLIS = 2000;

  private static final int MIN_NUM_PLAYERS_PER_WAITING_PERIOD = 2;
  private static final int MIN_NUM_PLAYERS_TO_START_GAME = 2;

  private static final int NUM_WAITING_PERIODS = 2;

  /**
   * Used solely for testing. Runs a server with a specified state.
   * @param port the port to connect to
   * @param state the state to start with
   * @return the result of the game
   */
  public static Tuple<List<Player>, List<Player>> runWithState(int port, State state) {
    Referee referee = new Referee(state);
    return runWithReferee(referee, port);
  }

  /**
   * Accepts up to MAX_NUM_CONNECTED_PLAYERS players after waiting some time. If enough players are
   * signed up, a Referee is created and runs an entire game to completion.
   * @param port the port to accept players on
   * @return a tuple containing the list of players that won and the list of players that were kicked
   */
  public static Tuple<List<Player>, List<Player>> run(int port) {
    Referee referee = new Referee(BOARD_WIDTH, BOARD_HEIGHT);
    return runWithReferee(referee, port);
  }

  /**
   * Helper method for running a server. Takes in a port and a referee and runs the server.
   * @param referee the referee to run the game with
   * @param port the port to accept players on
   * @return a tuple containing the list of players that won and the list of players that were kicked
   */
  private static Tuple<List<Player>, List<Player>> runWithReferee(Referee referee, int port) {
    List<Player> players = acceptPlayers(port);
    Util.reverseList(players);

    if(players.size() < MIN_NUM_PLAYERS_TO_START_GAME) {
      return new Tuple<>(new ArrayList<>(), new ArrayList<>());
    }

    return referee.runFullGame(players);
  }

  /**
   * Accepts players on the given port. This method accepts at most MAX_NUM_CONNECTED_PLAYERS on the
   * given port and returns a list of player proxies. The method waits SIGNUP_WAIT_TIME_SECS seconds
   * for players to connect. If no players join the server will wait the same time once more to join
   * the game.
   * @param port the port to accept players on
   * @return the list of connected players in order of when they joined the server (Oldest to Youngest)
   */
  private static List<Player> acceptPlayers(int port) {

    List<Player> players = new ArrayList<>();

    try {
      ServerSocket server = new ServerSocket(port);

      for(int i = 0; i < NUM_WAITING_PERIODS && players.size() < MIN_NUM_PLAYERS_PER_WAITING_PERIOD; i++){
        acceptBatchOfPlayers(server, players);
      }

    } catch (IOException e) {
      //There is already a process running on the specified port, so we don't do anything
    }
    return players;
  }

  /**
   * Accepts a batch of players that connect to the given port. The batch size allows at most the
   * (MAX_NUM_CONNECTED_PLAYERS minus the number of players already connected) to connect.
   * @param server the port to accept players on
   * @param playersAlreadyAccepted the players already accepted, this list gets mutated
   */
  private static void acceptBatchOfPlayers(ServerSocket server, List<Player> playersAlreadyAccepted) {

    ExecutorService executor = Executors.newSingleThreadExecutor();

    Runnable playersRunnable = () -> acceptMultiplePlayers(server, playersAlreadyAccepted);

    Future<?> future = executor.submit(playersRunnable);
    try {
      future.get(SIGNUP_WAIT_TIME_SECS, TimeUnit.SECONDS);

    } catch (Exception e) {
      future.cancel(true);
    }
    executor.shutdownNow();
  }

  /**
   * Accepts players one at a time until all spots are filled. This will be used within a Callable to run as a Future
   * so the method can time out if not all spots are filled. This method adds new players to the given
   * list of players. So, if the method is stopped because of a timeout, the players that already
   * joined will be safe.
   *
   * @param server the port to accept the players on
   * @param playersAlreadyAccepted the players already accepted, this list gets mutated
   */
  private static void acceptMultiplePlayers(ServerSocket server, List<Player> playersAlreadyAccepted) {
    while(playersAlreadyAccepted.size() < MAX_NUM_CONNECTED_PLAYERS) {
      Optional<Player> player = acceptSinglePlayer(server);
      if(player.isPresent()) {
        playersAlreadyAccepted.add(player.get());
      }
    }
  }

  /**
   * Attempts to accept a single player. If a player connects, it will wait up to NAME_WAIT_TIME_SECS
   * seconds to receive a name from the player. If no response is given, the method will return an
   * empty optional. If a player does connect to the given port and sends a name within the allowed time,
   * the method will return an optional that contains the proxy player.
   * @param server the server to accept a player on
   * @return an optional of the player that connected or an empty optional
   */
  private static Optional<Player> acceptSinglePlayer(ServerSocket server) {
    try {
      Socket client = server.accept();
      client.setSoTimeout(NAME_WAIT_TIME_MILLIS);
      ObjectMapper mapper = JsonUtils.getMapper();
      JsonParser parser = JsonUtils.getJsonParser(client.getInputStream(), mapper);
      String name = mapper.readValue(parser, String.class);
      if(!name.matches("^[a-zA-Z0-9]{1,20}$")){
        throw new IllegalStateException("illegal name provided");
      }
      return Optional.of(new remote.Player(client, name));
    } catch (IOException | IllegalStateException e) {
      return Optional.empty();
    }
  }

}
