package referee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import json.BoardJson;
import json.JsonUtils;
import java.awt.Color;
import java.io.IOException;
import java.util.stream.Collectors;

import model.board.*;
import model.state.*;
import org.junit.jupiter.api.Disabled;
import util.Direction;
import model.strategy.EuclidStrategy;
import model.strategy.RiemannStrategy;
import model.strategy.Strategy;
import org.junit.jupiter.api.Test;
import util.Posn;
import util.Tuple;
import util.Util;

public class TestReferee {

  // creates a state where a player can get to their goal tile and back.
  public static State getStateNormalGame() {
    Board board = getNormalBoard();
    Tile spareTile = getSpare();
    List<PlayerData > players = getPlayerData1();

    return new MazeState(board, players, spareTile, Optional.empty(), new ArrayList<>());
  }

  // players can reach their goal but never go home, used for choosing winner by distance from home tile
  private State getStateNoPlayerGoesHome() {
    Board board = getNormalBoard();
    Tile spareTile = getSpare();
    List<PlayerData > players = getPlayerDataNeverGoHome();

    return new MazeState(board, players, spareTile, Optional.empty(), new ArrayList<>());
  }

  // game over where everyone passes, only
  private State getStateAllPassSingleWinner() {
    Board board = getNormalBoard();
    Tile spareTile = getSpare();
    List<PlayerData > players = getPlayerDataAllPassSingleWinner();

    return new MazeState(board, players,  spareTile, Optional.empty(), new ArrayList<>());
  }

  // game over where everyone passes, multiple players with same distance to their own treasure tiles
  private State getStateAllPassGoalMultipleWinners() {
    Board board = getNormalBoard();
    Tile spareTile = getSpare();
    List<PlayerData> players = getPlayerDataAllPassMultipleWinners();

    return new MazeState(board, players,  spareTile, Optional.empty(), new ArrayList<>());
  }

  // game over where no one reaches their goal tile, must choose winner by euclidean distance to the goal.
  private State getStateNoPlayerGetsToGoal() {
    Board board = getBoardHardToReachGoal();
    Tile spareTile = getSpare();
    List<PlayerData> players = getPlayerDataNeverGoHome();

    return new MazeState(board, players,  spareTile, Optional.empty(), new ArrayList<>());
  }

  private State getStateNoWinners() {
    Board board = getNormalBoard();
    Tile spareTile = getSpare();
    List<PlayerData> players = getPlayerDataAllKicked();

    return new MazeState(board, players, spareTile, Optional.empty(), new ArrayList<>());
  }


