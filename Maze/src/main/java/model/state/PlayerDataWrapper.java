package model.state;


import java.awt.Color;
import util.Posn;

/**
 * This class represents all the information an observer can know about the given PlayerData player.
 *
 * An observer can know the following information about the player:
 * - name
 * - avatar (color)
 * - home location
 * - current location
 */
public class PlayerDataWrapper {

  private PlayerData player;

  public PlayerDataWrapper(PlayerData player) {
    this.player = player;
  }

  public Color getAvatar() {
    return this.player.getAvatar();
  }

  public Posn getHomeLocation() {
    return this.player.getHomeLocation();
  }

  public Posn getCurrentLocation() {
    return this.player.getCurrentLocation();
  }
}
