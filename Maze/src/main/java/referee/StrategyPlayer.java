package referee;

import java.util.Optional;
import model.board.Board;
import model.state.Action;
import model.state.PlayerStateWrapper;
import model.strategy.Strategy;
import util.Posn;

/**
 * Player class that chooses turns via a given strategy.
 */
public class StrategyPlayer implements Player {

  private final String name;
  private final Strategy strategy;
  private Posn goal;

  public StrategyPlayer(String name, Strategy strategy) {
    this.name = name;
    this.strategy = strategy;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public Board proposeBoard0(int rows, int columns) {
    throw new UnsupportedOperationException("Behavior not supported at the moment.");
  }

  @Override
  public Object setup(Optional<PlayerStateWrapper> state0, Posn goal) {
    this.goal = goal;
    return goal;
  }

  @Override
  public Action takeTurn(PlayerStateWrapper s) {
    return this.strategy.makeAction(s, goal);
  }

  @Override
  public Object win(Boolean won) {
    return won;
  }
}
