package model.board;

import java.util.EnumSet;
import java.util.List;
import util.Tuple;

public interface Tile {

  /**
   * Returns the gems located on this tile.
   * @return the gems held on this tile
   */
  List<Gem> getGems();

  /**
   * Finds all accessible tiles based on whether this tile can reach the neighbor tile and the
   * neighbor tile can reach this tile.
   * @param neighbors the tiles that neighbor this tile in the given direction
   * @return the tiles that are accessible
   */
  List<Tile> getAccessibleNeighbors(List<Tuple<Tile, Direction>> neighbors);

  /**
   * Checks whether this tile points in the given direction.
   * @param dir the direction to check
   * @return whether this tile points to the given direction
   */
  boolean doesTilePointInDirection(Direction dir);

  /**
   * Gets the tile directions
   * @return the directions that this tile can reach other tiles
   */
  EnumSet<Direction> getTileDirections();

  /**
   * Rotates this tile counter-clockwise n times
   * @param n number of rotations to rotate this tile
   */
  void rotateCounterClockwiseNTimes(int n);

  /**
   * Makes a copy of this tile
   * @return a deep copy of this tile
   */
  Tile getCopy();

}