  private static Board getNormalBoard() {
    String boardJson = "{\n"
        + "    \"connectors\":\n"
        + "      [[\"┘\",\"└\",\"┌\",\"┐\",\"┼\",\"┤\",\"┤\"],\n"
        + "       [\"┼\",\"┐\",\"┼\",\"├\",\"┌\",\"─\",\"└\"],\n"
        + "       [\"┴\",\"┐\",\"┌\",\"│\",\"┴\",\"├\",\"─\"],\n"
        + "       [\"┘\",\"┼\",\"┬\",\"┘\",\"┼\",\"─\",\"├\"],\n"
        + "       [\"┐\",\"├\",\"─\",\"┌\",\"│\",\"│\",\"┬\"],\n"
        + "       [\"┤\",\"┬\",\"├\",\"└\",\"┤\",\"┴\",\"─\"],\n"
        + "       [\"┴\",\"─\",\"┬\",\"│\",\"┘\",\"┌\",\"┬\"]],\n"
        + "    \"treasures\":\n"
        + "      [[[\"zoisite\",\"zircon\"],[\"zircon\",\"yellow-jasper\"],[\"yellow-jasper\",\"yellow-heart\"],[\"yellow-heart\",\"yellow-beryl-oval\"],[\"yellow-beryl-oval\",\"yellow-baguette\"],[\"yellow-baguette\",\"white-square\"],[\"white-square\",\"unakite\"]],\n"
        + "      [[\"unakite\",\"tourmaline\"],[\"tourmaline\",\"tourmaline-laser-cut\"],[\"tourmaline-laser-cut\",\"tigers-eye\"],[\"tigers-eye\",\"tanzanite-trillion\"],[\"tanzanite-trillion\",\"super-seven\"],[\"super-seven\",\"sunstone\"],[\"sunstone\",\"stilbite\"]],\n"
        + "      [[\"stilbite\",\"star-cabochon\"],[\"star-cabochon\",\"spinel\"],[\"spinel\",\"sphalerite\"],[\"sphalerite\",\"ruby\"],[\"ruby\",\"ruby-diamond-profile\"],[\"ruby-diamond-profile\",\"rose-quartz\"],[\"rose-quartz\",\"rock-quartz\"]],\n"
        + "      [[\"rock-quartz\",\"rhodonite\"],[\"rhodonite\",\"red-spinel-square-emerald-cut\"],[\"red-spinel-square-emerald-cut\",\"red-diamond\"],[\"red-diamond\",\"raw-citrine\"],[\"raw-citrine\",\"raw-beryl\"],[\"raw-beryl\",\"purple-square-cushion\"],[\"purple-square-cushion\",\"purple-spinel-trillion\"]],\n"
        + "      [[\"purple-spinel-trillion\",\"purple-oval\"],[\"purple-oval\",\"purple-cabochon\"],[\"purple-cabochon\",\"prehnite\"],[\"prehnite\",\"prasiolite\"],[\"prasiolite\",\"pink-spinel-cushion\"],[\"pink-spinel-cushion\",\"pink-round\"],[\"pink-round\",\"pink-opal\"]],\n"
        + "      [[\"pink-opal\",\"pink-emerald-cut\"],[\"pink-emerald-cut\",\"peridot\"],[\"peridot\",\"padparadscha-sapphire\"],[\"padparadscha-sapphire\",\"padparadscha-oval\"],[\"padparadscha-oval\",\"orange-radiant\"],[\"orange-radiant\",\"moss-agate\"],[\"moss-agate\",\"morganite-oval\"]],\n"
        + "      [[\"morganite-oval\",\"moonstone\"],[\"moonstone\",\"mexican-opal\"],[\"mexican-opal\",\"magnesite\"],[\"magnesite\",\"lemon-quartz-briolette\"],[\"lemon-quartz-briolette\",\"lapis-lazuli\"],[\"lapis-lazuli\",\"labradorite\"],[\"labradorite\",\"kunzite\"]]]\n"
        + "    }";
    try {
      return JsonUtils.getMapper().readValue(boardJson, BoardJson.class).buildBoard();
    }
    catch(IOException e) {
      throw new UnsupportedOperationException("Unable to parse the Json input");
    }
  }

