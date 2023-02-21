package observer;

import model.state.State;

/**
 * This is a State observer interface. Any class that implements this interface can be notified of
 * a change in game state and when the game is over.
 */
public interface Observer {

  /**
   * This Observer is notified that a new game state was created.
   * @param state the latest State in the Referee's game
   */
  void notifyStateChange(State state);

  /**
   * The Observer is notified that the game has finished and that the Observer will not receive
   * additional States.
   */
  void notifyGameOver();

}
