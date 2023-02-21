package model.state;

import java.awt.Color;
import java.util.Optional;
import model.board.Direction;
import model.board.Board;
import referee.Player;
import referee.SafePlayer;
import util.Posn;
import util.Tuple;

public class PlayerData {

  private final Color avatar;

  private final Posn currentLocation;

  private final Posn homeLocation;

  private final Posn gemPairLocation;

  private final boolean visitedTargetedGemPairTile;

  private final Optional<SafePlayer> playerAPI;

  // This constructor is used by unit tests
  public PlayerData(Color avatar, Posn currentLocation,
      Posn homeLocation, boolean visitedTargetedGemPairTile) {
    this(avatar, currentLocation, homeLocation, new Posn(-1, -1),
        visitedTargetedGemPairTile, Optional.empty());
  }

  // This constructor is used by the testing harness
  public PlayerData(Posn currentLocation, Posn homeLocation, Color avatar) {
    this(avatar, currentLocation, homeLocation, new Posn(-1, -1),
        false, Optional.empty());
  }

  // This constructor is used by the Referee and this class to create a full Player
  public PlayerData(Color avatar, Posn currentLocation,
      Posn homeLocation, Posn gemPairLocation, boolean visitedTargetedGemPairTile, Optional<Player> playerAPI) {
    this.avatar = avatar;
    this.visitedTargetedGemPairTile = visitedTargetedGemPairTile;
    this.currentLocation = currentLocation;
    this.homeLocation = homeLocation;
    this.gemPairLocation = gemPairLocation;
    this.playerAPI = playerAPI.map(SafePlayer::new);
  }

  public Color getAvatar() {
    return avatar;
  }

  public Posn getCurrentLocation() {
    return currentLocation;
  }

  public PlayerData updateCurrentLocation(Posn currentLocation) {
    return new PlayerData(this.avatar, currentLocation,
        this.homeLocation, this.gemPairLocation, this.visitedTargetedGemPairTile, this.playerAPI.map(SafePlayer::getPlayer));
  }

  public Posn getHomeLocation() {
    return homeLocation;
  }

  public PlayerData updateHomeLocation(Posn homeLocation) {
    return new PlayerData(this.avatar, this.currentLocation,
        homeLocation, this.gemPairLocation, this.visitedTargetedGemPairTile, this.playerAPI.map(SafePlayer::getPlayer));
  }

  public boolean isVisitedTargetedGemPairTile() {
    return visitedTargetedGemPairTile;
  }

  public PlayerData updateVisitedTargetedGemTile(boolean visitedTargetedGemTile) {
    return new PlayerData(this.avatar, this.currentLocation,
        this.homeLocation, this.gemPairLocation, visitedTargetedGemTile, this.playerAPI.map(SafePlayer::getPlayer));
  }

  public PlayerData updateIfVisitingTargetedGemPairTile(Board board) {
    if(isPlayerOnTileWithGem(board)) {
      return new PlayerData(this.avatar, this.currentLocation,
          this.homeLocation, this.gemPairLocation, true, this.playerAPI.map(SafePlayer::getPlayer));
    }
    return this;
  }

  public boolean isPlayerOnTileWithGem(Board board) {
    return this.currentLocation.equals(this.gemPairLocation);
  }

  public PlayerData updateCurrentLocationIfOnSlide(Optional<Tuple<Integer, Direction>> plannedBoardMove, int width, int height) {
    if(plannedBoardMove.isEmpty()) {
      return this;
    }

    int index = plannedBoardMove.get().getFirst();
    Direction dir = plannedBoardMove.get().getSecond();

    if(((dir == Direction.LEFT || dir == Direction.RIGHT) && index == currentLocation.getY())
      || ((dir == Direction.UP || dir == Direction.DOWN) && index == currentLocation.getX())) {

      Posn newPos = Direction.offsetPosnWithDirection(dir, currentLocation, 1);

      int x = newPos.getX() % width;
      x = x < 0 ? width - 1: x;

      int y = newPos.getY() % height;
      y = y < 0 ? height - 1: y;

      newPos = new Posn(x, y);
      return new PlayerData(avatar, newPos, homeLocation,  this.gemPairLocation,
          visitedTargetedGemPairTile, this.playerAPI.map(SafePlayer::getPlayer));
    }
    return this;
  }

  public Optional<SafePlayer> getPlayerAPI() {
    return this.playerAPI;
  }

  public Posn getGemPairLocation() {
    return this.gemPairLocation;
  }


  /**
   * Determines if this player is the same distinct player as the given player. A player is the
   * "same" if their avatar colors are equal.
   * @param other the player to compare against
   * @return if the compared players are not distinct
   */
  public boolean isSameDistinctPlayer(PlayerData other) {
    return this.avatar.equals(other.avatar);
  }


  /**
   * Returns a new PlayerData that is a copy of this PlayerData but the name and playerAPI fields
   * have been updated to reflect the given playerAPI.
   * @param playerAPI the playerAPI to update to use
   * @return the new PlayerData
   */
  public PlayerData updatePlayerAPI(Player playerAPI) {
    return new PlayerData(this.avatar, this.currentLocation, this.homeLocation,
        this.gemPairLocation, this.visitedTargetedGemPairTile, Optional.of(playerAPI));
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof PlayerData) {
      PlayerData other = (PlayerData)o;
      return avatar.equals(other.avatar)
          && currentLocation.equals(other.currentLocation)
          && homeLocation.equals(other.homeLocation)
          && visitedTargetedGemPairTile == other.visitedTargetedGemPairTile
          && ((playerAPI.isPresent() && other.playerAPI.isPresent() && playerAPI.get().equals(other.playerAPI.get()))
          || (playerAPI.isEmpty() && other.playerAPI.isEmpty()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    return avatar.hashCode() + currentLocation.hashCode()
        + homeLocation.hashCode() + Boolean.hashCode(visitedTargetedGemPairTile) + playerAPI.hashCode();
  }


}
