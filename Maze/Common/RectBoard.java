package model.board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import util.Direction;
import util.Posn;
import util.Tuple;
import util.Util;

/**
 * A rectangle board representation that can be any pair of dimensions.
 */
public class RectBoard implements Board {

  // row major 2D array
  private ArrayList<ArrayList<Optional<Tile>>> gameboard;
  private List<List<Gem>> gems;

  private final int boardWidth;
  private final int boardHeight;

  private static final Predicate<Integer> isMovableRow = (x) -> (x % 2 == 0);
  private static final Predicate<Integer> isMovableCol = (x) -> (x % 2 == 0);

  public RectBoard(int boardWidth, int boardHeight) {
    this.boardWidth = boardWidth;
    this.boardHeight = boardHeight;

    gameboard = new ArrayList<>();
    for (int i = 0; i < this.boardHeight; i += 1) {
      ArrayList<Optional<Tile>> row = new ArrayList<>();
      for (int j = 0; j < this.boardWidth; j += 1) {
        row.add(Optional.empty());
      }
      gameboard.add(row);
    }
    gems = new ArrayList<>();
  }

  @Override
  public boolean placeTileSafely(Posn pos, Tile tile) {
    if (!inBounds(pos)) return false;

    Optional<Tile> tileOnBoard = gameboard.get(pos.getY()).get(pos.getX());

    if (tileOnBoard.isPresent()) return false;

    for(List<Gem> gemsAdded : gems) {
      if(Util.listsContainSameItems(tile.getGems(), gemsAdded)) {
        return false;
      }
    }

    this.gems.add(tile.getGems());
    this.placeTileUnsafely(pos, tile);
    return true;
  }

  @Override
  public Optional<Tile> getTile(Posn pos) {
    if (inBounds(pos)) {
      return this.gameboard.get(pos.getY()).get(pos.getX());
    }
    return Optional.empty();
  }

  @Override
  public boolean hasTileAt(Posn pos) {
    return this.getTile(pos).isPresent();
  }

