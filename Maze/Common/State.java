package model.state;

import java.util.List;
import java.util.Optional;
import model.board.Board;
import model.board.Direction;
import model.board.Tile;
import util.Tuple;

public interface State {

  /**
   * Determines which player's turn it is.
   * @return the player who plays
   */
  PlayerData whichPlayerTurn();

  /**
   * checks that the player doing the action is the player with the current turn.
   * Checks that the action is valid with Action.isValid(...).
   * @param action the action to apply on this state
   * @return the state resulting in the action applied to this state
   */
  boolean canApplyAction(Action action);

  /**
   * Returns a new game state is the outcome of applying the given action to this current state.
   * @param action the action to apply
   * @return the new game state
   */
  State applyActionSafely(Action action);

  /**
   * Determines if the game is over,
   * @return if the game is over
   */
  boolean isGameOver();

  /**
   * Returns which player has won the game. If the game is not over, and empty Optional is returned.
   * @return the winner of te game
   */
  Optional<PlayerData> getWinner();

  /**
   * Removes the current player from the game by returning a new game state that is identical to
   * this current state but the player list does not have the current player.
   * @return the new game state
   */
  State kickCurrentPlayer();


  int getBoardWidth();

  int getBoardHeight();

  Board getBoard();

  Tile getSpareTile();

  Optional<Tuple<Integer, Direction>> getPrevMove();

  /**
   * Get a shallow copy of the list of PlayerData in this State.
   * @return a list of the players in this game
   */
  List<PlayerData> getPlayers();
}
