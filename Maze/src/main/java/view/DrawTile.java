package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.swing.JPanel;
import util.Direction;
import model.board.Gem;
import model.board.Tile;
import model.state.PlayerData;

/**
 * Represents a special JPanel that draws the stored acceptable
 * character with a border at the edge of the JPanel.
 */
public class DrawTile extends JPanel {

  private final Tile tile;
  private List<PlayerData> playersWithHomesHere;
  private List<PlayerData> playersOnTile;

  /**
   * Public constructor for Tile that takes in a special character that will be used to draw.
   * @param tile character used to draw tile
   */
  public DrawTile(Tile tile) {
    super();
    this.tile = tile;
    this.playersOnTile = new ArrayList<>();
    this.playersWithHomesHere = new ArrayList<>();
  }

  @Override
  public void paint(Graphics g) {
    int width = getWidth();
    int height = getHeight();
    int strokeWeight = getWidth()/6;
    Graphics2D g2 = (Graphics2D)g;
    g2.setColor(Color.DARK_GRAY);

    this.drawPath(g2, width, height, strokeWeight);

    this.drawGems(g2, width, height, strokeWeight);

    this.drawHomes(g2, width, height, strokeWeight);

    this.drawPlayers(g2, width, height, strokeWeight);

    this.drawOutline(g2, width, height);
  }


  /**
   * Adds the list of PlayerData where their home location is on this tile. These player homes will
   * be drawn as squares in the drawHomes method in the color of the PlayerData's avatar.
   * @param players the players with homes located on this tile
   */
  public void addHomes(List<PlayerData> players) {
    this.playersWithHomesHere = players;
  }


  /**
   * Add the list of PlayerData where their current location is on this tile. These players will be
   * drawn as circles in the drawPlayers method in the color of the PlayerData's avatar
   * @param players the players with their current locations on this tile
   */
  public void addPlayers(List<PlayerData> players) {
    this.playersOnTile = players;
  }

  /**
   * Draws the Gem images.
   * @param g2 the graphics to draw to
   * @param width the width of the tile
   * @param height the height of the tile
   * @param strokeWeight the stroke weight of the paths
   */
  private void drawGems(Graphics2D g2, int width, int height, int strokeWeight) {
    //gems
    List<Gem> gems = this.tile.getGems();
    g2.drawImage(gems.get(0).getImage(), 0, 0, width /2 - strokeWeight/2,
        height/2 - strokeWeight/2, null);
    g2.drawImage(gems.get(1).getImage(), width /2 + strokeWeight/2, height/2 + strokeWeight/2,
        width /2 - strokeWeight/2, height/2 - strokeWeight/2, null);
  }

  /**
   * Draws an outline around the tile.
   * @param g2 the graphics to draw to
   * @param width the width of the tile
   * @param height the height of the tile
   */
  private void drawOutline(Graphics2D g2, int width, int height) {
    float thickness = 4;
    Stroke oldStroke = g2.getStroke();
    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(thickness));
    g2.drawRect(0, 0, width, height);
    g2.setStroke(oldStroke);
  }

  /**
   * Draws the path from the directions onto the tile
   * @param g2 the graphics to draw to
   * @param width the width of the tile
   * @param height the height of the tile
   * @param strokeWeight the stroke weight of the paths
   */
  private void drawPath(Graphics2D g2, int width, int height, int strokeWeight) {
    EnumSet<Direction> directions = this.tile.getTileDirections();

    if(directions.contains(Direction.UP)) {
      g2.fillRect(width/2 - strokeWeight/2, 0, strokeWeight, height/2 + strokeWeight/2);
    }

    if(directions.contains(Direction.DOWN)) {
      g2.fillRect(width/2 - strokeWeight/2, height/2 - strokeWeight/2, strokeWeight,
          height/2 + strokeWeight/2);
    }

    if(directions.contains(Direction.LEFT)) {
      g2.fillRect(0, height/2 - strokeWeight/2, width/2 + strokeWeight/2, strokeWeight);
    }

    if(directions.contains(Direction.RIGHT)) {
      g2.fillRect(width/2 - strokeWeight/2, height/2 - strokeWeight/2, width, strokeWeight);
    }
  }



  /**
   * Draws the players on this tile in the top right corner offset by the strokeWeight. If there
   * are more than one players to draw, the representations for each player are scaled down slightly
   * and offset so each player token is visible.
   * @param g2  the graphics to draw to
   * @param width the width of the tile
   * @param height the height of the tile
   * @param strokeWeight the stroke weight of the paths
   */
  private void drawPlayers(Graphics2D g2, int width, int height, int strokeWeight) {
    this.drawPlayerToken(g2, this.playersOnTile, TokenShape.OVAL, width/2 + strokeWeight/2, 0,
        width, height, strokeWeight);
  }

  /**
   * @param g2 the graphics to draw to
   * @param width the width of the tile
   * @param height the height of the tile
   * @param strokeWeight the stroke weight of the paths
   */
  private void drawHomes(Graphics2D g2, int width, int height, int strokeWeight) {
    this.drawPlayerToken(g2, this.playersWithHomesHere, TokenShape.RECT, 0, height/2 + strokeWeight/2, width, height, strokeWeight);
  }

  /**
   * Draws the given token for each player in the color of each player's avatar at the specified
   * location and within the bounds of the listed width and height of the tile. This method makes
   * each token visible by scaling and offsetting each token by the specified showFactor
   * constant.
   * @param g2 the graphics to draw to
   * @param players the players to draw tokens for
   * @param token the token shape to draw
   * @param x the starting x position to draw
   * @param y the starting y position to draw
   * @param width the width of the tile
   * @param height the height of the tile
   * @param strokeWeight the stroke weight of the path
   */
  private void drawPlayerToken(Graphics2D g2, List<PlayerData> players, TokenShape token, int x,
      int y, int width, int height, int strokeWeight) {

    int playerAmt = players.size();
    int widthAvailable = width/2 - strokeWeight/2;
    int heightAvailable = height/2 - strokeWeight/2;

    // constant only needed in this method to scale tokens
    double showFactor = 0.1;
    int rectWidth = (int)(widthAvailable - (showFactor * playerAmt));
    int rectHeight = (int)(heightAvailable - (showFactor * playerAmt));

    Color prevFill = g2.getColor();

    for(int i = 0; i < playerAmt; i++) {
      PlayerData player = players.get(i);
      Color color = player.getAvatar();
      int offsetX = (int)((showFactor * rectWidth) * i);
      int offsetY = (int)((showFactor * rectHeight) * i);

      g2.setColor(color);
      token.apply(g2, x + offsetX, y + offsetY, rectWidth, rectHeight);
    }

    g2.setColor(prevFill);
  }


  /**
   * An enum that specifies a specific method to call to draw to a Graphics2D object.
   */
  private enum TokenShape {
    OVAL(Graphics2D::fillOval),
    RECT(Graphics::fillRect);

    private final GraphicsTokenDrawingConsumer consumer;

    TokenShape(GraphicsTokenDrawingConsumer consumer) {
      this.consumer = consumer;
    }

    /**
     * Applies this TokenShape's consumer with the given parameters
     * @param g2 the graphics to apply to
     * @param x the x position
     * @param y the y position
     * @param width the width
     * @param height the height
     */
    public void apply(Graphics2D g2, int x, int y, int width, int height) {
      this.consumer.apply(g2, x, y, width, height);
    }

    /**
     * A simple function interface that represents any method call to the given Graphics2D object
     * that takes in four integers as parameters.
     */
    private interface GraphicsTokenDrawingConsumer {
      void apply(Graphics2D g2, int x, int y, int width, int height);
    }
  }
}

