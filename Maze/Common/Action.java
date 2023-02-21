package model.state;

import java.util.List;
import java.util.Optional;
import model.board.Board;
import util.Direction;
import model.board.Tile;
import util.Tuple;

/**
 * Represents a player action that does the following:
 * - Rotates the spare tile (optional)
 * - slides a row or column
 * - inserts the spare tile into the empty space
 * - moves the player to a targeted position on the board
 *  OR
 *  Passes the turn
 */
public interface Action {

  /**
   * Checks whether this action is valid based on the given parameters.
   * @param board the board this action acts on
   * @param spare the spare tile used with this action
   * @param player the player that uses this action
   * @param previousBoardMove the previous board movement
   * @return a boolean of whether this action would be valid
   */
  boolean isValidActionOn(Board board, Tile spare, PlayerData player, Optional<Tuple<Integer, Direction>> previousBoardMove);

  /**
   * Applies this action to the board, spare, and players (Mutates all three Objects).
   * @param board the board this action acts on
   * @param spare the spare tile used with this action
   * @param players the spare tile used with this action
   * @return the new spare tile that is removed
   */
  Tile accept(Board board, Tile spare, List<PlayerData> players);

  /**
   * Returns an Optional of a tuple of the planned sliding move this action will take. The Integer
   * represents the index of the slide and the Direction represents which way the slide will happen.
   * If the action will not change the state of the board, an empty optional is returned.
   * @return the planned board change of this action
   */
  Optional<Tuple<Integer, Direction>> getPlannedBoardMove();

}
