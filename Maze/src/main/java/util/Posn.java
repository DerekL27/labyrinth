package util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import json.JsonUtils;

/**
 * A simple class to represent a coordinate of integers.
 * The coordinate order is:
 *  x increases in the right direction
 *  y increases in the down direction
 */
public class Posn {

  private int x;

  private int y;

  @JsonCreator
  public Posn(@JsonProperty("column#") int x, @JsonProperty("row#") int y) {
    this.x = x;
    this.y = y;
  }

  @JsonProperty("column#")
  public int getX() {
    return x;
  }

  @JsonProperty("row#")
  public int getY() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof Posn) {
      Posn other = (Posn)o;
      return other.x == this.x && other.y == this.y;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(x) + Integer.hashCode(y);
  }


  /**
   * Serializes this Posn to its Json representation as a Coordinate.
   * @return the json representation of this Posn
   */
  public JsonNode serialize() {
    JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
    ObjectNode posnJson = jsonNodeFactory.objectNode();
    posnJson.set("column#", jsonNodeFactory.numberNode(this.x));
    posnJson.set("row#", jsonNodeFactory.numberNode(this.y));
    return posnJson;
  }

  /**
   * Determine the square distance of two Posns
   * @param second the second posn
   * @return the square distance of two posns
   */
  public int squareDistance(Posn second) {
    // a^2 + b^2 = c^2
    int a = (this.x - second.x);
    int b = (this.y - second.y);
    int csquared = (a * a) + (b * b);
    return csquared;
  }


  /**
   * Compares two Posns in Row-Column order
   * @param other the posn to compare
   * @return 1 if this Posn should come after the given Posn other, -1 if it should come before,
   *         and 0 if they are equal
   */
  public int compareTo(Posn other) {
    if(this.y > other.y) {
      return 1;
    }
    else if(this.y < other.y) {
      return -1;
    }
    else {
      if(this.x > other.x) {
        return 1;
      }
      else if(this.x < other.x) {
        return -1;
      }
      else {
        return 0;
      }
    }
  }
}
