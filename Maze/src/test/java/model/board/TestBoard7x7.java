package model.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import util.Direction;
import util.Posn;

public class TestBoard7x7 {

  @Test
  public void testBoardConstructor() {
    Board board = new Board7x7();

    for(int row = 0; row < board.getBoardHeight(); row++) {
      for(int col = 0; col < board.getBoardWidth(); col++) {
        Posn tileLocation = new Posn(col, row);
        assertFalse(board.hasTileAt(tileLocation));
      }
    }
  }

  @Test
  public void testPlaceTileSafely() {
    Board board = new Board7x7();

    Posn tilePos = new Posn(0, 0);
    EnumSet<Direction> dirs = EnumSet.noneOf(Direction.class);
    Tile tile = new BasicTile(dirs, Arrays.asList(Gem.APATITE, Gem.APLITE));

    assertTrue(board.placeTileSafely(tilePos, tile));

    Optional<Tile> tileInBoard = board.getTile(tilePos);
    assertTrue(tileInBoard.isPresent());
    assertEquals(tileInBoard.get(), tile);

    Tile other = new BasicTile(
        EnumSet.of(Direction.LEFT),
        Arrays.asList(Gem.APATITE, Gem.APLITE));

    assertFalse(board.placeTileSafely(new Posn(0, 1), other));

    board = new Board7x7();
    tilePos = new Posn(-10, 0);
    assertFalse(board.placeTileSafely(tilePos, tile));
    assertFalse(board.hasTileAt(tilePos));
  }

