package model.state;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import util.Direction;
import referee.Player;
import referee.SafePlayer;
import util.Posn;
import util.Tuple;

public class PlayerData {

  private final Color avatar;

  private final Posn currentLocation;

  private final Posn homeLocation;

  private final Posn goalLocation;

  private final Optional<SafePlayer> playerAPI;

  private final int numGoalsVisited;

  private final boolean hasReachedFinalGoal;

  private final boolean hasReturnedHome;

  private final boolean hasReceivedAdditionalGoal;

  // This constructor is used by unit tests
  public PlayerData(Color avatar, Posn currentLocation,
      Posn homeLocation, int numGoalsVisited, boolean hasReachedFinalGoal) {
    this(avatar, currentLocation, homeLocation, new Posn(-1, -1), Optional.empty(), numGoalsVisited, hasReachedFinalGoal, false, false);
  }

  // This constructor is used by the testing harness
  public PlayerData(Posn currentLocation, Posn homeLocation, Color avatar, int numGoalsVisited) {
    this(avatar, currentLocation, homeLocation, new Posn(-1, -1),
            Optional.empty(), numGoalsVisited, false, false, false);
  }

  // This constructor is used by the Referee and this class to create a full Player
  public PlayerData(Color avatar, Posn currentLocation, Posn homeLocation, Posn goalLocation, Optional<Player> playerAPI,
                    int numGoalsVisited, boolean hasReachedFinalGoal, boolean hasReturnedHome, boolean hasReceivedAdditionalGoal) {
    this.avatar = avatar;
    this.currentLocation = currentLocation;
    this.homeLocation = homeLocation;
    this.goalLocation = goalLocation;
    this.playerAPI = playerAPI.map(SafePlayer::new);
    this.numGoalsVisited = numGoalsVisited;
    this.hasReachedFinalGoal = hasReachedFinalGoal;
    this.hasReturnedHome = hasReturnedHome;
    this.hasReceivedAdditionalGoal = hasReceivedAdditionalGoal;
  }

  public Color getAvatar() {
    return avatar;
  }

  public Posn getCurrentLocation() {
    return currentLocation;
  }

  public PlayerData updateCurrentLocation(Posn currentLocation) {
    return new PlayerData(this.avatar, currentLocation,
        this.homeLocation, this.goalLocation, this.playerAPI.map(SafePlayer::getPlayer), this.numGoalsVisited, this.hasReachedFinalGoal, this.hasReturnedHome, this.hasReceivedAdditionalGoal);
  }

  public Posn getHomeLocation() {
    return homeLocation;
  }


  public boolean isPlayerOnGoal() {
    return this.currentLocation.equals(this.goalLocation);
  }

  public boolean isPlayerOnHome() {
    return this.currentLocation.equals(this.homeLocation);
  }

  /**
   * Takes in a planned board move and adjusts the player's current location accordingly.
   * @param plannedBoardMove the planned board move
   * @param width of the board
   * @param height of the board
   * @return the updated player data
   */
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
      return new PlayerData(avatar, newPos, homeLocation,  this.goalLocation,
              this.playerAPI.map(SafePlayer::getPlayer), this.numGoalsVisited, this.hasReachedFinalGoal, this.hasReturnedHome, this.hasReceivedAdditionalGoal);
    }
    return this;
  }

  public Optional<SafePlayer> getPlayerAPI() {
    return this.playerAPI;
  }

  public Posn getGoalLocation() {
    return this.goalLocation;
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
        this.goalLocation, Optional.of(playerAPI), this.numGoalsVisited, this.hasReachedFinalGoal, this.hasReturnedHome, this.hasReceivedAdditionalGoal);
  }

  public int getNumGoalsVisited() {
    return this.numGoalsVisited;
  }

  /**
   * Check if the player has reached a goal or home and update the player data accordingly.
   * @param additionalGoals the list of additional goals
   * @return the updated player data, doesn't update if the player has not reached anything
   */
  public PlayerData updateIfReachedTarget(List<Posn> additionalGoals) {
    // if there are still goals in the additional goals list
    if (this.goalLocation.equals(this.currentLocation) && additionalGoals.size() > 0){
      return new PlayerData(this.avatar, this.currentLocation, this.homeLocation,
              additionalGoals.remove(0), this.playerAPI.map(SafePlayer::getPlayer),
              this.numGoalsVisited + 1, false, false, true);
    }
    // if there are no more goals in the additional goals list and the player just reached the last goal
    else if (this.goalLocation.equals(this.currentLocation) && additionalGoals.size() == 0 && !this.hasReachedFinalGoal) {
      // if the player's final goal came from the additional goals list and is the same as their home
      // they don't get a treasure but it counts as going home
      if(this.currentLocation.equals(this.homeLocation) && this.hasReceivedAdditionalGoal) {
        return new PlayerData(this.avatar, this.currentLocation, this.homeLocation,
                this.homeLocation, this.playerAPI.map(SafePlayer::getPlayer),
                this.numGoalsVisited, true, true, true);
      }
      return new PlayerData(this.avatar, this.currentLocation, this.homeLocation,
              this.homeLocation, this.playerAPI.map(SafePlayer::getPlayer),
              this.numGoalsVisited + 1, true, false, this.hasReceivedAdditionalGoal);
    }
    //if the player has reached the last goal and is on the home location
    else if (this.goalLocation.equals(this.currentLocation) && this.hasReachedFinalGoal){
      return new PlayerData(this.avatar, this.currentLocation, this.homeLocation,
              this.homeLocation, this.playerAPI.map(SafePlayer::getPlayer),
              this.numGoalsVisited, true, true, this.hasReceivedAdditionalGoal);
    }
    return this;
  }

  public boolean getHasReachedFinalGoal() {
    return this.hasReachedFinalGoal;
  }

  public boolean getHasReturnedHome() {
    return this.hasReturnedHome;
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof PlayerData) {
      PlayerData other = (PlayerData)o;
      return avatar.equals(other.avatar)
          && currentLocation.equals(other.currentLocation)
          && homeLocation.equals(other.homeLocation)
          && ((playerAPI.isPresent() && other.playerAPI.isPresent() && playerAPI.get().equals(other.playerAPI.get()))
              || (playerAPI.isEmpty() && other.playerAPI.isEmpty()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    return avatar.hashCode() + currentLocation.hashCode()
        + homeLocation.hashCode()+ playerAPI.hashCode() + numGoalsVisited;
  }


}
