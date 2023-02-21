package client;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Connects a given list of referee.Players to the specified host at the specified port. After
 * the proxy Referees are created for each given player, have each proxy referee listen to the server
 * for incoming requests in each proxy referee.
 */
public class Client {

  private final String host;
  private final int port;

  public Client(String host, int port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Connects the given player to the specified host and port and then runs the proxy Referee
   * to listen for requests from the connected server.
   * @param player the player to connect
   */
  public void connectClientAndPlayGame(referee.Player player) {

    remote.Referee proxyReferee = createSinglePlayerRemoteRefereeProxy(player);

    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.submit(proxyReferee::run);
  }

  /**
   * Creates a remote.Referee proxy for the given referee.Player that is connected to the localhost at
   * the given port.
   *
   * @param player the referee.Player to create a remote.Referee proxy for.
   * @return the proxy referee for the given player
   * @throws IllegalStateException if the client is unable to connect to the server
   */
  private remote.Referee createSinglePlayerRemoteRefereeProxy(referee.Player player) throws IllegalStateException {
    try {
      Socket server = new Socket(this.host, this.port);

      String name = player.name();
      TextNode nameJson = JsonNodeFactory.instance.textNode(name);

      PrintStream serverPrint = new PrintStream(server.getOutputStream());
      serverPrint.println(nameJson);

      return new remote.Referee(server, player);

    } catch (IOException e) {
      throw new IllegalStateException("Unable to connect to server");
    }
  }
}