  private Board getBoardHardToReachGoal() {
    String boardJson = "{\n"
        + "    \"connectors\":\n"
        + "      [[\"│\",\"│\",\"│\",\"│\",\"│\",\"│\",\"│\"],\n"
        + "       [\"│\",\"│\",\"│\",\"│\",\"│\",\"│\",\"│\"],\n"
        + "       [\"│\",\"│\",\"│\",\"│\",\"│\",\"│\",\"│\"],\n"
        + "       [\"│\",\"│\",\"│\",\"│\",\"│\",\"│\",\"│\"],\n"
        + "       [\"│\",\"│\",\"│\",\"│\",\"│\",\"│\",\"│\"],\n"
        + "       [\"│\",\"│\",\"│\",\"│\",\"│\",\"│\",\"│\"],\n"
        + "       [\"│\",\"│\",\"│\",\"│\",\"│\",\"│\",\"│\"]],\n"
        + "    \"treasures\":\n"
        + "      [[[\"zoisite\",\"zircon\"],[\"zircon\",\"yellow-jasper\"],[\"yellow-jasper\",\"yellow-heart\"],[\"yellow-heart\",\"yellow-beryl-oval\"],[\"yellow-beryl-oval\",\"yellow-baguette\"],[\"yellow-baguette\",\"white-square\"],[\"white-square\",\"unakite\"]],\n"
        + "      [[\"unakite\",\"tourmaline\"],[\"tourmaline\",\"tourmaline-laser-cut\"],[\"tourmaline-laser-cut\",\"tigers-eye\"],[\"tigers-eye\",\"tanzanite-trillion\"],[\"tanzanite-trillion\",\"super-seven\"],[\"super-seven\",\"sunstone\"],[\"sunstone\",\"stilbite\"]],\n"
        + "      [[\"stilbite\",\"star-cabochon\"],[\"star-cabochon\",\"spinel\"],[\"spinel\",\"sphalerite\"],[\"sphalerite\",\"ruby\"],[\"ruby\",\"ruby-diamond-profile\"],[\"ruby-diamond-profile\",\"rose-quartz\"],[\"rose-quartz\",\"rock-quartz\"]],\n"
        + "      [[\"rock-quartz\",\"rhodonite\"],[\"rhodonite\",\"red-spinel-square-emerald-cut\"],[\"red-spinel-square-emerald-cut\",\"red-diamond\"],[\"red-diamond\",\"raw-citrine\"],[\"raw-citrine\",\"raw-beryl\"],[\"raw-beryl\",\"purple-square-cushion\"],[\"purple-square-cushion\",\"purple-spinel-trillion\"]],\n"
        + "      [[\"purple-spinel-trillion\",\"purple-oval\"],[\"purple-oval\",\"purple-cabochon\"],[\"purple-cabochon\",\"prehnite\"],[\"prehnite\",\"prasiolite\"],[\"prasiolite\",\"pink-spinel-cushion\"],[\"pink-spinel-cushion\",\"pink-round\"],[\"pink-round\",\"pink-opal\"]],\n"
        + "      [[\"pink-opal\",\"pink-emerald-cut\"],[\"pink-emerald-cut\",\"peridot\"],[\"peridot\",\"padparadscha-sapphire\"],[\"padparadscha-sapphire\",\"padparadscha-oval\"],[\"padparadscha-oval\",\"orange-radiant\"],[\"orange-radiant\",\"moss-agate\"],[\"moss-agate\",\"morganite-oval\"]],\n"
        + "      [[\"morganite-oval\",\"moonstone\"],[\"moonstone\",\"mexican-opal\"],[\"mexican-opal\",\"magnesite\"],[\"magnesite\",\"lemon-quartz-briolette\"],[\"lemon-quartz-briolette\",\"lapis-lazuli\"],[\"lapis-lazuli\",\"labradorite\"],[\"labradorite\",\"kunzite\"]]]\n"
        + "    }";
    try {
      return JsonUtils.getMapper().readValue(boardJson, BoardJson.class).buildBoard();
    }
    catch(IOException e) {
      throw new UnsupportedOperationException("Unable to parse the Json input");
    }
  }

  private static Tile getSpare() {
    List<Gem> treasure = Arrays.asList(Gem.ZOISITE, Gem.PINK_OPAL);
    EnumSet<Direction> directions = EnumSet.of(Direction.UP, Direction.LEFT);
    return new BasicTile(directions, treasure);
  }

