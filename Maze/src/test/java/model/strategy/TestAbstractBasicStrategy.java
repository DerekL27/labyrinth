package model.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.*;

import model.board.BasicTile;
import model.board.Board;
import model.board.Board7x7;
import util.Direction;
import model.board.Gem;
import model.board.Tile;
import model.state.Action;
import model.state.BasicTurnAction;
import model.state.MazeState;
import model.state.PlayerData;
import model.state.PlayerStateWrapper;
import model.state.State;
import org.junit.jupiter.api.Test;
import util.Posn;
import util.Tuple;

public class TestAbstractBasicStrategy {

  public static State buildState() {

    Board board = getBoard();
    List<PlayerData> players = getPlayers();
    Tile spare = new BasicTile(
        EnumSet.of(Direction.DOWN, Direction.RIGHT),
        Arrays.asList(Gem.FANCY_SPINEL_MARQUISE, Gem.YELLOW_BAGUETTE));
    Optional<Tuple<Integer, Direction>> prevMove = Optional.empty();

    /*
    The entire board is full this connector tile: ┘
    The spare tile is this connector: ┌
    */

    return new MazeState(board, players, spare, prevMove, new ArrayList<>());
  }

  // These two tests should have the same action outcome
  @Test
  public void testMakeActionMovePlayerLocationRiemman() {
    // the optimal move works when the player is moved by a slide
    Strategy strategy = new RiemannStrategy();
    testMakeActionMovePlayerLocation(strategy);
  }

  @Test
  public void testMakeActionMovePlayerLocationEuclid() {
    // the optimal move works when the player is moved by a slide
    Strategy strategy = new EuclidStrategy();
    testMakeActionMovePlayerLocation(strategy);
  }

  private void testMakeActionMovePlayerLocation(Strategy strategy) {
    // optimal move is for current layer to slide a tile on
    // their own column to get to the target position
    State state = buildState();
    Action action = strategy.makeAction(new PlayerStateWrapper(state, state.whichPlayerTurn()), new Posn(0, 1));

    Optional<Tuple<Integer, Direction>> plannedActionMove = action.getPlannedBoardMove();
    assertTrue(plannedActionMove.isPresent());

    // the planned move should be to slide the 0 index column down
    assertEquals(0, (int) plannedActionMove.get().getFirst());
    assertEquals(Direction.RIGHT, plannedActionMove.get().getSecond());

    assertTrue(action instanceof BasicTurnAction);
    BasicTurnAction basicTurnAction = (BasicTurnAction) action;
    assertEquals(0, basicTurnAction.getRotateAmt());
    assertEquals(new Posn(0, 1), basicTurnAction.getTargetPos());
  }



  @Test
  public void testMakeActionPlayerWrapsAround() {
    Strategy strategy = new RiemannStrategy();

    // use the second player in the list
    State state = buildState().kickCurrentPlayer();

    Action action = strategy.makeAction(new PlayerStateWrapper(state, state.whichPlayerTurn()), new Posn(3, 0));

    Optional<Tuple<Integer, Direction>> plannedActionMove = action.getPlannedBoardMove();
    assertTrue(plannedActionMove.isPresent());

    // the planned move should be to slide the 2 index column down
    assertEquals(2, (int) plannedActionMove.get().getFirst());
    assertEquals(Direction.DOWN, plannedActionMove.get().getSecond());

    assertTrue(action instanceof BasicTurnAction);
    BasicTurnAction basicTurnAction = (BasicTurnAction) action;
    assertEquals(0, basicTurnAction.getRotateAmt());
    assertEquals(new Posn(3, 0), basicTurnAction.getTargetPos());
  }

  @Test
  public void testMakeActionRotatePieceToGetPath() {

    Board board = getBoard();
    List<PlayerData> players = getPlayers();
    Tile spare = new BasicTile(
        EnumSet.of(Direction.UP, Direction.LEFT),
        Arrays.asList(Gem.PERIDOT, Gem.LAPIS_LAZULI)
    );

    State state = new MazeState(board, players,  spare, Optional.empty(), new ArrayList<>());
    state = state.kickCurrentPlayer(); // go to second player at Posn(2, 6)

    Strategy strategy = new EuclidStrategy();

    Posn target = new Posn(3, 0);

    Action action = strategy.makeAction(new PlayerStateWrapper(state, state.whichPlayerTurn()), target);

    Optional<Tuple<Integer, Direction>> plannedActionMove = action.getPlannedBoardMove();
    assertTrue(plannedActionMove.isPresent());

    // the planned move should be to slide the 2 index column down
    assertEquals(2, (int) plannedActionMove.get().getFirst());
    assertEquals(Direction.DOWN, plannedActionMove.get().getSecond());

    assertTrue(action instanceof BasicTurnAction);
    BasicTurnAction basicTurnAction = (BasicTurnAction) action;
    assertEquals(2, basicTurnAction.getRotateAmt());
    assertEquals(target, basicTurnAction.getTargetPos());
  }


