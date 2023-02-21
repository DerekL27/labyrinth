package referee;

import java.util.Optional;
import model.board.Board;
import model.state.Action;
import model.state.PlayerStateWrapper;
import util.Posn;

/**
 * The Player API. All methods listed here can be called to inform a Player or to request
 * information from the Player.
 */
public interface Player {

  /**
   * Determines the Player's name.
   * @return the name of this Player
   */
  String name();

  /**
   * Requests the Player to return a board with the given minimum dimensions.
   * Note: the dimensions must be Natural numbers.
   * @param rows the minimum number of rows that board must have
   * @param columns the minimum number of columns that the board must have
   * @return the board that is proposed from the player
   * @throws IllegalStateException if the response if malformed
   */
  Board proposeBoard0(int rows, int columns) throws IllegalStateException;


  /**
   * This Player is provided with the player's personal initial view of the state of the game and a
   * Posn that marks the goal the player must visit next. If the given state is empty, this method
   * is used to tell the Player the targeted goal is home. Additionally, if the state is empty, the
   * goal Posn will be the player's home position.
   * @param state0 the initial state
   * @param goal the position of the goal Tile the player must reach
   * @throws IllegalStateException if the response if malformed
   */
  Object setup(Optional<PlayerStateWrapper> state0, Posn goal) throws IllegalStateException;

  /**
   * The Player is provided with the current player's personal view of the state of the game. By
   * this request, the Player must make an action for the given state for the game to proceed.
   *
   * @param s the state that is used for the player to make an action on
   * @return the action, either pass or turn, that is made by the player
   * @throws IllegalStateException if the response if malformed
   */
  Action takeTurn(PlayerStateWrapper s) throws IllegalStateException;

  /**
   * Notifies the player if they won.
   * @param won true if this player won, false if they lost
   * @throws IllegalStateException if the response if malformed
   */
  Object win(Boolean won) throws IllegalStateException;

}
