package model.state;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import model.board.Board;
import model.board.Tile;
import util.Direction;
import util.Tuple;

/**
 * This is a wrapper over the State to hide certain methods and information from the current player.
 * This class only provides enough information for the current player to make an informed action on
 * the current State of the game.
 */
public class PlayerStateWrapper {

  private final State state;
  private final PlayerData currentPlayer;

  public PlayerStateWrapper(State state, PlayerData currentPlayer) {
    this.state = state;
    this.currentPlayer = currentPlayer;
  }

  public boolean canApplyAction(Action action) {
    return state.whichPlayerTurn().equals(this.currentPlayer) && this.state.canApplyAction(action);
  }

  public List<PlayerDataWrapper> getPlayerPublicInfo() {
    List<PlayerData> players = state.getPlayers();

    return players.stream()
        .map(PlayerDataWrapper::new)
        .collect(Collectors.toList());
   }

   public Board getBoard() {
    return state.getBoard();
   }

   public Tile getSpare() {
    return state.getSpareTile();
   }

   public int getBoardWidth() {
    return state.getBoardWidth();
   }

   public int getBoardHeight() {
    return state.getBoardHeight();
   }

   public Optional<Tuple<Integer, Direction>> getPrevMove() {
    return this.state.getPrevMove();
   }
}
