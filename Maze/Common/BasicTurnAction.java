package model.state;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import model.board.Board;
import util.Direction;
import model.board.Tile;
import util.Posn;
import util.Tuple;

/**
 * Represents a player action that does the following:
 * - Rotates the spare tile (optional)
 * - slides a row or column
 * - inserts the spare tile into the empty space
 * - moves the player to a targeted position on the board
 */
public class BasicTurnAction implements Action {

  private final Posn pos;
  private final Direction dir;
  private final int rotateAmt;
  private final Posn targetPos;

  private BasicTurnAction(Posn pos, Direction dir, int rotateAmt, Posn targetPos) {
    this.pos = pos;
    this.dir = dir;
    this.rotateAmt = rotateAmt;
    this.targetPos = targetPos;
  }

  @Override
  public boolean isValidActionOn(Board board, Tile spare, PlayerData player, Optional<Tuple<Integer, Direction>> previousBoardMove) {
    Tile spareCopy = spare.getCopy();
    Board boardCopy = board.getCopy();

    if(!boardCopy.canSlide(pos, dir) || willUndoPreviousMove(previousBoardMove)) {
      return false;
    }

    spareCopy.rotateCounterClockwiseNTimes(rotateAmt);
    boardCopy.slideSafely(pos, dir, spareCopy);

    Posn playerCurrentLocation = player.getCurrentLocation();
    Posn prevLocOfTileMovedOffBoard = this.getBoundaryPosnsOfSlide(pos, dir, board).getFirst();

    if(playerCurrentLocation.equals(prevLocOfTileMovedOffBoard)) {
      playerCurrentLocation = boardCopy.getPosOfTile(spareCopy);
    }
    else {
      player = player.updateCurrentLocationIfOnSlide(getPlannedBoardMove(), board.getBoardWidth(), board.getBoardHeight());
      playerCurrentLocation = player.getCurrentLocation();
    }

    Set<Tile> accessible = boardCopy.findAllAccessibleTiles(playerCurrentLocation);
    Optional<Tile> targetTile = boardCopy.getTile(targetPos);

    return targetTile.isPresent()
        && !playerCurrentLocation.equals(targetPos)
        && accessible.contains(targetTile.get());
  }



  @Override
  public Tile accept(Board board, Tile spare, List<PlayerData> players) {
    spare.rotateCounterClockwiseNTimes(rotateAmt);

    Tile newSpare = board.slideSafely(pos, dir, spare);

    updatePlayersAccordingToSlide(players, board);

    PlayerData currentPlayer = players.get(0);
    currentPlayer = currentPlayer.updateCurrentLocation(targetPos);
    currentPlayer = currentPlayer.updateIfOnHomeAfterReachingGoal();
    currentPlayer = currentPlayer.updateIfVisitingTargetedGemPairTile();
    players.set(0, currentPlayer);

    return newSpare;
  }



  @Override
  public Optional<Tuple<Integer, Direction>> getPlannedBoardMove() {
    if(this.dir == Direction.LEFT || this.dir == Direction.RIGHT) {
      return Optional.of(new Tuple<>(this.pos.getY(), this.dir));
    }
    return Optional.of(new Tuple<>(this.pos.getX(), this.dir));
  }

  public int getRotateAmt() {
    return rotateAmt;
  }

  public Posn getTargetPos() {
    return targetPos;
  }


  // updates all players who were located on the tile that was slid off the board (newSpare) with
  // a new location at the tile that was just added to the board (spare)
  private void updatePlayersAccordingToSlide(List<PlayerData> players, Board board) {
    for(int i = 0; i < players.size(); i++) {
      players.set(i, players.get(i).updateCurrentLocationIfOnSlide(getPlannedBoardMove(),
          board.getBoardWidth(), board.getBoardHeight()));
    }
  }