  @Override
  public boolean isBoardBuilt() {
    for(int row = 0; row < boardHeight; row++) {
      for(int col = 0; col < boardWidth; col++) {
        if(!hasTileAt(new Posn(col, row))) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public Tile slideSafely(Posn pos, Direction dir, Tile tile) {
    // every other col and row can slide, Edges can slide (4 sliding rows for a 7x7 board)
    // index should be even for row and col
    if(canSlide(pos, dir)) {
      return slideUnsafely(pos, dir, tile);
    }
    return tile;
  }

  private Tile slideUnsafely(Posn pos, Direction dir, Tile tile) {
    switch(dir) {
      case LEFT:
        return this.slideHorizontally(new Posn(0, pos.getY()), new Posn(boardWidth, pos.getY()),
            tile);
      case RIGHT:
        return this.slideHorizontally(new Posn(boardWidth, pos.getY()), new Posn(0, pos.getY()),
            tile);
      case DOWN:
        return this.slideVertically(new Posn(pos.getX(), boardHeight - 1), 0, tile);
      case UP:
        return this.slideVertically(new Posn(pos.getX(), 0), boardHeight - 1, tile);
      default:
        throw new IllegalArgumentException("Unsupported Direction: " + dir);
    }
  }

  @Override
  public boolean canSlide(Posn pos, Direction dir) {
    int row = pos.getY();
    int col = pos.getX();
    if(inBounds(pos) && isBoardBuilt()) {
      if(dir == Direction.DOWN || dir == Direction.UP) {
        if(isMovableCol.test(col)) {
          return true;
        }
      }
      else if(dir == Direction.LEFT || dir == Direction.RIGHT) {
        if(isMovableRow.test(row)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Set<Tile> findAllAccessibleTiles(Posn pos) {
    if(!inBounds(pos) || !hasTileAt(pos)) return new HashSet<>();

    Set<Tile> explored = new HashSet<>();
    Queue<Tile> queue = new LinkedList<>();
    queue.add(getTile(pos).get());

    this.exploreAccessibleTilesFromTile(explored, queue);

    // cannot traverse to starting point
    return explored;
  }

  @Override
  public int getBoardWidth() {
    return boardWidth;
  }

  @Override
  public int getBoardHeight() {
    return boardHeight;
  }

  @Override
  public Posn getPosOfTile(Tile tile) {
    for(int row = 0; row < boardHeight; row++) {
      for(int col = 0; col < boardWidth; col++) {
        Optional<Tile> boardTile = gameboard.get(row).get(col);
        if(boardTile.isPresent() && boardTile.get().equals(tile)) {
          return new Posn(col, row);
        }
      }
    }
    throw new IllegalArgumentException("Tile not found in board");
  }

  @Override
  public Board getCopy() {
    Board boardCopy = new RectBoard(boardWidth, boardHeight);
    for (int row = 0; row < boardHeight; row += 1) {
      for (int col = 0; col < boardWidth; col += 1) {
        Posn pos = new Posn(col, row);
        Optional<Tile> copy = getTile(new Posn(col, row));
        if(copy.isPresent()) {
          boardCopy.placeTileSafely(pos, copy.get().getCopy());
        }
      }
    }
    return boardCopy;
  }

  @Override
  public List<Integer> getMovableRowIndices() {
    List<Integer> rows = new ArrayList<>();
    for(int i = 0; i < boardHeight; i++) {
      if(isMovableRow.test(i)) {
        rows.add(i);
      }
    }
    return rows;
  }

  @Override
  public List<Integer> getMovableColIndices() {
    List<Integer> cols = new ArrayList<>();
    for (int col = 0; col < boardWidth; col++) {
      if (isMovableCol.test(col)) {
        cols.add(col);
      }
    }
    return cols;
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof RectBoard) {
      RectBoard other = (RectBoard) o;
      for(int row = 0; row < boardHeight; row++) {
        for(int col = 0; col < boardWidth; col++) {
          Posn pos = new Posn(col, row);
          if(!this.getTile(pos).equals(other.getTile(pos))) {
            return false;
          }
        }
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return this.gameboard.hashCode() * 27 + this.gems.hashCode() * 19;
  }

  // Helper Methods

  private boolean inBounds(Posn pos) {
    int x = pos.getX();
    int y = pos.getY();
    return x >= 0 && y >= 0 && x < boardWidth && y < boardHeight;
  }

  private void placeTileUnsafely(Posn pos, Tile tile) {
    Optional<Tile> opt = Optional.of(tile);
    this.gameboard.get(pos.getY()).set(pos.getX(), opt);
  }

  private Tile slideHorizontally(Posn removeAt, Posn addAt, Tile tile) {
    gameboard.get(addAt.getY()).add(addAt.getX(), Optional.of(tile));
    Optional<Tile> removedTile = gameboard.get(removeAt.getY()).remove(removeAt.getX());
    return removedTile.get();
  }

  private Tile slideVertically(Posn removePos, int endY, Tile tile) {
    Optional<Tile> removedTile = gameboard.get(removePos.getY()).get(removePos.getX());

    int rowIncrement = (endY - removePos.getY() > 0 ? 1 : -1);

    for(int row = removePos.getY(); Math.abs(endY - row) > 0; row += rowIncrement) {
      Optional<Tile> tileAbove = gameboard.get(row + rowIncrement).get(removePos.getX());
      gameboard.get(row).set(removePos.getX(), tileAbove);
    }

    gameboard.get(endY).set(removePos.getX(), Optional.of(tile));
    return removedTile.get();
  }

  private List<Tuple<Tile, Direction>> getNeighborsOfTileAt(Posn pos) {
    List<Tuple<Tile, Direction>> neighbors = new ArrayList<>();
    for(Direction dir : Direction.values()) {
      Posn neighborPos = Direction.offsetPosnWithDirection(dir, pos, 1);
      Optional<Tile> tile = getTile(neighborPos);
      if(tile.isPresent()) {
        neighbors.add(new Tuple<>(tile.get(), dir));
      }
    }
    return neighbors;
  }

  // Runs BFS over a Queue of Tiles.
  private void exploreAccessibleTilesFromTile(Set<Tile> explored, Queue<Tile> queue) {
    while(!queue.isEmpty()) {
      Tile currentTile = queue.remove();
      Posn currentPosn = this.getPosOfTile(currentTile);

      if (inBounds(currentPosn) && !explored.contains(currentTile)) {
        List<Tuple<Tile, Direction>> neighbors = this.getNeighborsOfTileAt(currentPosn);
        explored.add(currentTile);
        List<Tile> accessibleNeighbors = currentTile.getAccessibleNeighbors(neighbors);
        queue.addAll(accessibleNeighbors);
      }
    }
  }

}
