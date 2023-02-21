package model.board;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import util.Direction;
import util.Tuple;

public class TestBasicDrawTile {

  @Test
  public void testTileConstructor() {
    EnumSet<Direction> dirs1 = EnumSet.of(Direction.DOWN, Direction.UP);
    EnumSet<Direction> dirs2 = EnumSet.noneOf(Direction.class);
    List<Gem> gems = Arrays.asList(Gem.APLITE, Gem.APATITE);

    Tile tile1 = new BasicTile(dirs1, gems);

    assertTrue(tile1.getTileDirections().equals(dirs1),
        "Constructor unable to instantiate correct list of tiles.");

    Tile tile2 = new BasicTile(dirs2, gems);

    assertTrue(tile2.getTileDirections().equals(dirs2),
        "Constructor unable to instantiate correct list of tiles.");
  }

  @Test
  public void testGetAccessibleNeighbors() {
    EnumSet<Direction> centerDirs = EnumSet.of(
        Direction.DOWN, Direction.LEFT
    );
    EnumSet<Direction> leftNeighborDirs = EnumSet.of(
        Direction.RIGHT, Direction.UP
    );
    EnumSet<Direction> downNeighborDirs =EnumSet.of(
        Direction.DOWN, Direction.LEFT
    );

    List<Gem> gems = new ArrayList<>(Arrays.asList(Gem.APLITE, Gem.APATITE));
    Tile centerTile = new BasicTile(centerDirs, gems);
    Tile leftTile = new BasicTile(leftNeighborDirs, gems);
    Tile downTile = new BasicTile(downNeighborDirs, gems);

    List<Tuple<Tile, Direction>> centerNeighbors = new ArrayList<>(Arrays.asList(
       new Tuple<Tile, Direction>(leftTile, Direction.LEFT),
       new Tuple<Tile, Direction>(downTile, Direction.DOWN)
    ));

    List<Tile> calculatedTiles = centerTile.getAccessibleNeighbors(centerNeighbors);

    List<Tile> expectedTiles = new ArrayList<>(Arrays.asList(
        leftTile
    ));

    assertTrue(calculatedTiles.size() == expectedTiles.size(), "size mismatch. "
        + "Incorrect number of accessible neighbor tiles returned.");

    for(Tile expected : expectedTiles) {
      assertTrue(calculatedTiles.contains(expected), "Output missing an expected tile.");
    }
  }

  @Test
  public void testDoesTilePointInDirection() {
    EnumSet<Direction> dirs = EnumSet.of(
        Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP
    );
    List<Gem> gems = new ArrayList<>(Arrays.asList(Gem.APATITE, Gem.APLITE));
    Tile tile = new BasicTile(dirs, gems);

    assertTrue(tile.doesTilePointInDirection(Direction.DOWN));
    assertTrue(tile.doesTilePointInDirection(Direction.UP));
    assertTrue(tile.doesTilePointInDirection(Direction.LEFT));
    assertTrue(tile.doesTilePointInDirection(Direction.RIGHT));

    dirs = EnumSet.noneOf(Direction.class);
    tile = new BasicTile(dirs, gems);

    assertFalse(tile.doesTilePointInDirection(Direction.DOWN));
    assertFalse(tile.doesTilePointInDirection(Direction.UP));
    assertFalse(tile.doesTilePointInDirection(Direction.LEFT));
    assertFalse(tile.doesTilePointInDirection(Direction.RIGHT));

    dirs = EnumSet.of(
        Direction.LEFT, Direction.DOWN
    );
    tile = new BasicTile(dirs, gems);

    assertTrue(tile.doesTilePointInDirection(Direction.DOWN));
    assertTrue(tile.doesTilePointInDirection(Direction.LEFT));
    assertFalse(tile.doesTilePointInDirection(Direction.UP));
    assertFalse(tile.doesTilePointInDirection(Direction.RIGHT));
  }

  @Test
  public void testRotateCounterClockwiseNTimes() {
    // Tests single rotation with no directions
    EnumSet<Direction> dirs1 = EnumSet.noneOf(Direction.class);
    EnumSet<Direction> expectedDirs1 = EnumSet.noneOf(Direction.class);
    List<Gem> gems = new ArrayList<>(Arrays.asList(Gem.APATITE, Gem.APLITE));

    Tile tile1 = new BasicTile(dirs1, gems);
    tile1.rotateCounterClockwiseNTimes(1);
    assertTrue(tile1.getTileDirections().equals(expectedDirs1));

    // Tests single rotation with all directions
    EnumSet<Direction> dirs2 = EnumSet.of(Direction.LEFT, Direction.UP, Direction.RIGHT, Direction.DOWN);
    EnumSet<Direction> expectedDirs2 = EnumSet.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT);
    Tile tile2 = new BasicTile(dirs2, gems);
    tile2.rotateCounterClockwiseNTimes(3);
    assertTrue(tile2.getTileDirections().equals(expectedDirs2));

    // Tests single rotation with some directions
    EnumSet<Direction> dirs3 = EnumSet.of(Direction.LEFT, Direction.UP, Direction.DOWN);
    EnumSet<Direction> expectedDirs3 = EnumSet.of(Direction.UP, Direction.RIGHT, Direction.LEFT);
    Tile tile3 = new BasicTile(dirs3, gems);
    tile3.rotateCounterClockwiseNTimes(3);
    assertTrue(tile3.getTileDirections().equals(expectedDirs3));

    // Tests multiple rotations with some directions
    EnumSet<Direction> dirs4 = EnumSet.of(Direction.LEFT, Direction.UP, Direction.DOWN);
    EnumSet<Direction> expectedDirs4 = EnumSet.of(Direction.RIGHT, Direction.DOWN, Direction.UP);
    Tile tile4 = new BasicTile(dirs4, gems);
    tile4.rotateCounterClockwiseNTimes(2);
    assertTrue(tile4.getTileDirections().equals(expectedDirs4));
  }

}
