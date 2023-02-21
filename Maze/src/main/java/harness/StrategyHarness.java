package harness;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import json.ActionJson;
import json.JsonUtils;
import json.StateJson;
import json.StrategyJson;
import model.state.Action;
import model.state.PlayerStateWrapper;
import model.state.State;
import model.strategy.Strategy;
import util.Posn;

/**
 * Strategy Harness built for Milestone 5.
 */
public class StrategyHarness {

  public static void main(String[] args) {
    InputStream inputStream = System.in;
    PrintStream outputStream = System.out;
    strategyTestHarness(inputStream, outputStream);
    System.exit(0);
  }

  // Test harness used for Milestone 5
  // Takes in a Strategy Designation, State, and Coordinate
  // Prints the choice according to the strategy which is an Index, Direction, Degree, Coordinate
  public static boolean strategyTestHarness(InputStream inputStream, PrintStream outputStream) {
    try {
      ObjectMapper mapper = JsonUtils.getMapper();
      JsonParser parser = JsonUtils.getJsonParser(inputStream, mapper);

      Strategy strategy = new StrategyJson(mapper.readValue(parser, String.class)).getStrategy();

      State state = mapper.readValue(parser, StateJson.class).buildState();

      Posn coord = mapper.readValue(parser, Posn.class);

      Action action = strategy.makeAction(new PlayerStateWrapper(state, state.whichPlayerTurn()), coord);

      outputStream.println(ActionJson.serializeToJson(action));
      return true;

    } catch (IOException e) {
      System.out.println("Encountered issue parsing JSON...\n" + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
}
