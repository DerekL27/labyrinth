package client;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Connects a given referee to a single player. Players to the specified host at the specified port.
 * This is done by creating a new proxy referee for the given player and running it in a new thread created by executorService.
 */
public class Client {

  private final String host;
  private final int port;
  private static final int WAIT_TIME_BEFORE_RETRY_MILLIS = 500;

  public Client(String host, int port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Creates a new proxy referee for the given player and runs it in a new thread created by executorService.
   * @param player the player API to connect
   */
  public void connectClientAndPlayGame(referee.Player player) {

    remote.Referee proxyReferee = createSinglePlayerRemoteRefereeProxy(player);

    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.submit(proxyReferee::run);
    executor.shutdown();
  }

  /**
   * Creates a proxy referee for the given player and connects it to the server. Sends player name over the connection
   * before returning the proxy referee.
   *
   * @param player the player API to create a proxy referee for.
   * @return the proxy referee for the given player
   * @throws IllegalStateException if the client is unable to connect to the server
   */
  private remote.Referee createSinglePlayerRemoteRefereeProxy(referee.Player player) throws IllegalStateException {
    TextNode nameJson = JsonNodeFactory.instance.textNode(player.name());
    try{
      Socket server = new Socket(host, port);
      PrintStream out = new PrintStream(server.getOutputStream());
      out.println(nameJson);
      out.flush();
      return new remote.Referee(server, player);
    }
    catch (IOException e) {
      try {
        TimeUnit.MILLISECONDS.sleep(WAIT_TIME_BEFORE_RETRY_MILLIS);
        return createSinglePlayerRemoteRefereeProxy(player);
      } catch (InterruptedException e1) {
        Thread.currentThread().interrupt();
      }
    }
    throw new IllegalStateException("Unable to connect to server");
  }
}
