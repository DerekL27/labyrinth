package harness;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import json.JsonUtils;
import json.StateJson;
import util.Direction;
import model.state.Action;
import model.state.BasicTurnAction;
import model.state.BasicTurnAction.BasicTurnActionBuilder;
import model.state.PlayerData;
import model.state.State;
import util.Posn;

public class StateHarness {

  private final static int SINGLE_ROTATION_DEGREES = 90;

  public static void main(String[] args) {
    InputStream inputStream = System.in;
    PrintStream outputStream = System.out;
    stateTestHarness(inputStream, outputStream);
    System.exit(0);
  }

  // Test harness used for Milestone 4
  // Takes in a State, Index, Direction, and Degree
  // Prints a sorted Json array of all Coordinates that are accessible based on the given state
  // and other 3 inputs that make up a sliding action
  public static boolean stateTestHarness(InputStream inputStream, PrintStream outputStream) {
    try {
      ObjectMapper mapper = JsonUtils.getMapper();
      JsonParser parser = JsonUtils.getJsonParser(inputStream, mapper);

      State state = mapper.readValue(parser, StateJson.class).buildState();

      int index = mapper.readValue(parser, Integer.class);

      Direction direction = mapper.readValue(parser, Direction.class);

      int counterClockwiseRotations = counterClockwiseDegToRotateAmt(mapper.readValue(parser, Integer.class));

      List<Posn> accessibleTilePosns = getAccessibleTilesFromState(state, index, counterClockwiseRotations, direction);

      outputStream.println(JsonUtils.writeObjectToJson(accessibleTilePosns));
      return true;
    } catch (IOException e) {
      System.out.println("Encountered issue parsing JSON...\n" + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  // Gets the accessible tiles for the current player with the given state and slide
  private static List<Posn> getAccessibleTilesFromState(State state, int index, int clockwiseRotations, Direction direction) {

    List<Posn> accessibleTilePosns = new ArrayList<>();

    int width = state.getBoardWidth();
    int height = state.getBoardHeight();

    BasicTurnActionBuilder builder = BasicTurnAction.builder()
        .slideTilePosition(new Posn(index, index))
        .rotateSpare(clockwiseRotations)
        .slideTileDirection(direction);

    PlayerData currentPlayer = state.whichPlayerTurn();
    Posn currentLoc = currentPlayer.getCurrentLocation();

    Posn playerLocationAfterSlide = currentPlayer.updateCurrentLocationIfOnSlide(
        builder.targetPlayerPosition(currentLoc).build().getPlannedBoardMove(),
        width, height).getCurrentLocation();

    accessibleTilePosns.add(playerLocationAfterSlide);

    for (int row = 0; row < state.getBoardHeight(); row++) {
      for (int col = 0; col < state.getBoardWidth(); col++) {

        Posn target = new Posn(col, row);

        Action action = builder
            .targetPlayerPosition(target)
            .build();

        if(state.canApplyAction(action) && !accessibleTilePosns.contains(target)) {
          accessibleTilePosns.add(target);
        }
      }
    }
    return accessibleTilePosns.stream().sorted(Posn::compareTo).collect(Collectors.toList());
  }

  private static int counterClockwiseDegToRotateAmt(int degrees) {
    return degrees / SINGLE_ROTATION_DEGREES;
  }
}
