package referee;

import java.util.Optional;
import json.PlayerAPIJson.BadFM;
import model.board.Board;
import model.state.Action;
import model.state.PlayerStateWrapper;
import model.strategy.Strategy;
import util.Posn;


/**
 * Represents a player that throws an exception on a specified action.
 */
public class BadPlayer implements Player {

  private final String name;
  private final Strategy strategy;
  private final BadFM badFM;
  private final int countBeforeInfiniteLoop;
  private int badFMCallCount;
  private Posn goal;

  public BadPlayer(String name, Strategy strategy, BadFM badFM, int countBeforeInfiniteLoop) {
    this.name = name;
    this.strategy = strategy;
    this.badFM = badFM;
    this.countBeforeInfiniteLoop = countBeforeInfiniteLoop;
    this.badFMCallCount = 0;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public Board proposeBoard0(int rows, int columns) {
    throw new UnsupportedOperationException("This operation is not implemented yet.");
  }

  @Override
  public Object setup(Optional<PlayerStateWrapper> state0, Posn goal) {
    if(this.badFM == BadFM.setUp) {
      doBadLogic();
    }
    this.goal = goal;
    return goal;
  }

  @Override
  public Action takeTurn(PlayerStateWrapper s) {
    if(this.badFM == BadFM.takeTurn) {
      doBadLogic();
    }
    return this.strategy.makeAction(s, goal);
  }

  @Override
  public Object win(Boolean won) {
    if(badFM == BadFM.win) {
      doBadLogic();
    }
    return won;
  }

  /**
   * If the count before the infinite loop is zero, behave as needed for the BadPlayer 1
   * harness where a divide by zero exception is thrown. If the count required is > 0, then increase
   * count. If the count is then equal to the required amount, go into an infinte loop.
   */
  private void doBadLogic() {
    if(countBeforeInfiniteLoop <= 0) {
      int x = 1 / 0;
    }
    else {
      badFMCallCount++;
      if(badFMCallCount == countBeforeInfiniteLoop) {
        while(true) {
          //infinite loop
        }
      }
    }
  }
}