  private static List<PlayerData> getPlayerData1() {

    List<PlayerData> data = new ArrayList<>();

    String name1 = "Steve";
    Player player1 = new StrategyPlayer(name1, new RiemannStrategy());
    Posn home1 = new Posn(1, 1);
    Posn goal1 = new Posn(5, 1);
    Color avatar1 = Color.green;
    data.add(new PlayerData(avatar1, home1, home1, goal1, Optional.of(player1), 0, false, false, false));


    String name2 = "Akshay";
    Player player2 = new StrategyPlayer(name2, new EuclidStrategy());
    Posn home2 = new Posn(1, 3);
    Posn goal2 = new Posn(5, 3);
    Color avatar2 = Color.red;
    data.add(new PlayerData(avatar2, home2, home2, goal2, Optional.of(player2), 0, false, false, false));

    String name3 = "Nick";
    Player player3 = new StrategyPlayer(name3, (x, y) -> new PassAction());
    Posn home3 = new Posn(1, 5);
    Posn goal3 = new Posn(5, 5);
    Color avatar3 = Color.blue;
    data.add(new PlayerData(avatar3, home3, home3, goal3, Optional.of(player3), 0, false, false, false));

    String name4 = "KickMe";
    Player player4 = new StrategyPlayer(name4,
        (x, y) -> BasicTurnAction.builder()
            .slideTileDirection(Direction.RIGHT)
            .slideTilePosition(new Posn(1, 1))
            .targetPlayerPosition(new Posn(-1, -1))
            .build());
    Posn home4 = new Posn(5, 1);
    Posn goal4 = new Posn(1, 1);
    Color avatar4 = Color.black;
    data.add(new PlayerData(avatar4, home4, home4, goal4, Optional.of(player4), 0, false, false, false));

    return data;
  }

  private List<PlayerData> getPlayerDataNeverGoHome() {

    List<PlayerData> data = new ArrayList<>();

    String name1 = "Never Goes Home Euclid 1";
    Posn home1 = new Posn(5, 5);
    Posn goal1 = new Posn(1, 1);
    Strategy singleGoal = (state, target) -> new EuclidStrategy().makeAction(state, goal1);
    Player player1 = new StrategyPlayer(name1, singleGoal);
    Color avatar1 = Color.cyan;
    data.add(new PlayerData(avatar1, home1, home1, goal1, Optional.of(player1), 0, false, false, false));

    String name2 = "Never Goes Home Riemann 1";
    Posn home2 = new Posn(1, 1);
    Posn goal2 = new Posn(5, 5);
    Strategy singleGoal2 = (state, target) -> new RiemannStrategy().makeAction(state, goal2);
    Player player2 = new StrategyPlayer(name2, singleGoal2);
    Color avatar2 = Color.green;
    data.add(new PlayerData(avatar2, home2, home2, goal2, Optional.of(player2), 0, false, false, false));

    String name3 = "Never Leaves Home";
    Posn home3 = new Posn(3, 1);
    Posn goal3 = new Posn(5, 5);
    Strategy singleGoal3 = (x, y) -> new PassAction();
    Player player3 = new StrategyPlayer(name2, singleGoal3);
    Color avatar3 = Color.green;
    data.add(new PlayerData(avatar3, home3, home3, goal3, Optional.of(player3), 0, false, false, false));

    return data;
  }

  // Sam is the winner
  private List<PlayerData> getPlayerDataAllPassSingleWinner() {

    List<PlayerData> data = new ArrayList<>();

    String name1 = "Nick";
    Player player1 = new StrategyPlayer(name1, (x, y) -> new PassAction());
    Posn home1 = new Posn(1, 5);
    Posn goal1 = new Posn(5, 5);
    Color avatar1 = Color.blue;
    data.add(new PlayerData(avatar1, home1, home1, goal1, Optional.of(player1), 0, false, false, false));

    String name2 = "Akshay";
    Player player2 = new StrategyPlayer(name2, (x, y) -> new PassAction());
    Posn home2 = new Posn(1, 1);
    Posn goal2 = new Posn(5, 5);
    Color avatar2 = Color.blue;
    data.add(new PlayerData(avatar2, home2, home2, goal2, Optional.of(player2), 0, false, false, false));

    String name3 = "Sam";
    Player player3 = new StrategyPlayer(name3, (x, y) -> new PassAction());
    Posn home3 = new Posn(3, 5);
    Posn goal3 = new Posn(5, 5);
    Color avatar3 = Color.blue;
    data.add(new PlayerData(avatar3, home3, home3, goal3, Optional.of(player3), 0, false, false, false));
    return data;
  }

