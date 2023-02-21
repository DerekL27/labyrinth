package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public enum Direction {
  LEFT, UP, RIGHT, DOWN;

  public static Direction getClockwiseRotation(Direction dir) throws IllegalArgumentException {
    switch (dir) {
      case LEFT:
        return UP;
      case UP:
        return RIGHT;
      case RIGHT:
        return DOWN;
      case DOWN:
        return LEFT;
      default:
        throw new IllegalArgumentException("Unknown direction given. This should be impossible");
    }
  }

  public static Posn offsetPosnWithDirection(Direction dir, Posn pos, int increment) {
    switch(dir) {
      case LEFT:
        return new Posn(pos.getX() - increment, pos.getY());
      case UP:
        return new Posn(pos.getX(), pos.getY() - increment);
      case RIGHT:
        return new Posn(pos.getX() + increment, pos.getY());
      case DOWN:
        return new Posn(pos.getX(), pos.getY() + increment);
      default:
        throw new IllegalArgumentException("Unknown direction given");
    }
  }

  public static Direction getNClockwiseRotations(Direction dir, int necessaryRotations) {
    necessaryRotations = necessaryRotations % Direction.values().length;
    Direction output = dir;
    for(int i = 0; i < necessaryRotations; i++) {
      output = getClockwiseRotation(output);
    }
    return output;
  }

  private static final HashMap<Character, EnumSet<Direction>> specialCharDirectionMap = new HashMap<Character, EnumSet<Direction>>();
  static {
    specialCharDirectionMap.put('│', EnumSet.of(UP, DOWN));
    specialCharDirectionMap.put('─', EnumSet.of(LEFT, RIGHT));
    specialCharDirectionMap.put('┐', EnumSet.of(LEFT, DOWN));
    specialCharDirectionMap.put('└', EnumSet.of(UP, RIGHT));
    specialCharDirectionMap.put('┌', EnumSet.of(RIGHT, DOWN));
    specialCharDirectionMap.put('┘', EnumSet.of(UP, LEFT));
    specialCharDirectionMap.put('┬', EnumSet.of(LEFT, RIGHT, DOWN));
    specialCharDirectionMap.put('├', EnumSet.of(UP, RIGHT, DOWN));
    specialCharDirectionMap.put('┴', EnumSet.of(UP, LEFT, RIGHT));
    specialCharDirectionMap.put('┤', EnumSet.of(UP, LEFT, DOWN));
    specialCharDirectionMap.put('┼', EnumSet.of(UP, LEFT, RIGHT, DOWN));
  }

  public static EnumSet<Direction> specialCharToDirections(char specialChar) {
    if(specialCharDirectionMap.containsKey(specialChar)) {
      return specialCharDirectionMap.get(specialChar);
    }
    throw new IllegalArgumentException("unknown special char: " + specialChar);
  }

  public static char directionsToSpecialChar(EnumSet<Direction> directions) {
    List<Character> keys = specialCharDirectionMap.entrySet().stream()
        .filter(x -> x.getValue().equals(EnumSet.copyOf(directions)))
        .map(Entry::getKey)
        .collect(Collectors.toList());

    if(!keys.isEmpty())  {
      return keys.get(0);
    }
    throw new IllegalArgumentException("unknown set of directions: " + directions);
  }

  public static Direction getNCounterClockwiseRotations(Direction dir, int n) {
    int length = Direction.values().length;
    n = length - (n % length);
    return getNClockwiseRotations(dir, n);
  }

  /**
   * Serializes a given Direction as a JsonNode
   * @param dir the direction to serialaize
   * @return the JsonNode
   */
  public static JsonNode serialize(Direction dir) {
    return new TextNode(dir.toString());
  }
}
