package json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.io.IOException;
import java.util.Optional;
import model.state.BasicTurnAction.BasicTurnActionBuilder;
import model.state.PassAction;
import util.Direction;
import model.state.Action;
import model.state.BasicTurnAction;
import util.Posn;
import util.Tuple;

/**
 * This class deals with serializing an Action into a Json Choice.
 * @see Action
 * @see <a href="https://course.ccs.neu.edu/cs4500f22/5.html#%28tech._choice%29">Choice JSON Dcoumentation</a>
 */
public class ActionJson {

  private final Action action;
  private static final String PASS = "PASS";

  @JsonCreator
  public ActionJson(JsonNode node) throws JsonProcessingException {
    if(PASS.equals(node.asText())) {
      this.action = new PassAction();
    }
    else {
      ObjectMapper mapper = JsonUtils.getMapper();

      ArrayNode move = (ArrayNode) node;

      int index = move.get(0).asInt();
      Direction direction = mapper.treeToValue(move.get(1), Direction.class);
      int rotate = move.get(2).asInt() / 90;
      Posn target = mapper.treeToValue(move.get(3), Posn.class);

      this.action = BasicTurnAction.builder()
          .slideTilePosition(new Posn(index, index))
          .slideTileDirection(direction)
          .rotateSpare(rotate)
          .targetPlayerPosition(target)
          .build();
    }
  }

  public Action build() {
    return this.action;
  }


  /**
   * Serializes the given Action to a 'Choice'. A Choice is either a "PASS" or a
   * Json array with the values: [Index, Direction, Degree, Coordinate]
   * @param action the given action to serialize
   * @return the Json formatted as a String
   * @throws IOException if this method is unable to serialize the given data
   */
  public static JsonNode serializeToJson(Action action) {

    Optional<Tuple<Integer, Direction>> plannedAction = action.getPlannedBoardMove();

    JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

    if(plannedAction.isEmpty()) {
      return jsonNodeFactory.textNode("PASS");
    }
    else if(action instanceof BasicTurnAction) {
      ArrayNode jsonArray = jsonNodeFactory.arrayNode();
      BasicTurnAction basicTurnAction = (BasicTurnAction)action;

      jsonArray.add(plannedAction.get().getFirst());
      jsonArray.add(plannedAction.get().getSecond().toString());

      int degree = 90 * basicTurnAction.getRotateAmt(); // convert rotate amt
      jsonArray.add(degree);

      jsonArray.add(basicTurnAction.getTargetPos().serialize());

      return jsonArray;
    }
    throw new IllegalArgumentException("Unsupported Action to serialize.");
  }


}