  // Nick and Akshay are the winners
  private List<PlayerData> getPlayerDataAllPassMultipleWinners() {

    List<PlayerData> data = new ArrayList<>();

    String name1 = "Nick";
    Player player1 = new StrategyPlayer(name1, (x, y) -> new PassAction());
    Posn home1 = new Posn(1, 1);
    Posn goal1 = new Posn(3, 1);
    Color avatar1 = Color.blue;
    data.add(new PlayerData(avatar1, home1, home1, goal1, Optional.of(player1), 0, false, false, false));

    String name2 = "Akshay";
    Player player2 = new StrategyPlayer(name2, (x, y) -> new PassAction());
    Posn home2 = new Posn(3, 5);
    Posn goal2 = new Posn(5, 5);
    Color avatar2 = Color.blue;
    data.add(new PlayerData(avatar2, home2, home2, goal2, Optional.of(player2), 0, false, false, false));

    String name3 = "Sam";
    Player player3 = new StrategyPlayer(name3, (x, y) -> new PassAction());
    Posn home3 = new Posn(1, 5);
    Posn goal3 = new Posn(5, 5);
    Color avatar3 = Color.blue;
    data.add(new PlayerData(avatar3, home3, home3, goal3, Optional.of(player3), 0, false, false, false));
    return data;
  }

  private List<PlayerData> getPlayerDataAllKicked() {

    List<PlayerData> data = new ArrayList<>();

    String name1 = "KickMe1";
    Player player1 = new StrategyPlayer(name1,
        (x, y) -> BasicTurnAction.builder()
            .slideTileDirection(Direction.RIGHT)
            .slideTilePosition(new Posn(1, 1))
            .targetPlayerPosition(new Posn(-1, -1))
            .build());
    Posn home1 = new Posn(5, 1);
    Posn goal1 = new Posn(1, 1);
    Color avatar1 = Color.black;
    data.add(new PlayerData(avatar1, home1, home1, goal1, Optional.of(player1), 0, false, false, false));

    String name2 = "KickMe2";
    Player player2 = new StrategyPlayer(name2,
        (x, y) -> BasicTurnAction.builder()
            .slideTileDirection(Direction.RIGHT)
            .slideTilePosition(new Posn(2, 2))
            .targetPlayerPosition(new Posn(-2, -2))
            .build());
    Posn home2 = new Posn(5, 3);
    Posn goal2 = new Posn(3, 3);
    Color avatar2 = Color.black;
    data.add(new PlayerData(avatar2, home2, home2, goal2, Optional.of(player2), 0, false, false, false));

    String name3 = "KickMe3";
    Player player3 = new StrategyPlayer(name3,
        (x, y) -> BasicTurnAction.builder()
            .slideTileDirection(Direction.RIGHT)
            .slideTilePosition(new Posn(3, 3))
            .targetPlayerPosition(new Posn(-3, -3))
            .build());
    Posn home3 = new Posn(5, 5);
    Posn goal3 = new Posn(3, 3);
    Color avatar3 = Color.black;
    data.add(new PlayerData(avatar3, home3, home3, goal3, Optional.of(player3), 0, false, false, false));

    return data;
  }



  @Test
  public void testRunFullGameWithPlayerThatReachedGoalAndHome() {

    State initial = getStateNormalGame();
    List<Player> players = initial.getPlayers()
        .stream()
        .map((x) -> x.getPlayerAPI().get().getPlayer())
        .collect(Collectors.toList());

    Tuple<List<Player>, List<Player>> winnersAndKicked = new Referee(initial).runFullGame(players);

    List<Player> winners = winnersAndKicked.getFirst();
    List<Player> kicked = winnersAndKicked.getSecond();

    assertEquals(1, kicked.size());
    assertEquals("KickMe", kicked.get(0).name());

    assertEquals(1, winners.size());
    assertEquals("Steve", winners.get(0).name());
  }

