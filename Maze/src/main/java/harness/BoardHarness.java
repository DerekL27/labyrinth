package harness;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import json.BoardJson;
import json.JsonUtils;
import model.board.Board;
import model.board.Gem;
import model.board.Tile;
import util.Posn;

public class BoardHarness {

  public static void main(String[] args) {
    InputStream inputStream = System.in;
    PrintStream outputStream = System.out;
    boardTestHarness(inputStream, outputStream);
    System.exit(0);
  }

  // test harness used for Milestone 3
  // Takes in a Board and a Coordinate
  // Prints a sorted Json array of all Coordinates that are accessible from the given
  // Coordinate on the given board.
  public static boolean boardTestHarness(InputStream inputStream, PrintStream outputStream) {
    try {
      ObjectMapper mapper = JsonUtils.getMapper();
      JsonParser parser = JsonUtils.getJsonParser(inputStream, mapper);

      Board board = mapper.readValue(parser, BoardJson.class).buildBoard();
      Posn coord = mapper.readValue(parser, Posn.class);

      List<Posn> tileCoords = getSortedAccessibleTilePosns(board, coord);

      outputStream.println(JsonUtils.writeObjectToJson(tileCoords));
      return true;
    }
    catch(IOException e) {
      System.out.println("Encountered issue parsing JSON...\n" + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
  // Gets all the positions that can be accessed from the starting position in the board
  private static List<Posn> getSortedAccessibleTilePosns(Board board, Posn start) {
    Set<Tile> accessibleTiles = board.findAllAccessibleTiles(start);

    // abstract to method
    List<Posn> tileCoords = new ArrayList<>();
    for(Tile tile : accessibleTiles) {
      Posn pos = board.getPosOfTile(tile);
      tileCoords.add(pos);
    }
    tileCoords.sort((x, y) -> x.compareTo(y));

    return tileCoords;
  }

}
