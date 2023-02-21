package harness;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import json.JsonUtils;
import json.PlayerAPIJson;
import json.StateJson;
import model.state.State;
import observer.GUIObserver;
import referee.Player;
import referee.Referee;
import util.Tuple;
import util.Util;

public class BadPlayerHarness {

  public static void main(String[] args) {
    InputStream inputStream = System.in;
    PrintStream outputStream = System.out;
    badPlayerTestHarness(inputStream, outputStream);
    System.exit(0);
  }


  /*
   * This is for both bad player harness 1 and 2 (Milestone 7 and 8)
   */
  public static boolean badPlayerTestHarness(InputStream inputStream, PrintStream outputStream) {
    try {
      ObjectMapper mapper = JsonUtils.getMapper();
      JsonParser parser = JsonUtils.getJsonParser(inputStream, mapper);

      List<Player> players = new ArrayList<>(
          Arrays.asList(mapper.readValue(parser, PlayerAPIJson[].class)))
          .stream().map(PlayerAPIJson::build)
          .collect(Collectors.toList());

      StateJson stateJson = mapper.readValue(parser, StateJson.class);

      stateJson.updatePlayersToContainPlayerAPIs(players);

      State state = stateJson.buildState();

      Referee referee = new Referee(state);
      //referee.subscribeObserver(new GUIObserver());
      //ENABLE FOR MILESTONE 9 LOCAL TESTING:
      Util.reverseList(players);
      Tuple<List<Player>, List<Player>> winnersAndKicked = referee.runFullGame(players);

      List<String> winners = winnersAndKicked.getFirst()
          .stream()
          .map(Player::name)
          .sorted(String::compareTo)
          .collect(Collectors.toList());

      List<String> kicked = winnersAndKicked.getSecond()
          .stream()
          .map(Player::name)
          .sorted(String::compareTo)
          .collect(Collectors.toList());

      List<List<String>> output = new ArrayList<>(Arrays.asList(winners, kicked));

      outputStream.println(JsonUtils.writeObjectToJson(output));
      return true;
    } catch (IOException e) {
      System.out.println("Encountered issue parsing JSON...\n" + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

}




