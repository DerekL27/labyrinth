package model.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static referee.TestReferee.generateFullyConnectedBoardAndSpare;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import model.board.BasicTile;
import model.board.Board;
import model.board.Board7x7;
import model.strategy.RiemannStrategy;
import org.junit.jupiter.api.Disabled;
import referee.StrategyPlayer;
import util.Direction;
import model.board.Gem;
import model.board.Tile;
import org.junit.jupiter.api.Test;
import util.Posn;
import util.Tuple;

public class TestMazeState {

  @Test
  public void testWhichPlayerTurn() {
    Board board = new Board7x7();
    List<PlayerData> players = new ArrayList<>();
    PlayerData first = new PlayerData(new Posn(0, 0), new Posn(1, 1), Color.red, 0);
    players.add(first);
    players.add(new PlayerData(new Posn(0, 0), new Posn(3, 1), Color.blue, 0));
    players.add(new PlayerData(new Posn(0, 0), new Posn(1, 3), Color.green, 0));
    players.add(new PlayerData(new Posn(0, 0), new Posn(1, 5), Color.yellow, 0));

    Tile spare = new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.EMERALD, Gem.MAGNESITE));

    State state = new MazeState(board, players, spare, Optional.empty(), new ArrayList<>());

    assertEquals(first, state.whichPlayerTurn());

    players = new ArrayList<>();
    State state2 = new MazeState(board, players,
        spare, Optional.empty(), new ArrayList<>());
    assertThrows(IllegalArgumentException.class, () -> state2.whichPlayerTurn());
  }

  @Test
  public void testCanApplyAction() {
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

    PlayerData player = new PlayerData(startPlayerPos, new Posn(1, 1), Color.blue, 0);

    Tile spareTile = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.CHROME_DIOPSIDE, Gem.EMERALD));

    State state = new MazeState(board, Arrays.asList(player),  spareTile, Optional.empty(), new ArrayList<>());

    Board boardCopy = board.getCopy();

    assertTrue(state.canApplyAction(action));
    assertEquals(boardCopy, board);
  }

  @Disabled
  @Test
  //Test is disabled because we split up the check for whether an action can be applied and actually applying it into
  //two separate methods. This test is now redundant.
  //TODO: Change this test to test whether or not the specified move is valid (it's not)
  public void testApplyActionSafelyFalse() {
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

    PlayerData player = new PlayerData(startPlayerPos, new Posn(1, 1), Color.blue, 0);

    Tile spareTile = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.CHROME_DIOPSIDE, Gem.EMERALD));

    State state = new MazeState(board, Arrays.asList(player), spareTile, Optional.empty(), new ArrayList<>());

    assertEquals(state, state.applyActionWithoutChecking(action));
  }

  @Test
  public void testApplyActionSafely() {

    Posn slidePos = new Posn(0,0);
    Posn startPlayerPos = new Posn(0, 0);
    Posn targetPos = new Posn(1, 1);

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
                EnumSet.of(Direction.UP, Direction.LEFT, Direction.RIGHT),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    PlayerData player = new PlayerData(startPlayerPos, targetPos, Color.blue, 0);

    Tile spareTile = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.CHROME_DIOPSIDE, Gem.EMERALD));

    List<PlayerData> players = new ArrayList<>(Arrays.asList(player));

    State state = new MazeState(board, players,  spareTile, Optional.empty(), new ArrayList<>());
    State nextState = state.applyActionWithoutChecking(action);

    assertNotEquals(state, nextState);
  }

  @Test
  public void testIsGameOver() {

    Board board = new Board7x7();
    List<PlayerData> players = new ArrayList<>();
    PlayerData first = new PlayerData(Color.ORANGE, new Posn(0, 0), new Posn(3, 1), 0, false);
    players.add(first);
    players.add(new PlayerData(Color.BLUE, new Posn(1, 1), new Posn(1, 1), new Posn(1,1), Optional.empty(), 1, true, false, true));
    players.add(new PlayerData(Color.RED,  new Posn(0, 2), new Posn(1, 5), 0, false));
    players.add(new PlayerData(Color.GREEN,  new Posn(0, 3), new Posn(1, 3), 0, false));

    for(int row = 0; row < board.getBoardHeight(); row++) {
      for(int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(EnumSet.of(Direction.LEFT, Direction.RIGHT),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    Tile spare = new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.EMERALD, Gem.MAGNESITE));



    // game over on no players left in the state
    State state = new MazeState(board, new ArrayList<>(), spare, Optional.empty(), new ArrayList<>());
    assertTrue(state.isGameOver());

    // Game Not over
    state = new MazeState(board, players,  spare, Optional.empty(), new ArrayList<>());
    assertFalse(state.isGameOver());

    //update state by having the first player pass their turn
    state = state.applyActionWithoutChecking(new PassAction());
    assertFalse(state.isGameOver());

    Action action = BasicTurnAction.builder()
        .targetPlayerPosition(new Posn(1, 1))
        .slideTilePosition(new Posn(0, 2))
        .slideTileDirection(Direction.RIGHT)
        .build();
    State nextState = state.applyActionWithoutChecking(action);
    assertNotEquals(state, nextState);
    assertTrue(nextState.isGameOver());
  }

  @Test
  public void testGetWinner() {
    Board board = new Board7x7();
    List<PlayerData> players = new ArrayList<>();
    PlayerData first = new PlayerData(Color.ORANGE,  new Posn(0, 1), new Posn(3, 1), new Posn(3,1), Optional.empty(), 1, true, false, true);
    players.add(first);
    players.add(new PlayerData(Color.BLUE,  new Posn(0, 1), new Posn(1, 1), 1, true));
    players.add(new PlayerData(Color.RED,  new Posn(0, 2), new Posn(1, 5), 0, false));
    players.add(new PlayerData(Color.GREEN,  new Posn(0, 3), new Posn(1, 3), 0, false));

    for(int row = 0; row < board.getBoardHeight(); row++) {
      for(int col = 0; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(EnumSet.of(Direction.LEFT, Direction.RIGHT),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    Tile spare = new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.EMERALD, Gem.MAGNESITE));

    State state = new MazeState(board, players, spare, Optional.empty(), new ArrayList<>());

    Action action = BasicTurnAction.builder()
        .targetPlayerPosition(new Posn(3, 1))
        .slideTilePosition(new Posn(0, 2))
        .slideTileDirection(Direction.RIGHT)
        .build();
    State nextState = state.applyActionWithoutChecking(action);
    assertNotEquals(state, nextState);
    assertTrue(nextState.isGameOver());

    Optional<PlayerData> winner = nextState.getPlayerFinishedGame();
    assertTrue(winner.isPresent());
    assertEquals(first.getAvatar(), winner.get().getAvatar());
  }

  @Test
  public void testKickCurrentPlayer() {
    Board board = new Board7x7();

    List<PlayerData> players = new ArrayList<>();

    PlayerData first = new PlayerData(Color.ORANGE, new Posn(0, 0), new Posn(1, 3), 0, false);
    players.add(first);
    PlayerData second = new PlayerData(Color.BLUE, new Posn(0, 1), new Posn(1, 1), 0, false);
    players.add(second);
    players.add(new PlayerData(Color.RED, new Posn(0, 2), new Posn(1, 5), 0, false));
    players.add(new PlayerData(Color.GREEN, new Posn(0, 3), new Posn(3, 3), 0, false));

    Tile spare = new BasicTile(EnumSet.noneOf(Direction.class), Arrays.asList(Gem.EMERALD, Gem.MAGNESITE));

    State state = new MazeState(board, players, spare, Optional.empty(), new ArrayList<>());
    state = state.kickCurrentPlayer();
    assertEquals(second, state.whichPlayerTurn());
    state = state.kickCurrentPlayer();
    state = state.kickCurrentPlayer();
    state = state.kickCurrentPlayer();
    assertEquals(state, state.kickCurrentPlayer());
  }

  public static State stateWithFullyConnectedBoard() {
    Tuple<Board, Tile> boardAndSpare = generateFullyConnectedBoardAndSpare(7,7);
    Board board = boardAndSpare.getFirst();
    Tile spare = boardAndSpare.getSecond();
    List<PlayerData> players = new ArrayList<>();
    List<Posn> additionalGoals = new ArrayList<>();

    players.add(new PlayerData(Color.ORANGE,  new Posn(0, 1), new Posn(3, 3), new Posn(3,1), Optional.of(new StrategyPlayer("ebob", new RiemannStrategy())), 1, false, false, true));
    players.add(new PlayerData(Color.BLUE,  new Posn(0, 1), new Posn(1, 1), new Posn(3,1), Optional.of(new StrategyPlayer("ederek", new RiemannStrategy())), 12, false, false, true));
    players.add(new PlayerData(Color.RED,  new Posn(0, 2), new Posn(1, 5), new Posn(3,1), Optional.of(new StrategyPlayer("eben", new RiemannStrategy())), 8, false, false, true));
    players.add(new PlayerData(Color.GREEN,  new Posn(0, 3), new Posn(1, 3), new Posn(3,1), Optional.of(new StrategyPlayer("esam", new RiemannStrategy())), 5, false, false, true));

    return new MazeState(board, players, spare, Optional.empty(), additionalGoals);
  }

  @Test
  public void testUpdatingAdditionalGoalsList() {

    // initial state has no additionalGoals
    State state = stateWithFullyConnectedBoard();
    assertEquals(0, state.getAdditionalGoals().size());

    // additionalGoals is populated via addAdditionalGoals
    List<Posn> additionalGoals = new ArrayList<>();
    Posn newGoal = new Posn(1,1);
    additionalGoals.add(newGoal);
    state.addAdditionalGoals(additionalGoals);
    assertEquals(1, state.getAdditionalGoals().size());

    Posn slidePos = new Posn(0,0);
    Posn targetPos = new Posn(3, 1);
    Action action = BasicTurnAction.builder()
            .rotateSpare(0)
            .slideTilePosition(slidePos)
            .slideTileDirection(Direction.DOWN)
            .targetPlayerPosition(targetPos)
            .build();

    // first item in additionalGoals is popped to provide the goal-reaching Player with a new goal
    State nextState = state.applyActionWithoutChecking(action);
    assertEquals(0, nextState.getAdditionalGoals().size());

    // Player's new goal location is properly updated
    PlayerData updatedPlayer = nextState.getPlayers().get(3);
    assertEquals(2, updatedPlayer.getNumGoalsVisited());
    assertEquals(newGoal.hashCode(), updatedPlayer.getGoalLocation().hashCode());
  }
}
