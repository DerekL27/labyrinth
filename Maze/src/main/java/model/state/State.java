package model.state;

import java.util.List;
import java.util.Optional;
import model.board.Board;
import referee.Player;
import util.Direction;
import model.board.Tile;
import util.Posn;
import util.Tuple;

/**
 * Represents a state of the game of Labyrinth.
 */
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
  State applyActionWithoutChecking(Action action);

  /**
   * Determines if the game is over,
   * @return if the game is over
   */
  boolean isGameOver();

  /**
   * Returns which player has finished the game. If the game is not over, and empty Optional is returned.
   * @return the player that has reached their last goal and returned home
   */
  Optional<PlayerData> getPlayerFinishedGame();

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

  /**
   * Add additional goals to the State's goal list.
   * @param additionalGoals the goals to add
   */
  void addAdditionalGoals(List<Posn> additionalGoals);

  /**
   * Get a shallow copy of the list of additional goals in this State.
   */
  List<Posn> getAdditionalGoals();

  /**
   * Update the specified player's player API, used for testing.
   * @param index the index of the player to update
   * @param player the new player API
   */
  void updatePlayerAPI(int index, Player player);

}