  /**
   * Returns a tuple of posns that point to the boundaries of a slide.
   * The first value is the position of the tile that was removed from the sliding action.
   * The second value is the position of the tile that was added to the board.
   * @param posOfSlide the position of the slide
   * @param dirOfSlide the direction of the slide
   * @param board the board the slide happened on
   * @return a tuple of positions
   */
  private Tuple<Posn, Posn> getBoundaryPosnsOfSlide(Posn posOfSlide, Direction dirOfSlide, Board board) {
    switch(dirOfSlide) {
      case UP:
        return new Tuple<>(new Posn(posOfSlide.getX(), 0),
            new Posn(posOfSlide.getX(), board.getBoardHeight() - 1));
      case DOWN:
        return new Tuple<>(new Posn(posOfSlide.getX(), board.getBoardHeight() - 1),
            new Posn(posOfSlide.getX(), 0));
      case LEFT:
        return new Tuple<>(new Posn(0, posOfSlide.getY()),
            new Posn(board.getBoardWidth() - 1, posOfSlide.getY()));
      case RIGHT:
        return new Tuple<>(new Posn(board.getBoardWidth() - 1, posOfSlide.getY()),
            new Posn(0, posOfSlide.getY()));
      default:
        throw new IllegalArgumentException("Illegal Direction given.");
    }
  }

  /**
   * Checks if this action will undo the previous move.
   * @param prevMove the previous move as a tuple containing a rol/col index and the direction of
   *                 the slide
   * @return if this action will undo the previous move
   */
  private boolean willUndoPreviousMove(Optional<Tuple<Integer, Direction>> prevMove) {

    if(prevMove.isEmpty()) return false;

    Tuple<Integer, Direction> currentPlan = getPlannedBoardMove().get();
    Direction oppositeDirectionFromCurrent = Direction.getNClockwiseRotations(
        currentPlan.getSecond(), Direction.values().length / 2);

    return currentPlan.getFirst() == prevMove.get().getFirst()
        && oppositeDirectionFromCurrent == prevMove.get().getSecond();
  }



  /**
   * Used to create a BasicTurnAction.
   * @return a new instance of the builder class to construct a new BasicTurnAction
   */
  public static BasicTurnActionBuilder builder() {
    return new BasicTurnActionBuilder();
  }

  /**
   * Builder class to construct a BasicTurnAction in multiple steps.
   */
  public static class BasicTurnActionBuilder {
    private int rotateAmt;
    private Optional<Posn> tilePosToSlide;
    private Optional<Direction> dirToSlide;
    private Optional<Posn> targetPosToMovePlayer;

    public BasicTurnActionBuilder() {
      this.rotateAmt = 0;
      this.tilePosToSlide = Optional.empty();
      this.dirToSlide = Optional.empty();
      this.targetPosToMovePlayer = Optional.empty();
    }


    public BasicTurnActionBuilder rotateSpare(int rotateAmt) {
      this.rotateAmt = rotateAmt;
      return this;
    }

    public BasicTurnActionBuilder slideTilePosition(Posn tilePosToSlide) {
      this.tilePosToSlide = Optional.of(tilePosToSlide);
      return this;
    }

    public BasicTurnActionBuilder slideTileDirection(Direction dirToSlide) {
      this.dirToSlide = Optional.of(dirToSlide);
      return this;
    }

    public BasicTurnActionBuilder targetPlayerPosition(Posn targetPosToMovePlayer) {
      this.targetPosToMovePlayer = Optional.of(targetPosToMovePlayer);
      return this;
    }

    /**
     * Builds this BasicTurnAction using the data inside this builder class.
     * @return a built BasicTurnAction
     * @throws IllegalArgumentException if not all the fields for a BasicTurnAction are set
     */
    public BasicTurnAction build() throws IllegalArgumentException {
      if(!canBuild()) {
        throw new IllegalArgumentException("Cannot build Action: Incomplete Setup");
      }
      return new BasicTurnAction(
          this.tilePosToSlide.get(),
          this.dirToSlide.get(),
          this.rotateAmt,
          this.targetPosToMovePlayer.get());
    }

    /**
     * Determines if all necessary values of this action have been added.
     * @return if this Action can be built
     */
    public boolean canBuild() {
      return tilePosToSlide.isPresent()
          && dirToSlide.isPresent()
          && targetPosToMovePlayer.isPresent();
    }


  }
}
