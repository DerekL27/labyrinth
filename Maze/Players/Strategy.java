package model.strategy;

import model.state.Action;
import model.state.PlayerStateWrapper;
import util.Posn;

/**
 * A Strategy computes an Action based on a given state and goal position
 */
public interface Strategy {

  /**
   * Based on the given state of the game, compute an action for the current player of the state
   * with the goal of reaching the given target tile position.
   * @param state the game state to compute the action on
   * @param target the tile position the player wants to get to
   * @return an action with the intent to move the player closer to its target position
   */
  Action makeAction(PlayerStateWrapper state, Posn target);

}
