package harness;

import client.Client;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.JsonUtils;
import json.PlayerAPIJson;
import referee.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Client Harness for milestone 9.
 */
public class ClientHarness {

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        PrintStream outputStream = System.out;
        clientTestHarness(inputStream, outputStream, args);
        //System.exit(0);
    }

    // Test harness used for Milestone 9
    // Takes in a BadPlayerSpec2 and launches the clients accordingly
    public static boolean clientTestHarness(InputStream inputStream, PrintStream outputStream, String[] args) {
        int portNum = Integer.parseInt(args[0]);
        String host = "localhost";
        if(args.length == 2){
            host = args[1];
        }
        try {
            ObjectMapper mapper = JsonUtils.getMapper();
            JsonParser parser = JsonUtils.getJsonParser(inputStream, mapper);

            List<Player> players = new ArrayList<>(
                    Arrays.asList(mapper.readValue(parser, PlayerAPIJson[].class)))
                    .stream().map(PlayerAPIJson::build)
                    .collect(Collectors.toList());
            for( Player player : players){
                Client client = new Client(host,portNum);
                client.connectClientAndPlayGame(player);
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (IOException e) {
            System.out.println("Encountered issue parsing JSON...\n" + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (InterruptedException ignored) { }
        return true;
    }
}
