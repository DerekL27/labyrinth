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

import json.ActionJson;
import json.JsonUtils;
import json.StateJson;
import json.StrategyJson;
import model.state.Action;
import model.state.PlayerStateWrapper;
import model.state.State;
import model.strategy.Strategy;
import referee.Player;
import server.Server;
import util.Posn;
import util.Tuple;

/**
 * Strategy Harness built for Milestone 5.
 */
public class ServerHarness {

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        PrintStream outputStream = System.out;
        serverTestHarness(inputStream, outputStream, args);
        System.exit(0);
    }

    // Test harness used for Milestone 9
    // Takes in a RefereeState and launches a server
    public static boolean serverTestHarness(InputStream inputStream, PrintStream outputStream, String[] args) {
        try {
            ObjectMapper mapper = JsonUtils.getMapper();
            JsonParser parser = JsonUtils.getJsonParser(inputStream, mapper);

            StateJson stateJson = mapper.readValue(parser, StateJson.class);
            State state = stateJson.buildState();
            Tuple<List<Player>, List<Player>> winnersAndKicked = Server.runWithState(Integer.parseInt(args[0]), state);

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
            outputStream.flush();
            return true;
        } catch (IOException e) {
            System.out.println("Encountered issue parsing JSON...\n" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
