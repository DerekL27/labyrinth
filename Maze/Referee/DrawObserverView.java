package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.board.Board;
import model.board.Tile;
import model.state.PlayerData;
import model.state.State;
import observer.GUIObserver;
import util.Posn;

/**
 * This class is responsible for drawing the GUI component of the Obeserver.
 */
public class DrawObserverView {

  private final GUIObserver observerListener;
  private JFrame frame;

  public DrawObserverView(GUIObserver observerListener) {
    this.observerListener = observerListener;
    this.initializeFrame();
  }

  /**
   * Initializes the Swing canvas frame with some basic information.
   */
  private void initializeFrame() {
    frame = new JFrame("Observer");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.getContentPane().setLayout(new GridLayout());
    frame.setLayout(new BorderLayout());

    int initialWidth = 1000;
    int initialHeight = 800;
    frame.setSize(initialWidth, initialHeight);

  }


  /**
   * From the given state, create the view of the board, spare tile, and next and save state buttons
   * @param state the state used in drawing the view
   */
  public void drawView(State state) {
    frame.getContentPane().removeAll();
    Board board = state.getBoard();
    Tile spare = state.getSpareTile();
    List<PlayerData> players = state.getPlayers();

    JPanel boardPanel = drawBoardPanel(board, players);
    frame.add(boardPanel, BorderLayout.CENTER);

    JPanel sparePanel = new JPanel();

    JPanel spareTile = new DrawTile(spare);
    spareTile.setPreferredSize(new Dimension(100, 100));

    sparePanel.add(spareTile);
    frame.add(sparePanel, BorderLayout.LINE_END);

    JPanel buttonPanel = createButtonPanel();

    frame.add(buttonPanel, BorderLayout.PAGE_END);

    frame.setVisible(true);
  }


  /**
   * Creates the button panel that allows the user to change to next state and save the current state
   * @return a panel of two buttons of switching states and saving current state
   */
  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

    JButton nextButton = new JButton("Next");
    nextButton.addActionListener((e) -> this.observerListener.nextState());
    nextButton.setFocusable(false);
    buttonPanel.add(nextButton);

    JButton saveButton = new JButton("Save");
    saveButton.addActionListener((e) -> this.saveToFile(frame));
    saveButton.setFocusable(false);
    buttonPanel.add(saveButton);
    return buttonPanel;
  }


  /**
   * Returns a JPanel that is responsible for all the graphics of the board.
   * @param board the board to draw
   * @return the JPanel representation of the board
   */
  private static JPanel drawBoardPanel(Board board, List<PlayerData> players) {

    int boardHeight = board.getBoardHeight();
    int boardWidth = board.getBoardWidth();

    JPanel panel = new JPanel(new GridLayout(boardHeight, boardWidth));
    panel.setBackground(Color.GRAY);

    Map<Posn, List<PlayerData>> homesToPlayers = getPlayersMapFromLocation(players, PlayerData::getHomeLocation);
    Map<Posn, List<PlayerData>> currentLocationToPlayers = getPlayersMapFromLocation(players, PlayerData::getCurrentLocation);

    for (int row = 0; row < boardHeight; row++) {
      for (int col = 0; col < boardWidth; col++) {

        DrawTile tile = new DrawTile(board.getTile(new Posn(col, row)).get());
        addHomesAndPlayersToDrawTile(tile, homesToPlayers, currentLocationToPlayers, new Posn(col, row));
        tile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(tile);
      }
    }
    return panel;
  }

  /**
   * Adds the list of players who are on the given tile and the list of players whose homes are on the given tile
   * to the given DrawTile to render the players' tokens.
   * @param tile the tile to add to
   * @param homesToPlayers the map from home location to playerData
   * @param currentLocationToPlayers the map from current location to playerData
   * @param pos the position of the tile
   */
  private static void addHomesAndPlayersToDrawTile(DrawTile tile, Map<Posn,
      List<PlayerData>> homesToPlayers, Map<Posn, List<PlayerData>> currentLocationToPlayers, Posn pos) {
    if(homesToPlayers.containsKey(pos)) {
      tile.addHomes(homesToPlayers.get(pos));
    }

    if(currentLocationToPlayers.containsKey(pos)) {
      tile.addPlayers(currentLocationToPlayers.get(pos));
    }
  }

  /**
   * Creates a map from a Posn retrieved from applying the mapper function on each player to a list
   * of players that share the mapped Posn value.
   * @param players the players to map to
   * @param mapper the function to map PlayerData to Posns
   * @return a map of Posns from the given mapper function to list of players
   */
  private static Map<Posn, List<PlayerData>> getPlayersMapFromLocation(List<PlayerData> players,
      Function<PlayerData, Posn> mapper) {
    Map<Posn, List<PlayerData>> map = new HashMap<>();

    for(PlayerData player : players) {

      Posn position = mapper.apply(player);

      if(map.containsKey(position)) {
        List<PlayerData> homes = map.get(position);
        homes.add(player);
        map.put(position, homes);
      } else {
        map.put(position, new ArrayList<>(Arrays.asList(player)));
      }
    }
    return map;
  }

  /**
   * Prompts the user to choose a file to save the game state to
   * @param frame The Jframe used to display the file chooser
   */
  private void saveToFile(JFrame frame) {
    JFileChooser fileChooser = new JFileChooser();
    int option = fileChooser.showSaveDialog(frame);
    if(option == JFileChooser.APPROVE_OPTION){
      File file = fileChooser.getSelectedFile();
      this.observerListener.saveState(file);
    }
  }
}