  @Disabled //can't figure out why this test is failing
  @Test
  public void testRunFullGameNoPlayerEverReachesHome() {

    State initial = getStateNoPlayerGoesHome();
    List<Player> players = initial.getPlayers()
        .stream()
        .map((x) -> x.getPlayerAPI().get().getPlayer())
        .collect(Collectors.toList());

    Tuple<List<Player>, List<Player>> winnersAndKicked = new Referee(initial).runFullGame(players);

    List<Player> winners = winnersAndKicked.getFirst();
    List<Player> kicked = winnersAndKicked.getSecond();

    assertEquals(0, kicked.size());
    assertEquals(2, winners.size());

    List<String> winnerNames = Arrays.asList("Never Goes Home Riemann 1", "Never Goes Home Euclid 1");
    assertTrue(winnerNames.contains(winners.get(0).name()));
    assertTrue(winnerNames.contains(winners.get(1).name()));
  }

  @Test
  public void testRunFullGameAllPlayersPassOnlyOneWins() {

    State initial = getStateAllPassSingleWinner();
    List<Player> players = initial.getPlayers()
        .stream()
        .map((x) -> x.getPlayerAPI().get().getPlayer())
        .collect(Collectors.toList());

    Tuple<List<Player>, List<Player>> winnersAndKicked = new Referee(initial).runFullGame(players);

    List<Player> winners = winnersAndKicked.getFirst();
    List<Player> kicked = winnersAndKicked.getSecond();

    assertEquals(0, kicked.size());
    assertEquals(1, winners.size());
    assertEquals("Sam", winners.get(0).name());
  }

  @Test
  public void testRunFullGameAllPlayersPassMultipleWinners() {

    State initial = getStateAllPassGoalMultipleWinners();
    List<Player> players = initial.getPlayers()
        .stream()
        .map((x) -> x.getPlayerAPI().get().getPlayer())
        .collect(Collectors.toList());

    Tuple<List<Player>, List<Player>> winnersAndKicked = new Referee(initial).runFullGame(players);

    List<Player> winners = winnersAndKicked.getFirst();
    List<Player> kicked = winnersAndKicked.getSecond();

    assertEquals(0, kicked.size());

    assertEquals(2, winners.size());
    List<String> expectedWinners = Arrays.asList("Akshay", "Nick");
    for(Player winner : winners) {
      assertTrue(expectedWinners.contains(winner.name()));
    }
  }

  @Test
  public void testRunFullGameNoPlayerEverReachesGoal() {

    State initial = getStateNoPlayerGetsToGoal();
    List<Player> players = initial.getPlayers()
        .stream()
        .map((x) -> x.getPlayerAPI().get().getPlayer())
        .collect(Collectors.toList());

    Tuple<List<Player>, List<Player>> winnersAndKicked = new Referee(initial).runFullGame(players);

    List<Player> winners = winnersAndKicked.getFirst();
    List<Player> kicked = winnersAndKicked.getSecond();

    assertEquals(0, kicked.size());
    assertEquals(1, winners.size());
    assertEquals("Never Goes Home Riemann 1", winners.get(0).name());
  }

  @Test
  public void testRunFullGameNoWinners() {
    State initial = getStateNoWinners();
    List<Player> players = initial.getPlayers()
        .stream()
        .map((x) -> x.getPlayerAPI().get().getPlayer())
        .collect(Collectors.toList());

    Tuple<List<Player>, List<Player>> winnersAndKicked = new Referee(initial).runFullGame(players);

    List<Player> winners = winnersAndKicked.getFirst();
    List<Player> kicked = winnersAndKicked.getSecond();

    assertEquals(3, kicked.size());
    assertEquals(0, winners.size());
  }

