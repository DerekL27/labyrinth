package json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import model.state.PlayerData;
import model.state.PlayerDataWrapper;
import util.Posn;

/**
 * Class for serializing and deserializing Player information (both player and refereePlayer),
 * @see PlayerData
 * @see <a href="https://course.ccs.neu.edu/cs4500f22/4.html#%28tech._player%29">Player JSON Dcoumentation</a>
 * @see <a href="https://course.ccs.neu.edu/cs4500f22/6.html#%28tech._refereeplayer%29">RefereePlayer JSON Dcoumentation</a>
 */
public class PlayerJson {

  private Posn currentLocation;
  private Posn homeLocation;
  private Posn goTo;
  private Color color;

  @JsonCreator
  public PlayerJson(
      @JsonProperty("current") Posn currentLocation,
      @JsonProperty("home") Posn homeLocation,
      @JsonProperty("color") String color) {

    this.currentLocation = currentLocation;
    this.homeLocation = homeLocation;
    this.color = stringToColor(color);
    this.goTo = new Posn(-1, -1);
  }

  @JsonProperty("goto")
  public void setGoTo(Posn goTo) {
    this.goTo = goTo;
  }

  /**
   * Serializes the given list of players to a Json representation of an array of RefereePlayers.
   * @param players the players to serialize
   * @return the Json representation of the list of PlayerData (RefereePlayer)
   */
  public static JsonNode serializeRefereePlmt(List<PlayerData> players) {

    JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
    ArrayNode plmt = jsonNodeFactory.arrayNode();

    for(PlayerData playerData : players) {
      JsonNode refereePlayer = serializeRefereePlayer(playerData);
      plmt.add(refereePlayer);
    }
    return plmt;
  }

  /**
   * Serializes a lift of player data to their public JSON equivalent.
   * @param players the players to serialize
   * @return a JSON array of Player
   */
  public static JsonNode serializePublicPlmt(List<PlayerDataWrapper> players) {

    JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
    ArrayNode plmt = jsonNodeFactory.arrayNode();

    for(PlayerDataWrapper playerData : players) {
      JsonNode refereePlayer = serializePublicPlayerInfo(playerData);
      plmt.add(refereePlayer);
    }
    return plmt;
  }

  /**
   * Serializes a Player to the Referee Player definition.
   * @param playerData the player to serialize
   * @return the JSON serialized version of the Player
   */
  public static JsonNode serializeRefereePlayer(PlayerData playerData) {
    JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
    ObjectNode refereePlayer = jsonNodeFactory.objectNode();
    refereePlayer.set("current", playerData.getCurrentLocation().serialize());
    refereePlayer.set("home", playerData.getHomeLocation().serialize());
    refereePlayer.set("goto", playerData.getGoalLocation().serialize());
    refereePlayer.set("color", jsonNodeFactory.textNode(colorToString(playerData.getAvatar())));
    return refereePlayer;
  }

  /**
   * Serializes a Player as their public information.
   * @param playerData the player to serialize
   * @return the JSON serialized version of the Player
   */
  public static JsonNode serializePublicPlayerInfo(PlayerDataWrapper playerData) {
    JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
    ObjectNode publicPlayer = jsonNodeFactory.objectNode();
    publicPlayer.set("current", playerData.getCurrentLocation().serialize());
    publicPlayer.set("home", playerData.getHomeLocation().serialize());
    publicPlayer.set("color", jsonNodeFactory.textNode(colorToString(playerData.getAvatar())));
    return publicPlayer;
  }

  public PlayerData buildPlayer() {
    return new PlayerData(this.color, this.currentLocation, this.homeLocation, this.goTo,
            Optional.empty(), 0, false, false, false);
  }


  // All colors that have a special name instead of the regex can be stored as a map
  private static final Map<String, Color> playerColors = new HashMap<>();
  static {
    playerColors.put("purple", Color.magenta);
    playerColors.put("orange", Color.orange);
    playerColors.put("pink", Color.pink);
    playerColors.put("red", Color.red);
    playerColors.put("blue", Color.blue);
    playerColors.put("green", Color.green);
    playerColors.put("yellow", Color.yellow);
    playerColors.put("white", Color.white);
    playerColors.put("black", Color.black);
  }

  private static Color stringToColor(String color) {
    if(playerColors.containsKey(color)) {
      return playerColors.get(color);
    }
    return Color.decode("#" + color);
  }

  private static String colorToString(Color color) {
    List<String> keys = playerColors.entrySet().stream()
        .filter(x -> x.getValue().equals(color))
        .map(Entry::getKey)
        .collect(Collectors.toList());

    if(!keys.isEmpty())  {
      return keys.get(0);
    }
    return String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
  }
}
