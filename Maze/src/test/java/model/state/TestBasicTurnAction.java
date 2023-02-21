package model.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import model.board.BasicTile;
import model.board.Board;
import model.board.Board7x7;
import util.Direction;
import model.board.Gem;
import model.board.Tile;
import org.junit.jupiter.api.Test;
import util.Posn;
import util.Tuple;

public class TestBasicTurnAction {

  @Test
  public void testIsValidTrue() {
    Posn slidePos = new Posn(0,0);
    Posn startPlayerPos = new Posn(0, 0);
    Posn targetPos = new Posn(1, 0);

    Action action = BasicTurnAction.builder()
        .rotateSpare(0)
        .slideTilePosition(slidePos)
        .slideTileDirection(Direction.DOWN)
        .targetPlayerPosition(targetPos)
        .build();

    Board board = new Board7x7();

    for (int row = 0; row < board.getBoardHeight(); row++) {
      for (int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(
                EnumSet.of(Direction.UP, Direction.LEFT),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    PlayerData player = new PlayerData(startPlayerPos, new Posn(1, 1), Color.red, 0);

    Tile spareTile = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.CHROME_DIOPSIDE, Gem.EMERALD));

    assertTrue(action.isValidActionOn(board, spareTile, player, Optional.empty()));
  }

  @Test
  public void testIsValidPlayerOnNewSpareTile() {
    Posn slidePos = new Posn(0,0);
    Posn startPlayerPos = new Posn(0, 6);
    Posn targetPos = new Posn(1, 0);

    Action action = BasicTurnAction.builder()
        .rotateSpare(0)
        .slideTilePosition(slidePos)
        .slideTileDirection(Direction.DOWN)
        .targetPlayerPosition(targetPos)
        .build();

    Board board = new Board7x7();

    for (int row = 0; row < board.getBoardHeight(); row++) {
      for (int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(
                EnumSet.of(Direction.UP, Direction.LEFT),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    PlayerData player = new PlayerData(startPlayerPos, new Posn(1, 1), Color.red, 0);

    Tile spareTile = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.CHROME_DIOPSIDE, Gem.EMERALD));

    assertTrue(action.isValidActionOn(board, spareTile, player, Optional.empty()));
  }

  @Test
  public void testIsValidFalsePlayerOnNewSpareTile() {
    Posn slidePos = new Posn(0,0);
    Posn startPlayerPos = new Posn(0, 0);
    Posn targetPos = new Posn(1, 0);

    Action action = BasicTurnAction.builder()
        .rotateSpare(0)
        .slideTilePosition(slidePos)
        .slideTileDirection(Direction.UP)
        .targetPlayerPosition(targetPos)
        .build();

    Board board = new Board7x7();

    for (int row = 0; row < board.getBoardHeight(); row++) {
      for (int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(
                EnumSet.of(Direction.UP, Direction.LEFT),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    PlayerData player = new PlayerData(startPlayerPos, new Posn(1, 1), Color.red, 0);

    Tile spareTile = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.CHROME_DIOPSIDE, Gem.EMERALD));

    assertFalse(action.isValidActionOn(board, spareTile, player, Optional.empty()));
  }

  @Test
  public void testIsValidFalseCannotSlide() {
    Posn slidePos = new Posn(1,0);
    Posn startPlayerPos = new Posn(0, 0);
    Posn targetPos = new Posn(1, 0);

    Action action = BasicTurnAction.builder()
        .rotateSpare(0)
        .slideTilePosition(slidePos)
        .slideTileDirection(Direction.UP)
        .targetPlayerPosition(targetPos)
        .build();

    Board board = new Board7x7();

    for (int row = 0; row < board.getBoardHeight(); row++) {
      for (int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(
                EnumSet.of(Direction.UP, Direction.LEFT),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    PlayerData player = new PlayerData(startPlayerPos, new Posn(1, 1), Color.red, 0);

    Tile spareTile = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.CHROME_DIOPSIDE, Gem.EMERALD));

    assertFalse(action.isValidActionOn(board, spareTile, player, Optional.empty()));
  }

  @Test
  public void testIsValidFalseRevertsToPrevBoardState() {
    Posn slidePos = new Posn(0,0);
    Posn startPlayerPos = new Posn(0, 0);
    Posn targetPos = new Posn(1, 0);

    Action action = BasicTurnAction.builder()
        .rotateSpare(0)
        .slideTilePosition(slidePos)
        .slideTileDirection(Direction.UP)
        .targetPlayerPosition(targetPos)
        .build();

    Board board = new Board7x7();

    for (int row = 0; row < board.getBoardHeight(); row++) {
      for (int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(
                EnumSet.of(Direction.UP, Direction.LEFT),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    PlayerData player = new PlayerData(startPlayerPos, new Posn(1, 1), Color.red, 0);

    Tile spareTile = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.CHROME_DIOPSIDE, Gem.EMERALD));

    Tuple<Integer, Direction> previousBoardMove = new Tuple<>(0, Direction.DOWN);

    assertFalse(action.isValidActionOn(board, spareTile, player, Optional.of(previousBoardMove)));
  }

  @Test
  public void testIsValidFalsePlayerCannotMoveToTarget() {
    Posn slidePos = new Posn(0,0);
    Posn startPlayerPos = new Posn(0, 0);
    Posn targetPos = new Posn(4, 4);

    Action action = BasicTurnAction.builder()
        .rotateSpare(0)
        .slideTilePosition(slidePos)
        .slideTileDirection(Direction.UP)
        .targetPlayerPosition(targetPos)
        .build();

    Board board = new Board7x7();

    for (int row = 0; row < board.getBoardHeight(); row++) {
      for (int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(
                EnumSet.of(Direction.UP, Direction.LEFT),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    PlayerData player = new PlayerData(startPlayerPos, new Posn(1, 1), Color.red, 0);

    Tile spareTile = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.CHROME_DIOPSIDE, Gem.EMERALD));

    assertFalse(action.isValidActionOn(board, spareTile, player, Optional.empty()));
  }

  @Test
  public void testAccept() {
    Posn slidePos = new Posn(0,0);
    Posn startPlayerPos = new Posn(0, 0);
    Posn targetPos = new Posn(1, 0);

    Action action = BasicTurnAction.builder()
        .rotateSpare(0)
        .slideTilePosition(slidePos)
        .slideTileDirection(Direction.DOWN)
        .targetPlayerPosition(targetPos)
        .build();

    Board board = new Board7x7();

    for (int row = 0; row < board.getBoardHeight(); row++) {
      for (int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(
                EnumSet.of(Direction.UP, Direction.LEFT),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    PlayerData player = new PlayerData(startPlayerPos, new Posn(1, 1), Color.red, 0);

    Tile spareTile = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.CHROME_DIOPSIDE, Gem.EMERALD));


    Board expectedBoard = board.getCopy();
    expectedBoard.slideSafely(slidePos, Direction.DOWN, spareTile);

    BasicTile expectedSpare = new BasicTile(
        EnumSet.of(Direction.LEFT, Direction.UP),
        Arrays.asList(Gem.MORGANITE_OVAL, Gem.MOONSTONE));

    List<PlayerData> playerList = new ArrayList<>();
    playerList.add(player);
    Tile newSpare = action.accept(board, spareTile, playerList, new ArrayList<>());

    assertEquals(expectedBoard, board);
    assertEquals(expectedSpare, newSpare);
    assertEquals(new Posn(1, 0), playerList.get(0).getCurrentLocation());
  }

  @Test
  public void testAcceptPlayerOnNewSpareTile() {
    Posn slidePos = new Posn(0,0);
    Posn startPlayerPos = new Posn(0, 6);
    Posn targetPos = new Posn(1, 0);

    Action action = BasicTurnAction.builder()
        .rotateSpare(0)
        .slideTilePosition(slidePos)
        .slideTileDirection(Direction.DOWN)
        .targetPlayerPosition(targetPos)
        .build();

    Board board = new Board7x7();

    for (int row = 0; row < board.getBoardHeight(); row++) {
      for (int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(
                EnumSet.of(Direction.UP, Direction.LEFT),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    PlayerData player = new PlayerData(startPlayerPos, new Posn(1, 1), Color.red, 0);

    Tile spareTile = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.CHROME_DIOPSIDE, Gem.EMERALD));

    assertTrue(action.isValidActionOn(board, spareTile, player, Optional.empty()));

    List<PlayerData> playerList = new ArrayList<>();
    playerList.add(player);

    action.accept(board, spareTile, playerList, new ArrayList<>());

    Posn playerNewPos = playerList.get(0).getCurrentLocation();
    assertEquals(new Posn(1, 0), playerNewPos);
  }

}
