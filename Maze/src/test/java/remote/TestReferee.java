package remote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import json.JsonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TestReferee {

  Socket connection;
  InputStream inputStream;
  OutputStream outputStream;
  remote.Player player;
  remote.Referee testReferee;

  @BeforeEach
  public void setUp() throws IOException {
    connection = Mockito.mock(Socket.class);
    inputStream = new ByteArrayInputStream("\"void\"".getBytes());
    outputStream = Mockito.mock(OutputStream.class);
    player = Mockito.mock(remote.Player.class);
    try {
      Mockito.when(connection.getInputStream()).thenReturn(inputStream);
      Mockito.when(connection.getOutputStream()).thenReturn(outputStream);
    }
    catch (Exception e) {
      fail();
    }
    testReferee = new Referee(connection, player);
  }

  @Disabled
  @Test
  public void testHandlingWin() throws IOException {
    String winRequest = "[\"win\", [True]]";

    inputStream = new ByteArrayInputStream(winRequest.getBytes());
    testReferee.run();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    outputStream.write(outputStream.toByteArray());
    String output = outputStream.toString();
    assertEquals("\"void\"", output);
  }

}