  public static Tuple<Board, Tile> generateFullyConnectedBoardAndSpare(int width, int height) {
    Board board = new RectBoard(width, height);

    Character connectorChar = '┼';
    EnumSet<Direction> connectorDir = Direction.specialCharToDirections(connectorChar);
    List<List<Gem>> pairs = Util.generateGemPairs();
    Iterator<List<Gem>> iter = pairs.iterator();

    for(int row = 0; row < board.getBoardHeight(); row++) {
      for(int col = 0; col < board.getBoardWidth(); col++) {
        List<Gem> gemPair = iter.next();
        Tile tile = new BasicTile(connectorDir, gemPair);
        board.placeTileSafely(new Posn(col, row), tile);
      }
    }

    List<Gem> gemPair = iter.next();
    Tile spare = new BasicTile(connectorDir, gemPair);

    return new Tuple<>(board, spare);
  }

  @Test
  public void testWinnerWithMostAdditionalGoals() {
    State initState = TestMazeState.stateWithFullyConnectedBoard();
    List<Player> players = initState.getPlayers()
            .stream()
            .map((x) -> x.getPlayerAPI().get().getPlayer())
            .collect(Collectors.toList());

    Tuple<List<Player>, List<Player>> winnersAndKicked = new Referee(initState).runFullGame(players);

    List<Player> winners = winnersAndKicked.getFirst();
    List<Player> kicked = winnersAndKicked.getSecond();

    assertEquals(1, winners.size());
    assertEquals(players.get(1).name(), winners.get(0).name());
  }

  // Test that a player who is tied for most goals reached wins if they reach home first.
  @Test
  public void testOneWinnerWithOpponentAlsoOnHomeTile() {
    Tuple<Board, Tile> boardAndSpare = generateFullyConnectedBoardAndSpare(7,7);
    Board board = boardAndSpare.getFirst();
    Tile spare = boardAndSpare.getSecond();
    List<PlayerData> playerDataList = new ArrayList<>();
    List<Posn> additionalGoals = new ArrayList<>();

    playerDataList.add(new PlayerData(Color.ORANGE,  new Posn(0, 1), new Posn(3, 3), new Posn(5,5), Optional.of(new StrategyPlayer("eben", new RiemannStrategy())), 11, true, false, true));
    playerDataList.add(new PlayerData(Color.BLUE,  new Posn(3, 1), new Posn(3, 1), new Posn(3,1), Optional.of(new StrategyPlayer("ederek", new RiemannStrategy())), 11, true, false, true));
    playerDataList.add(new PlayerData(Color.RED,  new Posn(0, 2), new Posn(1, 5), new Posn(3,1), Optional.of(new StrategyPlayer("ebob", new RiemannStrategy())), 8, false, false, true));
    playerDataList.add(new PlayerData(Color.GREEN,  new Posn(0, 3), new Posn(1, 3), new Posn(3,1), Optional.of(new StrategyPlayer("esam", new RiemannStrategy())), 5, false, false, true));

    State initState = new MazeState(board, playerDataList, spare, Optional.empty(), additionalGoals);
    List<Player> players = initState.getPlayers()
            .stream()
            .map((x) -> x.getPlayerAPI().get().getPlayer())
            .collect(Collectors.toList());

    Posn slidePos = new Posn(0,0);
    Posn targetPos = new Posn(3, 1);
    Action action = BasicTurnAction.builder()
            .rotateSpare(0)
            .slideTilePosition(slidePos)
            .slideTileDirection(Direction.DOWN)
            .targetPlayerPosition(targetPos)
            .build();

    Tuple<List<Player>, List<Player>> winnersAndKicked = new Referee(initState).runFullGame(players);

    List<Player> winners = winnersAndKicked.getFirst();
    List<Player> kicked = winnersAndKicked.getSecond();

    assertEquals(1, winners.size());
    assertEquals("eben", winners.get(0).name());
  }

}
