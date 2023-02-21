package remote;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
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

/**
 * This class represents a proxy referee, which simulates the referee on the client side. It deserializes incoming
 * JSON to the appropriate objects, hands them to the player, and serializes outgoing objects to JSON.
 */
public class Referee {

  private final Socket server;
  private final InputStream in;
  private final PrintStream out;
  private final referee.Player player;
  private final ObjectMapper mapper = JsonUtils.getMapper();

  private static final String VOID_RETURN = "void";

  public Referee(Socket server, referee.Player player) throws IOException{
    this.server = server;
    this.in = server.getInputStream();
    this.out = new PrintStream(server.getOutputStream());
    this.player = player;
  }

  /**
   * Runs the proxy referee. This method will run in perpetuity until the connection is closed, which will only happen
   * if the server sends a malformed response or the server sends the win information.
   */
  public void run() {
    try {
      JsonParser parser = JsonUtils.getJsonParser(in, mapper);
      while(!server.isClosed()) {
        ArrayNode request = mapper.readTree(parser);

        try {
          MName methodName = MName.fromString(request.get(0).asText());
          ArrayNode parameters = (ArrayNode)request.get(1);
          handleRequest(methodName, parameters);
        }
        catch (JsonProcessingException | ClassCastException | IllegalArgumentException e) {
          //We received malformed JSON from the server, but we can't do anything about it except close streams
          this.in.close();
          this.out.close();
        }
      }
    }
    catch (IOException e) {
      // Disconnected from server or parsing exception, which we can't do anything about
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
    TextNode voidJson = JsonNodeFactory.instance.textNode(VOID_RETURN);
    out.println(voidJson);
    out.flush();
    try {
      server.close();
    }
    catch (IOException e) {
      // This block will only be reached if the server is already closed, which should never happen,
      // Or unless we are being blocked by an in-progress I/O operation, which should also never happen.
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
    TextNode voidJson = JsonNodeFactory.instance.textNode(VOID_RETURN);
    out.println(voidJson);
    out.flush();
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
    out.flush();
  }

}