  @Test
  public void testMakeActionGetClosestToTile() {

    Board board = getBoard();
    List<PlayerData> players = getPlayers();
    Tile spare = new BasicTile(
        EnumSet.of(Direction.UP, Direction.LEFT),
        Arrays.asList(Gem.PERIDOT, Gem.LAPIS_LAZULI)
    );

    State state = new MazeState(board, players, spare, Optional.empty(), new ArrayList<>());
    state = state.kickCurrentPlayer(); // go to second player at Posn(2, 6)

    Strategy strategy = new EuclidStrategy();

    Posn target = new Posn(3, 1);

    Action action = strategy.makeAction(new PlayerStateWrapper(state, state.whichPlayerTurn()), target);

    Optional<Tuple<Integer, Direction>> plannedActionMove = action.getPlannedBoardMove();
    assertTrue(plannedActionMove.isPresent());

    // the planned move should be to slide the 2 index column down
    assertEquals(2, (int) plannedActionMove.get().getFirst());
    assertEquals(Direction.DOWN, plannedActionMove.get().getSecond());

    assertTrue(action instanceof BasicTurnAction);
    BasicTurnAction basicTurnAction = (BasicTurnAction) action;
    assertEquals(2, basicTurnAction.getRotateAmt());
    assertEquals(new Posn(3, 0), basicTurnAction.getTargetPos());

  }

  @Test
  public void testMakeActionRiemannVsEuclidSameState() {

    Board board = new Board7x7();

    for (int row = 0; row < board.getBoardHeight(); row++) {
      int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()));
      board.placeTileSafely(new Posn(0, row),
          new BasicTile(
              EnumSet.of(Direction.UP, Direction.DOWN),
              Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
    }

    for (int row = 0; row < board.getBoardHeight(); row++) {
      int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + 1);
      board.placeTileSafely(new Posn(1, row),
          new BasicTile(
              EnumSet.of(Direction.LEFT, Direction.RIGHT),
              Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
    }

    for (int row = 0; row < board.getBoardHeight(); row++) {
      for (int col = 2; col < board.getBoardWidth(); col++) {
        int gemIndex = Gem.values().length - (1 + (row * board.getBoardHeight()) + col);
        board.placeTileSafely(new Posn(col, row),
            new BasicTile(
                EnumSet.of(Direction.UP, Direction.DOWN),
                Arrays.asList(Gem.values()[gemIndex], Gem.values()[gemIndex - 1])));
      }
    }

    List<PlayerData> players = getPlayers();
    Tile spare = new BasicTile(
        EnumSet.of(Direction.UP, Direction.LEFT),
        Arrays.asList(Gem.PERIDOT, Gem.LAPIS_LAZULI)
    );

    State state = new MazeState(board, players,  spare, Optional.empty(), new ArrayList<>());
    // player at 0, 0
    // target at 1, 6
    Posn target = new Posn(1, 5);

    Strategy riemann = new RiemannStrategy();
    Strategy euclid = new EuclidStrategy();

    Action riemannAction = riemann.makeAction(new PlayerStateWrapper(state, state.whichPlayerTurn()), target);
    Action euclidAction = euclid.makeAction(new PlayerStateWrapper(state, state.whichPlayerTurn()), target);

    // riemann can't get to the target so pick the first valid action
    assertEquals(0, (int) riemannAction.getPlannedBoardMove().get().getFirst());
    assertEquals(Direction.UP, riemannAction.getPlannedBoardMove().get().getSecond());

    assertTrue(riemannAction instanceof BasicTurnAction);
    BasicTurnAction basicTurnAction = (BasicTurnAction) riemannAction;
    assertEquals(0, basicTurnAction.getRotateAmt());
    assertEquals(new Posn(0, 0), basicTurnAction.getTargetPos()); //player gets pushed down by the slide to access the top left tile


    // euclid can't get to the target so pick the action that brings the player closest to the target
    assertEquals(6, (int) euclidAction.getPlannedBoardMove().get().getFirst()); // row 6
    assertEquals(Direction.LEFT, euclidAction.getPlannedBoardMove().get().getSecond());

    assertTrue(euclidAction instanceof BasicTurnAction);
    basicTurnAction = (BasicTurnAction) euclidAction;
    assertEquals(0, basicTurnAction.getRotateAmt());
    assertEquals(new Posn(0, 5), basicTurnAction.getTargetPos());
  }



  private static List<PlayerData> getPlayers() {
    List<PlayerData> players = Arrays.asList(
        new PlayerData(Color.red, new Posn(0, 0), new Posn(1, 1), 0, false),
        new PlayerData(Color.blue, new Posn(2, 6), new Posn(1, 3), 0, false),
        new PlayerData(Color.green, new Posn(5, 0), new Posn(1, 5), 0, false)
    );
    return players;
  }

  private static Board getBoard() {
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
    return board;
  }

}
