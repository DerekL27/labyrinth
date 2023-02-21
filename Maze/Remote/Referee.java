package remote;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Optional;
import json.ActionJson;
import json.JsonUtils;
import json.StateJson;
import model.state.Action;
import model.state.PlayerStateWrapper;
import model.state.State;
import util.Posn;

public class Referee {

  private final Socket server;
  private final InputStream in;
  private final PrintStream out;
  private final referee.Player player;
  private final ObjectMapper mapper = JsonUtils.getMapper();

  public Referee(Socket server, referee.Player player) throws IOException{
    this.server = server;
    this.in = server.getInputStream();
    this.out = new PrintStream(server.getOutputStream());
    this.player = player;
  }

  public void run() {
    try {
      JsonParser parser = JsonUtils.getJsonParser(in, mapper);
      while(!server.isClosed()) {
        ArrayNode request = mapper.readTree(parser);

        MName methodName = MName.fromString(request.get(0).asText());
        ArrayNode parameters = (ArrayNode)request.get(1);
        try {
          handleRequest(methodName, parameters);
        }
        catch (JsonProcessingException e) {
          //TODO: what does the player do if the json it receives is malformed?
        }
      }
    }
    catch (IOException e) {
      // Disconnected from server or parsing exception
    }
  }

  /**
   * Determines which request was sent to this proxy referee and delegates to helper methods to handle
   * the deserialization and response logic.
   * @param mName the 'MName' of the method
   * @param parameters the parameters associated with the specified method
   * @throws JsonProcessingException if a helper method fails to deserialize the given parameters
   */
  private void handleRequest(MName mName, ArrayNode parameters) throws JsonProcessingException {
    switch (mName) {
      case WIN:
        this.handleWin(parameters);
        break;
      case SETUP:
        this.handleSetup(parameters);
        break;
      case TAKE_TURN:
        this.handleTakeTurn(parameters);
        break;
    }

  }

  /**
   * Deserializes the win parameter, calls the win method with the correct parameter,
   * and sends a response to show the player acknowledged the call. This method also
   * closes the server socket to stop waiting for requests from the server.
   * @param parameters the JSON array of parameters sent with the Win request
   */
  private void handleWin(ArrayNode parameters) {
    player.win(parameters.get(0).asBoolean());
    out.println("\"void\"");
    try {
      server.close();
    }
    catch (IOException e) {

    }
  }

  /**
   * Deserializes the setup parameters, calls the setup method with the parameters,
   * and sends a response containing the serialized response of the player
   * @param parameters the parameters to deserialize
   * @throws JsonProcessingException if the parameters are malformed
   */
  private void handleSetup(ArrayNode parameters) throws JsonProcessingException{

    Posn coord = mapper.treeToValue(parameters.get(1), Posn.class);
    if(!parameters.get(0).isBoolean()) {
      State s = mapper.treeToValue(parameters.get(0), StateJson.class).buildState();
      player.setup(Optional.of(new PlayerStateWrapper(s, s.whichPlayerTurn())), coord);
    } else {
      player.setup(Optional.empty(), coord);
    }
    out.println("\"void\"");
  }

  /**
   * Deserializes the takeTurn parameters, calls the takeTurn method with the parameters and
   * sends a response containing the serialized response of the player
   * @param parameters the parameters to deserialize
   * @throws JsonProcessingException if the parameters are malformed
   */
  private void handleTakeTurn(ArrayNode parameters) throws JsonProcessingException{
    State s = mapper.treeToValue(parameters.get(0), StateJson.class).buildState();
    Action action = player.takeTurn(new PlayerStateWrapper(s, s.whichPlayerTurn()));
    out.println(ActionJson.serializeToJson(action));
  }

}
