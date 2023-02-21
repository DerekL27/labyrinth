package model.board;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import util.Posn;

public interface Board {

  /**
   * Places a tile in the board at the given position if there is no tile populated at the given
   * position. Also ensures the given posn is within bounds of the board.
   * @param pos the location of the tile
   * @param tile the tile to add to the board
   * @return if the tile has been added or not
   */
  boolean placeTileSafely(Posn pos, Tile tile);

  /**
   * Retrieves the tile at the given location. If there is no tile at the location, or the given
   * position is out of bounds, then the Optional will be empty.
   * @param pos the location of the tile to retrieve
   * @return an Optional Tile that contains the tile at the location if the tile exists; otherwise,
   *         returns an empty Optional Tile
   */
  Optional<Tile> getTile(Posn pos);

  /**
   * Determines whether there is a tile at the given position in the board.
   * @param pos the position to look for a tile
   * @return if the given pos is within bounds and there is a tile at the given pos
   */
  boolean hasTileAt(Posn pos);

  /**
   * Determines if the board is completely populated with tiles (there are no empty spaces).
   * @return if the board is filled with tiles
   */
  boolean isBoardBuilt();

  /**
   * 1. Checks if the row/column at the given position can slide in the given direction.
   * 2. If so, slides the row/column appropriately. Adds the given tile to the empty location
   *    caused by the slide.
   *    If not, the board state is unchanged and the given tile is returned
   * @param pos the position of a tile within the row/column to move
   * @param dir the direction to slide in
   * @param tile the tile to add to the board
   * @return the tile removed by the sliding action; if there is no slide, the given tile is
   *         returned
   */
  Tile slideSafely(Posn pos, Direction dir, Tile tile);

  /**
   * Determines if the row/column at the given position can slide in the given direction according
   * to the board rules.
   * @param pos the position of a tile within the row/column
   * @param dir the direction to slide in
   * @return if the row/col at the given position can slide in the given direction
   */
  boolean canSlide(Posn pos, Direction dir);

  /**
   * Finds a list of all accessible Tiles from the tile at the given position.
   * @param pos the position of the starting tile
   * @return a list of all accessible Tiles from the starting tile
   */
  Set<Tile> findAllAccessibleTiles(Posn pos);

  /**
   * Gets the board width
   * @return the number of tiles that makes up the width of the board
   */
  int getBoardWidth();

  /**
   * Gets the board height
   * @return the number of tiles that makes up the height of the board
   */
  int getBoardHeight();

  /**
   * Returns the coordinates of a given tile within the Board as a Posn. If the board does not
   * contain the tile, the output is a posn with x and y set to -1.
   * @param tile the tile to search for
   * @return the location of the tile in the board
   */
  Posn getPosOfTile(Tile tile);

  /**
   * Provides a deep copy of this Board to prevent from mutating both this board and its copy.
   * @return the deep copy of this board
   */
  Board getCopy();

  /**
   * Retrieves a list of indices that represent which rows can slide (are movable).
   * @return the list of indices
   */
  List<Integer> getMovableRowIndices();

  /**
   * Retrieves a list of indices that represent which columns can slide (are movable).
   * @return the list of indices
   */
  List<Integer> getMovableColIndices();
}
