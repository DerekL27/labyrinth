package remote;

import static org.junit.jupiter.api.Assertions.assertEquals;

import client.Client;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.awt.Color;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import json.BoardJson;
import json.JsonUtils;
import json.PlayerAPIJson.BadFM;
import json.TileJson;
import model.board.Board;
import model.board.Tile;
import model.state.MazeState;
import model.state.PlayerData;
import model.state.State;
import model.strategy.EuclidStrategy;
import org.junit.jupiter.api.Test;
import referee.BadPlayer;
import referee.Player;
import referee.StrategyPlayer;
import referee.TestReferee;
import server.Server;
import util.Posn;
import util.Tuple;

/**
 * NOTE: Because Threads in Java cannot be explicitly killed, Running more than one of the tests in
 * this class at a time with the same port will fail. So, all tests must run with different ports.
 */
public class TestServerClientInteractions {


  @Test
  public void testClientServerSimpleExample() throws InterruptedException, ExecutionException {
    int port = 55443;
    State state = TestReferee.getStateNormalGame();
    Tuple<List<referee.Player>, List<referee.Player>> result = runServerAndClients(state, port);

    assertEquals(1, result.getFirst().size());
    assertEquals("Steve", result.getFirst().get(0).name());
    assertEquals(1, result.getSecond().size());
    assertEquals("KickMe", result.getSecond().get(0).name());
  }

  @Test
  public void testTwoClientsForcedToMoveOffGoalTile()
      throws JsonProcessingException, ExecutionException, InterruptedException {
    int port = 55442;
    State state = getStateTwoPlayersStartOnGoalAndHomeTile();
    Tuple<List<referee.Player>, List<referee.Player>> result = runServerAndClients(state, port);

    assertEquals(1, result.getFirst().size());
    assertEquals("Derek", result.getFirst().get(0).name());
    assertEquals(1, result.getSecond().size());
    assertEquals("Nick", result.getSecond().get(0).name());
  }


  /**
   * This test is messy but it basically takes the functionality of runServerAndClients(...) but
   * it connects the players without the Client code in order to make the players not send names.
   * @throws InterruptedException
   */
  @Test
  public void testPlayerConnectsWithoutSendingName() throws InterruptedException, ExecutionException {
    int port = 55441;
    State state = TestReferee.getStateNormalGame();

    ExecutorService executor = Executors.newFixedThreadPool(2);

    Callable<Tuple<List<referee.Player>, List<referee.Player>>> serverCallable = () -> Server.runWithState(port, state);

    List<Player> playersFromState = state.getPlayers().stream()
        .map(x -> x.getPlayerAPI().get().getPlayer())
        .collect(Collectors.toList());

    List<Runnable> clients = new ArrayList<>();
    for(referee.Player player : playersFromState) {
      clients.add(() -> {
        try {
          Socket server = new Socket("localhost", port);
          new remote.Referee(server, player).run();
        } catch (IOException e) {
          // do nothing
        }
      });
    }

    Future<Tuple<List<referee.Player>, List<referee.Player>>> serverFuture = executor.submit(serverCallable);

    Thread.sleep(2000); // wait a bit for server to setup
    for(Runnable client : clients) {
      executor.submit(client);
      Thread.sleep(2100);
    }

    Tuple<List<referee.Player>, List<referee.Player>> result = serverFuture.get();

    assertEquals(0, result.getFirst().size());
    assertEquals(0, result.getSecond().size());
  }



