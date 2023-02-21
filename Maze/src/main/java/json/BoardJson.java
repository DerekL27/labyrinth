package json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.List;
import model.board.BasicTile;
import model.board.Board;
import model.board.RectBoard;
import util.Direction;
import model.board.Gem;
import model.board.Tile;
import util.Posn;

/**
 * This class deals with serializing a Board into a Json Board and deserializing a Json Board into a Board.
 * @see Board
 * @see <a href="https://course.ccs.neu.edu/cs4500f22/3.html#%28tech._board%29">Board JSON Dcoumentation</a>
 */
public class BoardJson {

  private List<List<String>> connectors;
  private List<List<List<String>>> treasures;

  @JsonCreator
  public BoardJson(@JsonProperty("connectors") List<List<String>> connectors,
      @JsonProperty("treasures") List<List<List<String>>> treasures) {
    this.connectors = connectors;
    this.treasures = treasures;
  }

  /**
   * Serializes the given board to a JsonObject represented as a JsonNode.
   * @param board the board to serialize
   * @return the Json representation of the board
   */
  public static JsonNode serialize(Board board) {
    if(!board.isBoardBuilt()) {
      return null; // null is a valid json value
    }

    JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
    ArrayNode connectorArray = jsonNodeFactory.arrayNode();
    ArrayNode treasureArray = jsonNodeFactory.arrayNode();


    for(int row = 0; row < board.getBoardHeight(); row++) {
      ArrayNode connectorRow = jsonNodeFactory.arrayNode();
      ArrayNode treasureRow = jsonNodeFactory.arrayNode();
      for(int col = 0; col < board.getBoardWidth(); col ++) {

        Tile tile = board.getTile(new Posn(col, row)).get();
        List<Gem> gems = tile.getGems();

        TextNode connector = jsonNodeFactory.textNode("" + Direction.directionsToSpecialChar(tile.getTileDirections()));

        ArrayNode treasure = jsonNodeFactory.arrayNode();
        treasure.add(gems.get(0).toString());
        treasure.add(gems.get(1).toString());

        connectorRow.add(connector);
        treasureRow.add(treasure);
      }
      connectorArray.add(connectorRow);
      treasureArray.add(treasureRow);
    }

    ObjectNode boardObject = jsonNodeFactory.objectNode();
    boardObject.set("connectors", connectorArray);
    boardObject.set("treasures", treasureArray);

    return boardObject;
  }

  /**
   * Returns a new Board containing tiles in the order given by the JSON input.
   * @return the new Board instance
   */
  public Board buildBoard() {
    List<List<Tile>> tiles = convertToTiles();
    int boardHeight = tiles.size();

    if(boardHeight == 0) {
      throw new IllegalArgumentException("Board of height 0 is invalid.");
    }

    int boardWidth = tiles.get(0).size();

    if(boardWidth == 0) {
      throw new IllegalArgumentException("Board of width 0 is invalid.");
    }

    Board board = new RectBoard(boardWidth, boardHeight);

    for(int row = 0; row < boardHeight; row++) {
      List<Tile> rowTiles = tiles.get(row);
      for(int col = 0; col < boardWidth; col++) {
        Posn pos = new Posn(col, row);
        board.placeTileSafely(pos, rowTiles.get(col));
      }
    }
    return board;
  }

  /**
   * Converts the connectors and treasures in this Json to Tiles
   * @return a completed board which is a 2d list of tiles
   */
  private List<List<Tile>> convertToTiles() {

    List<List<Tile>> tiles = new ArrayList<>();

    for(int row = 0; row < connectors.size(); row++) {
      List<String> rowStrs = connectors.get(row);
      List<Tile> rowTiles = new ArrayList<>();
      for(int col = 0; col < rowStrs.size(); col++) {

        char specialChar = rowStrs.get(col).charAt(0);
        List<Gem> gems = convertToGems(treasures.get(row).get(col));

        rowTiles.add(new BasicTile(Direction.specialCharToDirections(specialChar), gems));
      }
      tiles.add(rowTiles);
    }
    return tiles;
  }

  private static List<Gem> convertToGems(List<String> names) {
    List<Gem> gems = new ArrayList<>();
    for(String name : names) {
      String formatToEnum = name.replaceAll("-", "_").toUpperCase();
      Gem value = Gem.valueOf(formatToEnum);
      gems.add(value);
    }
    return gems;
  }
}