  @Test
  public void testGetTile() {
    Board board = new Board7x7();
    assertEquals(Optional.empty(), board.getTile(new Posn(9, 8)));
    Tile tile = new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.ALEXANDRITE_PEAR_SHAPE, Gem.APLITE));
    board.placeTileSafely(new Posn(1, 0), tile);
    Optional<Tile> fromBoard = board.getTile(new Posn(1, 0));
    assertTrue(fromBoard.isPresent());
    assertTrue(tile.equals(fromBoard.get()));
  }

  @Test
  public void testHasTileAt() {
    Board board = new Board7x7();
    assertFalse(board.hasTileAt(new Posn(0, 0)));

    board.placeTileSafely(new Posn(0, 0),
        new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.APATITE, Gem.APLITE)));
    assertTrue(board.hasTileAt(new Posn(0, 0)));

    board.placeTileSafely(new Posn(5, 6),
        new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.ALEXANDRITE_PEAR_SHAPE, Gem.APLITE)));
    assertTrue(board.hasTileAt(new Posn(5, 6)));
  }

  @Test
  public void testIsBoardBuilt() {
    // Tests method with a board with no tiles filled
    Board board1 = new Board7x7();
    assertFalse(board1.isBoardBuilt());

    // Tests method with all tiles filled
    for (int row = 0; row < board1.getBoardHeight(); row++) {
      for (int col = 0; col < board1.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board1.getBoardHeight()) + col);
        board1.placeTileSafely(new Posn(col, row),
            new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }
    assertTrue(board1.isBoardBuilt());
    // Tests method with some tiles filled
    Board board2 = new Board7x7();
    // Tests method with some tiles filled
    for (int row = 0; row < board1.getBoardHeight() - 3; row++) {
      for (int col = 0; col < board1.getBoardWidth() - 2; col++) {
        int gemIndex = Gem.values().length - (1 + (row * board2.getBoardHeight()) + col);
        board2.placeTileSafely(new Posn(col, row),
            new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }
    assertFalse(board2.isBoardBuilt());
  }

  @Test
  public void testSlideSafelyRight() {
    // one has posn that can't slide
    // slide each direction
    Board board = new Board7x7();

    Tile first = new BasicTile(
        EnumSet.of(Direction.UP, Direction.LEFT),
        Arrays.asList(Gem.APRICOT_SQUARE_RADIANT, Gem.AQUAMARINE));

    Tile second = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.LEFT),
        Arrays.asList(Gem.ALEXANDRITE_PEAR_SHAPE, Gem.ALEXANDRITE));


    Tile third = new BasicTile(
        EnumSet.of(Direction.RIGHT, Direction.LEFT),
        Arrays.asList(Gem.AMETRINE, Gem.AMMOLITE));

    Tile fourth = new BasicTile(
        EnumSet.of(Direction.UP, Direction.DOWN),
        Arrays.asList(Gem.APATITE, Gem.APLITE));

    Tile fifth = new BasicTile(
        EnumSet.of(Direction.UP, Direction.RIGHT),
        Arrays.asList(Gem.AUSTRALIAN_MARQUISE, Gem.AVENTURINE)
    );

    Tile sixth = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.ALMANDINE_GARNET, Gem.AMETHYST)
    );

    Tile seventh = new BasicTile(
        EnumSet.of(Direction.UP, Direction.LEFT, Direction.RIGHT),
        Arrays.asList(Gem.AZURITE, Gem.BERYL)
    );

    List<Tile> sevenTiles = new ArrayList<>(Arrays.asList(
        second,
        sixth,
        third,
        fourth,
        first,
        fifth,
        seventh
    ));

    for(int col = 0; col < board.getBoardWidth(); col++) {
      board.placeTileSafely(new Posn(col, 0), sevenTiles.get(col));
    }

    //fill board
    for(int row = 1; row < board.getBoardHeight(); row++) {
      for(int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }
    Tile tileToAdd = new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.ZIRCON, Gem.ZOISITE));

    // test slide RIGHT, invalid posn (odd y)
    Posn invalidSlide = new Posn(4, 1);
    assertEquals(tileToAdd, board.slideSafely(invalidSlide, Direction.RIGHT, tileToAdd));

    // test slide RIGHT, top row
    Posn posToSlideAt = new Posn(6, 0);
    List<Tile> expectedTilesFromBoard = new ArrayList<>();
    for(Tile tile : sevenTiles) {
      expectedTilesFromBoard.add(tile);
    }

    Tile tileRemoved = board.slideSafely(posToSlideAt, Direction.RIGHT, tileToAdd);

    assertEquals(seventh, tileRemoved);

    expectedTilesFromBoard.remove(6);
    expectedTilesFromBoard.add(0, tileToAdd);

    for(int col = 0; col < board.getBoardWidth(); col++) {
      Optional<Tile> tileInBoard = board.getTile(new Posn(col, 0));
      assertTrue(tileInBoard.isPresent());
      assertEquals(expectedTilesFromBoard.get(col).getTileDirections(), tileInBoard.get().getTileDirections());
    }
  }

  @Test
  public void testSlideSafelyLeft() {
    // one has posn that can't slide
    // slide each direction
    Board board = new Board7x7();

    Tile first = new BasicTile(
        EnumSet.of(Direction.UP, Direction.LEFT),
        Arrays.asList(Gem.APRICOT_SQUARE_RADIANT, Gem.AQUAMARINE));

    Tile second = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.LEFT),
        Arrays.asList(Gem.ALEXANDRITE_PEAR_SHAPE, Gem.ALEXANDRITE));


    Tile third = new BasicTile(
        EnumSet.of(Direction.RIGHT, Direction.LEFT),
        Arrays.asList(Gem.AMETRINE, Gem.AMMOLITE));

    Tile fourth = new BasicTile(
        EnumSet.of(Direction.UP, Direction.DOWN),
        Arrays.asList(Gem.APATITE, Gem.APLITE));

    Tile fifth = new BasicTile(
        EnumSet.of(Direction.UP, Direction.RIGHT),
        Arrays.asList(Gem.AUSTRALIAN_MARQUISE, Gem.AVENTURINE)
    );

    Tile sixth = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.ALMANDINE_GARNET, Gem.AMETHYST)
    );

    Tile seventh = new BasicTile(
        EnumSet.of(Direction.UP, Direction.LEFT, Direction.RIGHT),
        Arrays.asList(Gem.AZURITE, Gem.BERYL)
    );

    List<Tile> sevenTiles = new ArrayList<>(Arrays.asList(
        second,
        sixth,
        third,
        fourth,
        first,
        fifth,
        seventh
    ));

    for(int col = 0; col < board.getBoardWidth(); col++) {
      board.placeTileSafely(new Posn(col, 0), sevenTiles.get(col));
    }

    //fill board
    for(int row = 1; row < board.getBoardHeight(); row++) {
      for(int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }
    Tile tileToAdd = new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.ZIRCON, Gem.ZOISITE));
    // test slide Left, invalid posn (odd y)
    Posn invalidSlide = new Posn(4, 1);
    assertEquals(tileToAdd, board.slideSafely(invalidSlide, Direction.RIGHT, tileToAdd));

    // test slide LEFT, top row
    Posn posToSlideAt = new Posn(2, 0);
    List<Tile> expectedTilesFromBoard = new ArrayList<>();
    for(Tile tile : sevenTiles) {
      expectedTilesFromBoard.add(tile);
    }

    Tile tileRemoved = board.slideSafely(posToSlideAt, Direction.LEFT, tileToAdd);

    assertEquals(second, tileRemoved);

    expectedTilesFromBoard.remove(0);
    expectedTilesFromBoard.add(tileToAdd);

    for(int col = 0; col < board.getBoardWidth(); col++) {
      Optional<Tile> tileInBoard = board.getTile(new Posn(col, 0));
      assertTrue(tileInBoard.isPresent());
      assertEquals(expectedTilesFromBoard.get(col).getTileDirections(), tileInBoard.get().getTileDirections());
    }
  }

  @Test
  public void testSlideSafelyUp() {
    Board board = new Board7x7();

    Tile first = new BasicTile(
        EnumSet.of(Direction.UP, Direction.LEFT),
        Arrays.asList(Gem.APRICOT_SQUARE_RADIANT, Gem.AQUAMARINE));

    Tile second = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.LEFT),
        Arrays.asList(Gem.ALEXANDRITE_PEAR_SHAPE, Gem.ALEXANDRITE));


    Tile third = new BasicTile(
        EnumSet.of(Direction.RIGHT, Direction.LEFT),
        Arrays.asList(Gem.AMETRINE, Gem.AMMOLITE));

    Tile fourth = new BasicTile(
        EnumSet.of(Direction.UP, Direction.DOWN),
        Arrays.asList(Gem.APATITE, Gem.APLITE));

    Tile fifth = new BasicTile(
        EnumSet.of(Direction.UP, Direction.RIGHT),
        Arrays.asList(Gem.AUSTRALIAN_MARQUISE, Gem.AVENTURINE)
    );

    Tile sixth = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.ALMANDINE_GARNET, Gem.AMETHYST)
    );

    Tile seventh = new BasicTile(
        EnumSet.of(Direction.UP, Direction.LEFT, Direction.RIGHT),
        Arrays.asList(Gem.AZURITE, Gem.BERYL)
    );

    List<Tile> sevenTiles = new ArrayList<>(Arrays.asList(
        second,
        sixth,
        third,
        fourth,
        first,
        fifth,
        seventh
    ));

    //fill first col with special tiles
    for(int row = 0; row < board.getBoardHeight(); row++) {
      board.placeTileSafely(new Posn(0, row), sevenTiles.get(row));
    }

    //fill board
    for(int row = 0; row < board.getBoardHeight(); row++) {
      for(int col = 1; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    Tile tileToAdd = new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.ZIRCON, Gem.ZOISITE));


    // test slide UP, top row
    Posn posToSlideAt = new Posn(0, 3);
    List<Tile> expectedTilesFromBoard = new ArrayList<>();
    for(Tile tile : sevenTiles) {
      expectedTilesFromBoard.add(tile);
    }

    // test incorrect posn slide location
    assertEquals(tileToAdd, board.slideSafely(new Posn(3, 2), Direction.UP, tileToAdd));

    Tile tileRemoved = board.slideSafely(posToSlideAt, Direction.UP, tileToAdd);

    assertEquals(second, tileRemoved);

    expectedTilesFromBoard.remove(0);
    expectedTilesFromBoard.add(tileToAdd);

    for(int row = 0; row < board.getBoardHeight(); row++) {
      Optional<Tile> tileInBoard = board.getTile(new Posn(0, row));
      assertTrue(tileInBoard.isPresent());
      assertEquals(expectedTilesFromBoard.get(row).getTileDirections(), tileInBoard.get().getTileDirections());
    }
  }

  @Test
  public void testSlideSafelyDown() {
    Board board = new Board7x7();

    Tile first = new BasicTile(
        EnumSet.of(Direction.UP, Direction.LEFT),
        Arrays.asList(Gem.APRICOT_SQUARE_RADIANT, Gem.AQUAMARINE));

    Tile second = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.LEFT),
        Arrays.asList(Gem.ALEXANDRITE_PEAR_SHAPE, Gem.ALEXANDRITE));


    Tile third = new BasicTile(
        EnumSet.of(Direction.RIGHT, Direction.LEFT),
        Arrays.asList(Gem.AMETRINE, Gem.AMMOLITE));

    Tile fourth = new BasicTile(
        EnumSet.of(Direction.UP, Direction.DOWN),
        Arrays.asList(Gem.APATITE, Gem.APLITE));

    Tile fifth = new BasicTile(
        EnumSet.of(Direction.UP, Direction.RIGHT),
        Arrays.asList(Gem.AUSTRALIAN_MARQUISE, Gem.AVENTURINE)
    );

    Tile sixth = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.ALMANDINE_GARNET, Gem.AMETHYST)
    );

    Tile seventh = new BasicTile(
        EnumSet.of(Direction.UP, Direction.LEFT, Direction.RIGHT),
        Arrays.asList(Gem.AZURITE, Gem.BERYL)
    );

    List<Tile> sevenTiles = new ArrayList<>(Arrays.asList(
        second,
        sixth,
        third,
        fourth,
        first,
        fifth,
        seventh
    ));

    //fill first col with special tiles
    for(int row = 0; row < board.getBoardHeight(); row++) {
      board.placeTileSafely(new Posn(0, row), sevenTiles.get(row));
    }

    //fill board
    for(int row = 0; row < board.getBoardHeight(); row++) {
      for(int col = 1; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        List<Gem> gems = Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1]);
        board.placeTileSafely(new Posn(col, row), new BasicTile(EnumSet.noneOf(Direction.class), gems));
      }
    }


    // test slide LEFT, top row
    Posn posToSlideAt = new Posn(0, 6);
    Tile tileToAdd = new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.PERIDOT, Gem.LAPIS_LAZULI));
    List<Tile> expectedTilesFromBoard = new ArrayList<>();
    for(Tile tile : sevenTiles) {
      expectedTilesFromBoard.add(tile);
    }

    // test incorrect posn slide location
    assertEquals(tileToAdd, board.slideSafely(new Posn(1, 2), Direction.DOWN, tileToAdd));

    Tile tileRemoved = board.slideSafely(posToSlideAt, Direction.DOWN, tileToAdd);

    assertEquals(seventh, tileRemoved);

    expectedTilesFromBoard.remove(6);
    expectedTilesFromBoard.add(0, tileToAdd);

    for(int row = 0; row < board.getBoardHeight(); row++) {
      Optional<Tile> tileInBoard = board.getTile(new Posn(0, row));
      assertTrue(tileInBoard.isPresent());
      assertEquals(expectedTilesFromBoard.get(row).getTileDirections(), tileInBoard.get().getTileDirections());
    }
  }

  @Test
  public void testCanSlide() {
    Board board = new Board7x7();
    // Board is not built
    assertFalse(board.canSlide(new Posn(0, 0), Direction.DOWN));

    for (int row = 0; row < board.getBoardHeight(); row++) {
      for (int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }
    // Out of bonds
    assertFalse(board.canSlide(new Posn(9, 12), Direction.DOWN));
    // Odd column
    assertFalse(board.canSlide(new Posn(1, 0), Direction.UP));
    // Odd row
    assertFalse(board.canSlide(new Posn(1, 3), Direction.RIGHT));
    // Even column
    assertTrue(board.canSlide(new Posn(0, 0), Direction.DOWN));
    // Even row
    assertTrue(board.canSlide(new Posn(0, 2), Direction.LEFT));
  }

  @Test
  public void testFindAllAccessibleTiles() {
    Board board = new Board7x7();

    // board space from (3, 2) to (4, 3) looks like:
    //    ┌│
    //    └─
    Posn startPos = new Posn(4, 3);
    EnumSet<Direction> dirs = EnumSet.of(
      Direction.RIGHT, Direction.LEFT
    );

    Tile tile4x3 = new BasicTile(dirs, Arrays.asList(Gem.APATITE, Gem.APLITE));
    board.placeTileSafely(startPos, tile4x3);

    Posn pos = new Posn(3, 3);
    dirs = EnumSet.of(
        Direction.RIGHT, Direction.UP
    );
    Tile tile3x3 = new BasicTile(dirs, Arrays.asList(Gem.APATITE, Gem.ALEXANDRITE));
    board.placeTileSafely(pos, tile3x3);

    pos = new Posn(3, 2);
    dirs = EnumSet.of(
        Direction.RIGHT, Direction.DOWN
    );
    Tile tile3x2 = new BasicTile(dirs, Arrays.asList(Gem.APATITE, Gem.ALEXANDRITE_PEAR_SHAPE));
    board.placeTileSafely(pos, tile3x2);

    pos = new Posn(4, 2);
    dirs = EnumSet.of(
        Direction.DOWN, Direction.UP
    );
    Tile tile4x2 = new BasicTile(dirs, Arrays.asList(Gem.APATITE, Gem.ALMANDINE_GARNET));
    board.placeTileSafely(pos, tile4x2);

    Set<Tile> accessibleTiles = board.findAllAccessibleTiles(startPos);
    accessibleTiles.remove(board.getTile(startPos).get());

    assertEquals(2, accessibleTiles.size());

    assertTrue(accessibleTiles.contains(tile3x3));
    assertTrue(accessibleTiles.contains(tile3x2));
    assertFalse(accessibleTiles.contains(tile4x2));
    assertFalse(accessibleTiles.contains(tile4x3));
  }
}
