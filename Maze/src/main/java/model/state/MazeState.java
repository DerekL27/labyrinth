package model.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.board.Board;
import referee.Player;
import util.Direction;
import model.board.Tile;
import util.Posn;
import util.Tuple;
import util.Util;

/**
 * Represents a state of the game Labyrinth. This state is immutable.
 */
public class MazeState implements State {

  private final Board board;

  // The first PlayerData in the list refers to the player whose turn it is
  private final List<PlayerData> players;
  private final Tile spareTile;

  // represents the previous sliding action done by the action to produce this state
  // the Integer is the row/col index and the Direction is the direction of the slide
  private final Optional<Tuple<Integer, Direction>> previousMove;

  private final List<Posn> additionalGoals;


  public MazeState(Board board, List<PlayerData> players,
      Tile spareTile, Optional<Tuple<Integer, Direction>> previousMove, List<Posn> additionalGoals) {
    this.board = board;
    this.players = players;

    validateHomeandGoalTiles(players, board);
    validateAdditionalGoals(additionalGoals, board);

    this.spareTile = spareTile;
    this.previousMove = previousMove;
    this.additionalGoals = additionalGoals;
  }

  /**
   * Validates the home and goal tile information of the players passed into the state to ensure that they're on
   * immovable tiles and home tiles are unique
   * @param players the list of player information to validate
   * @param board the board to validate the players on
   */
  private void validateHomeandGoalTiles(List<PlayerData> players, Board board){
    //gets the number of distinct home location coordinates and compares it with the number of players
    if(players.stream().map(PlayerData::getHomeLocation).distinct().count() != players.size()){
      throw new IllegalStateException("Home tiles must be unique");
    }
    List<Integer> movableColIndices = board.getMovableColIndices();
    List<Integer> movableRowIndices = board.getMovableRowIndices();
    for(PlayerData playerData : players){
      if(movableColIndices.contains(playerData.getHomeLocation().getX()) ||
              movableRowIndices.contains(playerData.getHomeLocation().getY()) ||
              movableColIndices.contains(playerData.getGoalLocation().getX()) ||
              movableRowIndices.contains(playerData.getGoalLocation().getY())){
        throw new IllegalStateException("Home and goal tiles must be on immovable tiles");
      }
    }
  }

  /**
   * Validates the additional goals passed into the state to ensure that they're on immovable tiles
   * @param additionalGoals the list of additional goals to validate
   * @param board the board to validate the additional goals on
   */
  private void validateAdditionalGoals(List<Posn> additionalGoals, Board board){
    List<Integer> movableColIndices = board.getMovableColIndices();
    List<Integer> movableRowIndices = board.getMovableRowIndices();
    for(Posn posn : additionalGoals){
      if(movableColIndices.contains(posn.getX()) ||
              movableRowIndices.contains(posn.getY())){
        throw new IllegalStateException("Additional goals must be on immovable tiles");
      }
    }
  }

  @Override
  public PlayerData whichPlayerTurn() {
    if(players.isEmpty()) {
      throw new IllegalArgumentException("No players in the game!");
    }
    return this.players.get(0);
  }

  @Override
  public boolean canApplyAction(Action action) {
    return action.isValidActionOn(this.board, this.spareTile, whichPlayerTurn(), this.previousMove);
  }

  @Override
  public State applyActionWithoutChecking(Action action) {
    Board newBoard = this.board.getCopy();
    List<PlayerData> newPlayerList = Util.shallowCopyOf(this.players);
    Optional<Tuple<Integer, Direction>> newPrevMove;

    Optional<Tuple<Integer, Direction>> plannedMove = action.getPlannedBoardMove();
    if(plannedMove.isPresent()) {
      newPrevMove = plannedMove;
    }
    else {
      newPrevMove = previousMove;
    }

    Tile currentSpare = this.spareTile.getCopy();

    // action.accept(...) mutates the board, spare tile, and list of players
    Tile newSpare = action.accept(newBoard, currentSpare, newPlayerList, this.additionalGoals);

    //push current player to end of list
    newPlayerList.add(newPlayerList.remove(0));

    return new MazeState(newBoard, newPlayerList, newSpare, newPrevMove, this.additionalGoals);
  }

  @Override
  public boolean isGameOver() {
    return this.getPlayerFinishedGame().isPresent() || this.players.isEmpty();
  }

  @Override
  public Optional<PlayerData> getPlayerFinishedGame() {
    for(PlayerData player : this.players) {
      if(player.isPlayerOnGoal() && player.isPlayerOnHome()
       && player.getHasReachedFinalGoal() && player.getHasReturnedHome()) {
        return Optional.of(player);
      }
    }
    return Optional.empty();
  }

  @Override
  public State kickCurrentPlayer() {
    if (players.isEmpty()) {
      return this;
    }
    Board newBoard = this.board.getCopy();
    List<PlayerData> newPlayerList = Util.shallowCopyOf(this.players);
    Tile spare = this.spareTile.getCopy();

    newPlayerList.remove(0);

    return new MazeState(newBoard, newPlayerList, spare, previousMove, this.additionalGoals);
  }

  @Override
  public int getBoardWidth() {
    return board.getBoardWidth();
  }

  @Override
  public int getBoardHeight() {
    return board.getBoardHeight();
  }

  @Override
  public Board getBoard() { return board.getCopy(); }

  @Override
  public Tile getSpareTile() { return spareTile.getCopy(); }

  @Override
  public Optional<Tuple<Integer, Direction>> getPrevMove() {
    return this.previousMove;
  }

  @Override
  public List<PlayerData> getPlayers() {
    return new ArrayList<>(players);
  }

  @Override
  public void updatePlayerAPI(int index, Player player){
    this.players.set(index, this.players.get(index).updatePlayerAPI(player));
  }

  @Override
  public void addAdditionalGoals(List<Posn> additionalGoals) {
    validateAdditionalGoals(additionalGoals, this.board);
    this.additionalGoals.addAll(additionalGoals);
  }

  @Override
  public List<Posn> getAdditionalGoals() {
    return Util.shallowCopyOf(this.additionalGoals);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof MazeState) {
      MazeState other = (MazeState)o;
      return board.equals(other.board)
          && players.equals(other.players)
          && spareTile.equals(other.spareTile)
          && previousMove.equals(other.previousMove);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return board.hashCode()
        + players.hashCode()
        + spareTile.hashCode()
        + previousMove.hashCode();
  }
}