  /**
   * Spawns a Server on a thread and a Client thread for each player in the state and then returns
   * the result of the server thread. This is done by pulling the players from the state and turning them into
   * remote players via the Client class. Then the clients connnect to the server and the game runs as a server/client.
   * @param state the initial state of the game that contains the players
   * @param port the port to run the game on
   * @return the result of the game
   * @throws ExecutionException something went wrong with the threads
   * @throws InterruptedException something went wrong with the threads
   */
  private static Tuple<List<referee.Player>, List<referee.Player>> runServerAndClients(State state, int port)
      throws ExecutionException, InterruptedException {

    ExecutorService executor = Executors.newFixedThreadPool(2);

    Callable<Tuple<List<referee.Player>, List<referee.Player>>> serverCallable = () -> Server.runWithState(port, state);

    List<Player> playersFromState = state.getPlayers().stream()
        .map(x -> x.getPlayerAPI().get().getPlayer())
        .collect(Collectors.toList());

    List<Runnable> clients = new ArrayList<>();
    for(referee.Player player : playersFromState) {
      clients.add(() -> new Client("localhost", port).connectClientAndPlayGame(player));
    }

    Future<Tuple<List<referee.Player>, List<referee.Player>>> serverFuture = executor.submit(serverCallable);

    for(Runnable client : clients) {
      executor.submit(client);
      Thread.sleep(100);
    }

    Tuple<List<referee.Player>, List<referee.Player>> result = serverFuture.get();
    executor.shutdownNow();
    return result;
  }


  /**
   * This returns a state to check the logic that a player must move onto the goal tile and move
   * onto the home tile to win the game. Because of this logic, the Derek player will need to make
   * four moves to win the game. The Nick player will move two steps before going into an infinite
   * loop because it received the second setup call.
   *
   * @return the state described above
   * @throws JsonProcessingException if the json cannot be parsed
   */
  public static State getStateTwoPlayersStartOnGoalAndHomeTile() throws JsonProcessingException {
    String boardJson = "{\n"
        + "        \"connectors\":\n"
        + "       [[\"┼\",\"┼\",\"┼\",\"┼\"],\n"
        + "        [\"┼\",\"┼\",\"┼\",\"┼\"],\n"
        + "        [\"┼\",\"┼\",\"┼\",\"┼\"],\n"
        + "        [\"┼\",\"┼\",\"┼\",\"┼\"]],\n"
        + "        \"treasures\":\n"
        + "          [[[\"zoisite\",\"zircon\"],[\"zircon\",\"yellow-jasper\"],[\"yellow-jasper\",\"yellow-heart\"],[\"yellow-heart\",\"yellow-beryl-oval\"]],\n"
        + "          [[\"unakite\",\"tourmaline\"],[\"tourmaline\",\"tourmaline-laser-cut\"],[\"tourmaline-laser-cut\",\"tigers-eye\"],[\"tigers-eye\",\"tanzanite-trillion\"]],\n"
        + "          [[\"stilbite\",\"star-cabochon\"],[\"star-cabochon\",\"spinel\"],[\"spinel\",\"sphalerite\"],[\"sphalerite\",\"ruby\"]],\n"
        + "          [[\"rock-quartz\",\"rhodonite\"],[\"rhodonite\",\"red-spinel-square-emerald-cut\"],[\"red-spinel-square-emerald-cut\",\"red-diamond\"],[\"red-diamond\",\"raw-citrine\"]]]\n"
        + "      }";

    String spareJson = "{ \"tilekey\": \"┼\",\n"
        + "        \"1-image\": \"zoisite\",\n"
        + "        \"2-image\": \"rose-quartz\"\n"
        + "      }";

    Board board = JsonUtils.getMapper().readValue(boardJson, BoardJson.class).buildBoard();
    Tile spareTile = JsonUtils.getMapper().readValue(spareJson, TileJson.class).getTile();

    List<PlayerData> players = new ArrayList<>();

    Posn derekPos = new Posn(1, 1);
    PlayerData derek = new PlayerData(Color.green, derekPos, derekPos, derekPos,
            Optional.of(new StrategyPlayer("Derek", new EuclidStrategy())), 0, false, false, false);

    Posn nickPos = new Posn(3, 3);
    PlayerData nick = new PlayerData(Color.red, nickPos, nickPos, nickPos,
            Optional.of(new BadPlayer("Nick", new EuclidStrategy(), BadFM.setUp, 2)), 0, false, false, false);

    players.add(derek);
    players.add(nick);

    return new MazeState(board, players, spareTile, Optional.empty(), new ArrayList<>());
  }
}
