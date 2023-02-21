package model.board;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import util.Direction;
import util.Tuple;
import util.Util;

/**
 * Represents a tile on the board of a game of Labyrinth.
 */
public class BasicTile implements Tile {

  private EnumSet<Direction> tileDirections;
  private final List<Gem> gems;

  private static final int REQ_NUM_GEMS = 2;

  /**
   * Constructs a BasicTile with the given directions and gems.
   * @param tileDirections the directions of the tile
   * @param gems the gems on the tile
   * @throws IllegalArgumentException if the number of gems is not the required number of gems
   */
  public BasicTile(EnumSet<Direction> tileDirections, List<Gem> gems) {
    this.tileDirections = tileDirections;

    if(!givenGemsAreLegalSet(gems)) {
      throw new IllegalArgumentException("Malformed set of Gems added to this Tile.");
    }
    this.gems = gems;
  }

  @Override
  public List<Gem> getGems() {
    return this.gems;
  }

  @Override
  public List<Tile> getAccessibleNeighbors(List<Tuple<Tile, Direction>> neighbors) {
    List<Tile> accessible = new ArrayList<>();
    for (Tuple<Tile, Direction> neighborTuple : neighbors) {

      boolean thisTileCanAccessNeighbor = this.tileDirections.contains(neighborTuple.getSecond());
      Direction reverseDirection = Direction.getNClockwiseRotations(neighborTuple.getSecond(), Direction.values().length / 2);
      boolean neighborTileCanAccessTile = neighborTuple.getFirst().doesTilePointInDirection(reverseDirection);

      if(thisTileCanAccessNeighbor && neighborTileCanAccessTile) {
        accessible.add(neighborTuple.getFirst());
      }
    }
    return accessible;
  }

  @Override
  public boolean doesTilePointInDirection(Direction dir) {
    return tileDirections.contains(dir);
  }

  @Override
  public EnumSet<Direction> getTileDirections() {
    return tileDirections;
  }

  @Override
  public void rotateCounterClockwiseNTimes(int n) {
    EnumSet<Direction> rotatedAccessibleDirs = EnumSet.noneOf(Direction.class);
    for (Direction dir: this.tileDirections) {
      Direction rotatedValue = Direction.getNCounterClockwiseRotations(dir, n);
      rotatedAccessibleDirs.add(rotatedValue);
    }
    this.tileDirections = rotatedAccessibleDirs;
  }

  @Override
  public Tile getCopy() {

    EnumSet<Direction> directionsCopy = EnumSet.noneOf(Direction.class);
    for (Direction direction: this.tileDirections) {
      directionsCopy.add(direction);
    }
    List<Gem> gemsCopy = new ArrayList<>();
    for (Gem gem : this.gems) {
      gemsCopy.add(gem);
    }
    return new BasicTile(directionsCopy, gemsCopy);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof BasicTile) {
      BasicTile other = (BasicTile)o;
      return this.tileDirections.equals(other.tileDirections) &&
          Util.listsContainSameItems(this.gems, other.gems);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return tileDirections.hashCode() * 17 + gems.hashCode() * 29;
  }

  private boolean givenGemsAreLegalSet(List<Gem> gems) {
    return gems.size() == REQ_NUM_GEMS;
  }
}
