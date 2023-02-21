package json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.board.Board;
import model.state.PlayerStateWrapper;
import util.Direction;
import model.board.Tile;
import model.state.MazeState;
import model.state.PlayerData;
import model.state.State;
import referee.Player;
import util.Posn;
import util.Tuple;

/**
 * A class that deserializes State and RefereeState.
 * @see State
 * @see <a href="https://course.ccs.neu.edu/cs4500f22/5.html#%28tech._state%29">State JSON Documentation</a>
 * @see <a href="https://course.ccs.neu.edu/cs4500f22/6.html#%28tech._refereestate%29">RefereeState JSON Documentation</a>
 */
public class StateJson {

  private final Board board;
  private final Tile spare;
  private final List<PlayerData> players;
  private final Optional<Tuple<Integer, Direction>> previous;

  private Optional<List<Posn>> goals;

  @JsonCreator
  public StateJson(
      @JsonProperty("board") BoardJson boardJson,
      @JsonProperty("spare") TileJson spareJson,
      @JsonProperty("plmt") List<PlayerJson> players,
      @JsonProperty("last") List<String> action) {

    this.board = boardJson.buildBoard();
    this.spare = spareJson.getTile();

    this.players = new ArrayList<>();
    for(PlayerJson player : players) {
      this.players.add(player.buildPlayer());
    }

    if(action != null) {
      int index = Integer.parseInt(action.get(0));
      Direction direction = Direction.valueOf(action.get(1));
      previous = Optional.of(new Tuple<>(index, direction));
    }
    else {
      previous = Optional.empty();
    }

    this.goals = Optional.empty();
  }

  @JsonProperty("goals")
  public void setGoals(List<Posn> goals) {
    this.goals = Optional.of(goals);
  }

  public State buildState() {
    if(goals.isPresent()) {
      return new MazeState(this.board, this.players, this.spare, this.previous, this.goals.get());
    }
    return new MazeState(this.board, this.players, this.spare, this.previous, new ArrayList<>());
  }

  public void updatePlayersToContainPlayerAPIs(List<Player> playerAPIs) {

    for(int i = 0; i < this.players.size(); i++) {
      this.players.set(i, this.players.get(i).updatePlayerAPI(playerAPIs.get(i)));
    }
  }

  /**
   * Serializes the given State to a JSON object represented as a JsonNode.
   * @param s the state to serialize
   * @return the string representation of the JSON object
   */
  public static JsonNode serializeRefereeState(State s) {
    ObjectMapper mapper = JsonUtils.getMapper();
    ObjectNode stateJson = mapper.getNodeFactory().objectNode();
    stateJson.set("board", BoardJson.serialize(s.getBoard()));
    stateJson.set("spare", TileJson.serialize(s.getSpareTile()));
    stateJson.set("plmt", PlayerJson.serializeRefereePlmt(s.getPlayers()));
    stateJson.set("last", serializeLastAction(s.getPrevMove()));
    return stateJson;
  }

  public static JsonNode serializePublicState(PlayerStateWrapper s) {
    ObjectMapper mapper = JsonUtils.getMapper();
    ObjectNode stateJson = mapper.getNodeFactory().objectNode();
    stateJson.set("board", BoardJson.serialize(s.getBoard()));
    stateJson.set("spare", TileJson.serialize(s.getSpare()));
    stateJson.set("plmt", PlayerJson.serializePublicPlmt(s.getPlayerPublicInfo()));
    stateJson.set("last", serializeLastAction(s.getPrevMove()));
    return stateJson;
  }

  public static JsonNode serializeLastAction(Optional<Tuple<Integer, Direction>> prevMove) {

    if(prevMove.isEmpty()) {
      return null;
    }

    ObjectMapper mapper = JsonUtils.getMapper();
    ArrayNode lastNode = mapper.getNodeFactory().arrayNode(2);

    lastNode.add(prevMove.get().getFirst());
    lastNode.add(Direction.serialize(prevMove.get().getSecond()));
    return lastNode;
  }
}
