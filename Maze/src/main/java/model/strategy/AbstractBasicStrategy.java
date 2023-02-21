package model.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import util.Direction;
import model.state.Action;
import model.state.BasicTurnAction;
import model.state.BasicTurnAction.BasicTurnActionBuilder;
import model.state.PassAction;
import model.state.PlayerStateWrapper;
import util.Posn;

/**
 * An abstract implementation of the Strategy interface. This Strategy does the following steps to
 * make an action based on the given state of the game and the target to reach:
 * - if the current player is already on the desired goal, return a pass action
 * - finds all candidate tiles to try to reach
 * - processes the candidate tiles in order of the comparator returned by classes that extend this
 *   AbstractBasicStrategy
 * - checks all move actions for each candidate
 * - returns the first valid action
 * - if no valid move action is found, the strategy must pass
 */
public abstract class AbstractBasicStrategy implements Strategy {

  @Override
  public Action makeAction(PlayerStateWrapper state, Posn target) {

    List<Posn> candidates = getCandidates(state);

    candidates.sort(getCandidatesComparator(target));

    candidates.remove(target);
    candidates.add(0, target);

    for(Posn candidate : candidates) {
      Optional<Action> possibleCandidate = getFirstPossibleActionToGoal(state, candidate);
      if(possibleCandidate.isPresent()) {
        return possibleCandidate.get();
      }
    }

    return new PassAction();
  }

  /**
   * Method used to return the Comparator which is used to  order the Positions of candidates in
   * the order that each implementation of this class sees fit.
   * @param target the target posn to reach
   * @return the ordered list of candidates
   */
  protected abstract Comparator<Posn> getCandidatesComparator(Posn target);

  /**
   * Returns the first action that can reach the goal position based on the given state for the current player
   * @param state the game state to check whether the goal is reachable
   * @param goal the goal position to check if it is reachable
   * @return an action if possible to get the current player to the goal
   */
  private Optional<Action> getFirstPossibleActionToGoal(PlayerStateWrapper state, Posn goal) {

    List<Posn> rows = getRows(state.getBoardHeight());
    List<Posn> cols = getCols(state.getBoardWidth());
    List<Direction> horizontal = Arrays.asList(Direction.LEFT, Direction.RIGHT);
    List<Direction> vertical = Arrays.asList(Direction.UP, Direction.DOWN);
    List<Integer> rotations = Arrays.asList(0, 1, 2, 3);

    Optional<Action> action = getActionHelper(state, rows, horizontal, rotations, goal);
    if (action.isPresent()) {
      return action;
    }

    action = getActionHelper(state, cols, vertical, rotations, goal);
    return action;
  }


  /**
   * Returns the first action that can reach the goal position based on the given possible slides of the state for the current player
   * @param state the game state that is used to make sure the action can be applied
   * @param slidePosns all possible positions that a slide can happen
   * @param directions the directions that the slide occurs
   * @param rotations the rotations of the spare tile
   * @param goal the position of the goal tile for the current player to reach
   * @return an action if possible to get the current player to the goal
   */
  private Optional<Action> getActionHelper(PlayerStateWrapper state, List<Posn> slidePosns,
      List<Direction> directions, List<Integer> rotations, Posn goal) {

    BasicTurnActionBuilder builder = BasicTurnAction.builder().targetPlayerPosition(goal);

    for(Posn slidePosn : slidePosns) {
      for(Direction dir : directions) {
        for(int rot : rotations) {
          builder.slideTilePosition(slidePosn);
          builder.slideTileDirection(dir);
          builder.rotateSpare(rot);
          Action action = builder.build();
          if(state.canApplyAction(action)) {
            return Optional.of(action);
          }
        }
      }
    }
    return Optional.empty();
  }


  /**
   * Generates the list of candidates which is a list of all possible positions on the board
   *
   * @param state the game state
   * @return a list of all possible positions that can be reached
   */
  private List<Posn> getCandidates(PlayerStateWrapper state) {
    List<Posn> candidates = new ArrayList<>();

    int width = state.getBoardWidth();
    int height = state.getBoardHeight();
    for(int row = 0; row < height; row++) {
      for(int col = 0; col < width; col++) {
        candidates.add(new Posn(col, row));
      }
    }
    return candidates;
  }

  /**
   * Generates a list of all possible rows represented by positions with the given height
   *
   * @param height the height of the board
   * @return a list of positions, each representing a single row
   */
  private List<Posn> getRows(int height) {
    List<Posn> rows = new ArrayList<>();
    for(int i = 0; i < height; i++) {
      rows.add(new Posn(0, i));
    }
    return rows;
  }

  /**
   * Generates a list of all possible columns represented by positions with the given height
   *
   * @param width the width of the board
   * @return a list of positions, each representing a single column
   */
  private List<Posn> getCols(int width) {
    List<Posn> cols = new ArrayList<>();
    for(int i = 0; i < width; i++) {
      cols.add(new Posn(i, 0));
    }
    return cols;
  }

}
