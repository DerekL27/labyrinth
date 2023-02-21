package model.state;

import java.util.List;
import java.util.Optional;
import model.board.Board;
import util.Direction;
import model.board.Tile;
import util.Tuple;

/**
 * Action used by a player to pass their turn. This does not affect the board at all.
 */
public class PassAction implements Action {

  @Override
  public boolean isValidActionOn(Board board, Tile spare, PlayerData player, Optional<Tuple<Integer, Direction>> previousBoardMove) {
    return true;
  }

  @Override
  public Tile accept(Board board, Tile spare, List<PlayerData> player) {
    return spare;
  }

  @Override
  public Optional<Tuple<Integer, Direction>> getPlannedBoardMove() {
    return Optional.empty();
  }
}
