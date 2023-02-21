package json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import model.board.BasicTile;
import util.Direction;
import model.board.Gem;
import model.board.Tile;
/**
 * Intermediate data structure from Json to the model.board.BasicTile.
 * @see BasicTile
 * @see <a href="https://course.ccs.neu.edu/cs4500f22/4.html#%28tech._tile%29">Tile JSON Documentation</a>
 */
public class TileJson {

  private Tile tile;

  @JsonCreator
  public TileJson(@JsonProperty("tilekey") String connector,
      @JsonProperty("1-image") String gem1,
      @JsonProperty("2-image") String gem2) {

    EnumSet<Direction> directions = Direction.specialCharToDirections(connector.charAt(0));

    List<Gem> gems = new ArrayList<>(Arrays.asList(
        Gem.valueOf(gem1.replaceAll("-", "_").toUpperCase()),
        Gem.valueOf(gem2.replaceAll("-", "_").toUpperCase())
    ));

    this.tile = new BasicTile(directions, gems);
  }

  public Tile getTile() {
    return tile;
  }

  public static JsonNode serialize(Tile tile) {
    ObjectMapper mapper = JsonUtils.getMapper();
    ObjectNode tileJson = mapper.getNodeFactory().objectNode();
    JsonNodeFactory jsonNodeFactory = mapper.getNodeFactory();
    List<Gem> gems = tile.getGems();

    tileJson.set("tilekey", jsonNodeFactory.textNode("" + Direction.directionsToSpecialChar(tile.getTileDirections())));
    tileJson.set("1-image", jsonNodeFactory.textNode(gems.get(0).toString()));
    tileJson.set("2-image", jsonNodeFactory.textNode(gems.get(1).toString()));

    return tileJson;
  }
}
