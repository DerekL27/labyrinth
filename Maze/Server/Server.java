package server;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import json.JsonUtils;
import model.state.State;
import referee.Player;
import referee.Referee;
import util.Tuple;
import util.Util;


/**
 * The Server accepts a certain number of sockets on a given port, runs an entire game to completion,
 * and returns the winners and kicked players of the game.
 */
public class Server {

  private static final int BOARD_WIDTH = 7;
  private static final int BOARD_HEIGHT = 7;
  private static final int MAX_NUM_CONNECTED_PLAYERS = 6;

  private static final int SIGNUP_WAIT_TIME_SECS = 20;
  private static final int NAME_WAIT_TIME_MILLIS = 2000;

//  public static void main(String[] args) {
//    Tuple<List<Player>, List<Player>> result = run(55443);
//    System.out.println("Winners: " + result.getFirst().stream().map(Player::name).collect(Collectors.toList()));
//    System.out.println("Kicked: " + result.getSecond().stream().map(Player::name).collect(Collectors.toList()));
//  }

  /**
   * Used solely for testing. Runs a server with a specified state.
   * @param port the port to connect to
   * @param state the state to start with
   * @return the result of the game
   */
  public static Tuple<List<Player>, List<Player>> runWithState(int port, State state) {

    List<Player> players = acceptPlayers(port);
    Util.reverseList(players); // order youngest player first

    if(players.size() <= 1) {
      return new Tuple<>(new ArrayList<>(), new ArrayList<>());
    }

    Referee referee = new Referee(state);
    return referee.runFullGame(players);
  }

  /**
   * Accepts up to MAX_NUM_CONNECTED_PLAYERS players after waiting some time. If enough players are
   * signed up, a Referee is created and runs an entire game to completion.
   * @param port the port to accept players on
   * @return a tuple containing the list of players that won and the list of players that were kicked
   */
  public static Tuple<List<Player>, List<Player>> run(int port) {

    List<Player> players = acceptPlayers(port);

    if(players.size() <= 1) {
      return new Tuple<>(new ArrayList<>(), new ArrayList<>());
    }

    Referee referee = new Referee(BOARD_WIDTH, BOARD_HEIGHT);
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
      server.setSoTimeout(NAME_WAIT_TIME_MILLIS);

      acceptBatchOfPlayers(server, players);

      if(players.isEmpty()) {
        acceptBatchOfPlayers(server, players);
      }

    } catch (IOException e) {
    }
    return players;
  }

  /**
   * Accepts a batch of players that connect to the given port. The batch size allows at most the
   * (MAX_NUM_CONNECTED_PLAYERS minus the number of players already connected) to connect.
   * @param server the port to accept players on
   * @param playersAlreadyAccepted the players already accepted.
   */
  private static void acceptBatchOfPlayers(ServerSocket server, List<Player> playersAlreadyAccepted) {

    ExecutorService executor = Executors.newSingleThreadExecutor();

    Callable<List<Player>> playersCallable = () -> acceptMultiplePlayers(server, playersAlreadyAccepted);

    Future<List<Player>> future = executor.submit(playersCallable);
    try {
      future.get(SIGNUP_WAIT_TIME_SECS, TimeUnit.SECONDS);

    } catch (Exception e) {
      future.cancel(true);
    }
    executor.shutdownNow();
  }

  /**
   * Accepts players until all spots are filled. This will be used within a Callable to run as a Future
   * so the method can time out if not all spots are filled. This method adds new players to the given
   * list of players. So, if the method is stopped because of a timeout, the players that already
   * joined will be safe.
   *
   * @param server the port to accept the players on
   * @param playersAlreadyAccepted the players already accepted
   * @return the given list of players appended with newly joined players
   */
  private static List<Player> acceptMultiplePlayers(ServerSocket server, List<Player> playersAlreadyAccepted) {
    while(playersAlreadyAccepted.size() < MAX_NUM_CONNECTED_PLAYERS) {
      Optional<Player> player = acceptSinglePlayer(server);
      if(player.isPresent()) {
        playersAlreadyAccepted.add(player.get());
      }
    }
    return playersAlreadyAccepted;
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
      ObjectMapper mapper = JsonUtils.getMapper();
      JsonParser parser = JsonUtils.getJsonParser(client.getInputStream(), mapper);
      String name = mapper.readValue(parser, String.class);
      return Optional.of(new remote.Player(client, name));
    } catch (IOException e) {
      return Optional.empty();
    }
  }

}
