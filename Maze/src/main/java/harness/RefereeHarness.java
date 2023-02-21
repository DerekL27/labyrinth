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
import observer.Observer;
import referee.Player;
import referee.Referee;
import util.Tuple;

public class RefereeHarness {


  public static void main(String[] args) {
    InputStream inputStream = System.in;
    PrintStream outputStream = System.out;
    refereeTestHarness(inputStream, outputStream);
    System.exit(0);
  }

  // Test harness used for Milestone 6.
  // Takes in a RefereeState and launches a referee, returns the winner(s)
  public static boolean refereeTestHarness(InputStream inputStream, PrintStream printStream, List<Observer> observers) {
    try {
      ObjectMapper mapper = JsonUtils.getMapper();
      JsonParser parser = JsonUtils.getJsonParser(inputStream, mapper);

      List<Player> players = new ArrayList<>(
          Arrays.asList(mapper.readValue(parser, PlayerAPIJson[].class)))
          .stream().map(PlayerAPIJson::build)
          .collect(Collectors.toList());

      StateJson state = mapper.readValue(parser, StateJson.class);

      state.updatePlayersToContainPlayerAPIs(players);

      Referee referee = new Referee(state.buildState());

      for(Observer observer : observers) {
        referee.subscribeObserver(observer);
      }

      Tuple<List<Player>, List<Player>> winnersAndLosers = referee.runFullGame(players);

      List<String> winners = winnersAndLosers.getFirst()
          .stream()
          .map(Player::name)
          .sorted(String::compareTo)
          .collect(Collectors.toList());

      printStream.println(JsonUtils.writeObjectToJson(winners));
      return true;
    }
    catch (IOException e) {
      System.out.println("Encountered issue parsing JSON...\n" + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  public static boolean refereeTestHarness(InputStream inputStream, PrintStream printStream) {
    return refereeTestHarness(inputStream, printStream, new ArrayList<>());
  }

}
