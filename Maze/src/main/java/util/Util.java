package util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import model.board.BasicTile;
import model.board.Board;
import model.board.Gem;
import model.board.RectBoard;
import model.board.Tile;
import model.state.MazeState;
import model.state.PlayerData;
import model.state.State;
import model.strategy.RiemannStrategy;
import referee.Player;
import referee.StrategyPlayer;

/**
 * General utility class used for methods that could be used by any given package.
 */
public class Util {

  /**
   * Determines if the two given sets have the exact same set of items (order does not matter).
   * @param first the first list
   * @param second the second list
   * @param <T> the type of both lists
   * @return if the lists have exactly the same items
   */
  public static <T> boolean listsContainSameItems(List<T> first, List<T> second) {
    List<T> accumulator = new ArrayList<>();
    for(T item : first) {
      accumulator.add(item);
    }
    for(T item : second) {
      if(accumulator.contains(item)) {
        accumulator.remove(item);
      } else {
        return false;
      }
    }
    return accumulator.isEmpty();
  }

  /**
   * Builds a shallow copy of a list. Intended for use on a list of immutable items.
   * @param list the list to copy
   * @param <T> the type of element in the list
   * @return the copied list
   */
  public static <T> List<T> shallowCopyOf(List<T> list) {
    List<T> copy = new ArrayList<>();
    for(T item : list) {
      copy.add(item);
    }
    return copy;
  }

  /**
   * Generates a random Board and spare Tile using the given dimensions and random seed.
   * @param width the board width
   * @param height the board height
   * @param seed the random seed
   * @return a tuple containing the randomly generated board and spare tile
   */
  public static Tuple<Board, Tile> generateRandomBoardAndSpare(int width, int height, int seed) {
    Random random = new Random(seed);

    Board board = new RectBoard(width, height);

    List<Character> specialChars = Arrays.asList('│', '─', '┐', '└', '┌', '┘', '┬', '├', '┴', '┤', '┼');
    List<List<Gem>> pairs = generateGemPairs();
    Iterator<List<Gem>> iter = pairs.iterator();

    for(int row = 0; row < board.getBoardHeight(); row++) {
      for(int col = 0; col < board.getBoardWidth(); col++) {
        List<Gem> gemPair = iter.next();
        EnumSet<Direction> directions = Direction.specialCharToDirections(specialChars.get(random.nextInt(specialChars.size())));
        Tile tile = new BasicTile(directions, gemPair);
        board.placeTileSafely(new Posn(col, row), tile);
      }
    }

    List<Gem> gemPair = iter.next();
    EnumSet<Direction> directions = Direction.specialCharToDirections(specialChars.get(random.nextInt(specialChars.size())));
    Tile spare = new BasicTile(directions, gemPair);

    return new Tuple<>(board, spare);
  }

  /**
   * Generates all possible unique and unordered pairs of gems as a list of lists.
   * @return a list containing lists of two gems
   */
  public static List<List<Gem>> generateGemPairs() {
    List<List<Gem>> pairs = new ArrayList<>();
    Gem[] gems = Gem.values();
    int gemSize = gems.length;

    for(int i = 0; i < gemSize; i++) {
      for(int j = i; j < gemSize; j++) {
        List<Gem> pair = Arrays.asList(gems[i], gems[j]);
        pairs.add(pair);
      }
    }
    return pairs;
  }

  /**
   * Tries to find a unique color that is not an avatar color of any PlayerData in the given list of
   * Players. If it cannot find a unique color, white is chosen.
   * @param playerData the list of player information to check against to find a unique color
   * @param randomSeed the random seed
   * @return a randomly generated color
   */
  public static Color provideUniqueColorFromPlayers(List<PlayerData> playerData, int randomSeed) {
    List<Color> colors = playerData.stream().map(PlayerData::getAvatar).collect(Collectors.toList());
    Random random = new Random(randomSeed);
    int maxFails = 1000;
    while(maxFails > 0) {
      Color col = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
      if(!colors.contains(col)) {
        return col;
      }
      maxFails--;
    }
    return Color.white;
  }

  /**
   * Generates a random maze board using the given dimensions and random seed.
   * @param players the list of players to include in the randomly generated state
   * @param boardWidth the width of the board
   * @param boardHeight the height of the board
   * @return a randomly generated state
   */
  public static State createRandomState(List<Player> players, int boardWidth, int boardHeight) {
    Tuple<Board, Tile> boardAndTile = generateRandomBoardAndSpare(boardWidth, boardHeight, 100);
    Optional<Tuple<Integer, Direction>> prevMove = Optional.empty();

    List<PlayerData> playerDatas = new ArrayList<>();
    List<Posn> allUnmovableLocations = getAllUnMovableLocations(boardAndTile.getFirst());

    for(int i = 0; i < players.size(); i++) {
      Player player = players.get(i);
      Color playerColor = provideUniqueColorFromPlayers(playerDatas, 0);
      Posn home = allUnmovableLocations.get(i);
      Posn goal = allUnmovableLocations.get((int)(Math.random() * allUnmovableLocations.size()));
      playerDatas.add(new PlayerData(playerColor, home, home, goal, Optional.of(player), 0, false, false, false));
    }
    return new MazeState(boardAndTile.getFirst(), playerDatas, boardAndTile.getSecond(), prevMove, new ArrayList<>());
  }

  /**
   * Gets all the locations on the board that are unmovable.
   * @param board the board to check
   * @return
   */
  public static List<Posn> getAllUnMovableLocations(Board board) {
    List<Integer> movableRows = board.getMovableRowIndices();
    List<Integer> immovableRows = IntStream.range(0, board.getBoardHeight()).filter(x -> !movableRows.contains(x))
        .boxed().collect(Collectors.toList());

    List<Integer> movableCols = board.getMovableColIndices();
    List<Integer> immovableCols = IntStream.range(0, board.getBoardHeight()).filter(x -> !movableCols.contains(x))
        .boxed().collect(Collectors.toList());

    List<Posn> immovablePosns = new ArrayList<>();

    for(int row : immovableRows) {
      for (int col : immovableCols) {
        immovablePosns.add(new Posn(col, row));
      }
    }
    return immovablePosns;
  }


  /**
   * Reverses the given list by mutation.
   * @param list the list to reverse
   * @param <T> the type of element in the list
   */
  public static <T> void reverseList(List<T> list) {
    int size = list.size();
    for(int i = 0; i < size; i++) {
      list.add(i, list.remove(size - 1));
    }
  }
}
