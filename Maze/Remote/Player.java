package remote;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Optional;
import json.ActionJson;
import json.JsonUtils;
import json.PlayerAPIJson;
import json.PlayerJson;
import json.StateJson;
import model.board.Board;
import model.state.Action;
import model.state.PlayerStateWrapper;
import util.Posn;

/**
 * Given a Socket connection, do the following for each method:
 * - serialize all parameters
 * - form the request json
 * - wait for a response
 * - deserialize the response into the return type
 * - if the response is malformed, throw an exception
 */
public class Player implements referee.Player {

  private final Socket client;
  private InputStream clientIn;
  private PrintStream clientOut;
  private final String name;
  private static final JsonNodeFactory factory = JsonNodeFactory.instance;

  public Player(Socket client, String name) {
    this.client = client;
    try {
      this.clientIn = client.getInputStream();
      this.clientOut = new PrintStream(client.getOutputStream());
    }
    catch (IOException e) {
      throw new IllegalStateException("Could not connect to player");
    }
    this.name = name;
  }


  @Override
  public String name() {
    return this.name;
  }

  @Override
  public Board proposeBoard0(int rows, int columns) {
    throw new UnsupportedOperationException("Cannot propose a Board");
  }

  @Override
  public Object setup(Optional<PlayerStateWrapper> state0, Posn goal) {

    ArrayNode parameters = factory.arrayNode();
    parameters.add(state0.isPresent() ? StateJson.serializePublicState(state0.get()) : factory.booleanNode(false));
    parameters.add(goal.serialize());

    this.sendRequest(MName.SETUP, parameters);

    try {
      assert (JsonUtils.deserialize(clientIn, String.class).equals("void"));
      return "void";
    }
    catch(IOException e) {
      throw new IllegalStateException("Malformed response");
    }
  }

  @Override
  public Action takeTurn(PlayerStateWrapper s) {

    ArrayNode parameters = factory.arrayNode();
    parameters.add(StateJson.serializePublicState(s));

    this.sendRequest(MName.TAKE_TURN, parameters);

    try {
      ObjectMapper mapper = JsonUtils.getMapper();
      JsonParser parser = JsonUtils.getJsonParser(clientIn, mapper);

      ActionJson response = mapper.readValue(parser, ActionJson.class);
      return response.build();
    }
    catch(IOException e) {
      throw new IllegalStateException("Malformed response");
    }
  }

  @Override
  public Object win(Boolean won) {

    ArrayNode parameters = factory.arrayNode();
    parameters.add(factory.booleanNode(won));

    this.sendRequest(MName.WIN, parameters);

    try {
      assert (JsonUtils.deserialize(clientIn, String.class).equals("void"));
      return "void";
    }
    catch(IOException e) {
      throw new IllegalStateException("Malformed response");
    }
  }

  /**
   * Sends a request to the client socket.
   * @param mName the method to send
   * @param parameters the Json Array of parameters to send
   */
  private void sendRequest(MName mName, ArrayNode parameters) {
    ArrayNode request = factory.arrayNode();
    request.add(mName.toString());
    request.add(parameters);
    clientOut.println(request);
  }
}
