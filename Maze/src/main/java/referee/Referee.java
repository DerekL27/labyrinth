package referee;

import model.state.*;
import observer.Observer;
import util.Tuple;
import util.Util;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * The Referee is in charge of running an entire game to completion.
 * A game is complete if any of the following is satisfied:
 * - A player reaches its home tile after visiting its goal tile
 * - All players pass consecutively
 * - The referee plays 1000 rounds of the game
 *
 * The Referee is also in charge of removing Players for inappropriate behavior.
 * The referee kicks a player if:
 * - the player provides an invalid action
 * - the player throws an exception
 * - the player takes too long to respond
 *
 * Note: The Referee will properly kick a player that infinitely loops and run the game to
 * completion but the thread that is created to handle the action cannot be closed and must be
 * handled by the process that calls the runGame method.
 */
public class Referee {

  private static final int MAX_NUMBER_OF_ROUNDS = 1000;

  // The current State of the game
  private State state;

  // The number of rounds the Referee has completed
  private int numRoundsCompleted;

  private boolean allPlayersPassRound;

  private List<Observer> observers;

  private List<Player> kickedPlayers;

  private final int boardWidth;
  private final int boardHeight;

  /**
   * As of now, the functionality to request a board from players has not yet been implemented.
   * To test the Referee effectively, the initial State must be initialized. This constructor is
   * strictly for testing the functionality of the Referee with a controlled start state.
   * @param initialState the initial state the Referee starts the game with
   */
  public Referee(State initialState) {
    this.state = initialState;
    this.observers = new ArrayList<>();
    this.kickedPlayers = new ArrayList<>();
    this.allPlayersPassRound = false;
    this.boardWidth = state.getBoardWidth();
    this.boardHeight = state.getBoardHeight();
  }

  public Referee(int width, int height) {
    this.observers = new ArrayList<>();
    this.kickedPlayers = new ArrayList<>();
    this.allPlayersPassRound = false;
    this.boardWidth = width;
    this.boardHeight = height;
  }

  /**
   * Used to subscribe a given observer to this Referee. All Observers that are subscribed to this
   * Referee will be notified of every change in state and when the game is over.
   * @param observer the observer to add
   */
  public void subscribeObserver(Observer observer) {
    this.observers.add(observer);
  }

  /**
   * Runs the full game from beginning to end which includes the phases of setup, notifying the
   * players of the setup, running the game loop of players taking turns, and then determining
   * the winners at the end. This method returns a Tuple of two lists of players. The first list
   * in the tuple denotes the players that won the game. The second list in the tuple denotes the
   * players that were kicked from the game.
   *
   * @param players the list of players that are part of the game
   * @return a tuple that consists of the players that won and the players that were kicked
   */
  public Tuple<List<Player>, List<Player>> runFullGame(List<Player> players) {

    this.setUpInitialState(players);
    this.numRoundsCompleted = 0;
    this.setupAllPlayers();
    this.notifyObserversOfNewState();
    this.runGameLoop();
    List<Player> winners = this.calculateAndNotifyPlayersWhoWon();
    this.notifyObserversGameOver();
    return new Tuple<>(winners, kickedPlayers);

  }


  /**
   * Sets up the initial state of the game. Creates a new random state if one wasn't passed in
   * for testing.
   * @param players the list of players that are part of the game
   */
  private void setUpInitialState(List<Player> players) {
    if(state == null) {
      this.state = Util.createRandomState(players, boardWidth, boardHeight);
    } else {
      //Plug in any given players to the already initialized state (FOR TESTING ONLY)
      for(int i = 0; i < this.state.getPlayers().size(); i++) {
        this.state.updatePlayerAPI(i, players.get(i));
      }
    }
  }


  /**
   * Notifies all players within this current state of the initial State and their goal tile Position.
   * Loops through all players and calls the setup method on each player. If a player throws an exception
   * or returns an invalid response, the player is kicked from the game. The player is wrapped in a
   * safePlayer object to prevent exception throwing.
   */
  private void setupAllPlayers() {
    State currentState = this.state;
    int playerAmt = this.state.getPlayers().size();

    for (int i = 0; i < playerAmt; i++) {
      PlayerData player = currentState.whichPlayerTurn();

      SafePlayer safePlayerAPI = player.getPlayerAPI().get();
      Optional<Object> response = safePlayerAPI.setup(Optional.of(new PlayerStateWrapper(state, player)),
          player.getGoalLocation());

      if(response.isEmpty()) {
        this.kickedPlayers.add(safePlayerAPI.getPlayer());
        currentState = currentState.kickCurrentPlayer();
      }
      else {
        currentState = currentState.applyActionWithoutChecking(new PassAction());
      }
    }
    this.state = currentState;
  }

  /**
   * Determines if this game is complete based on the three criteria:
   * - A player reaches its home tile after visiting its goal tile
   * - All players pass consecutively
   * - The referee has played 1000 rounds of the game
   * @return if this game is complete
   */
  private boolean isGameComplete() {
    return numRoundsCompleted >= MAX_NUMBER_OF_ROUNDS || state.isGameOver() || this.allPlayersPassRound;
  }

  /**
   * Runs the actual game loop. While the game is not complete, a single round will run. The loop
   * will continue until the game is complete.
   */
  private void runGameLoop() {
    while(!this.isGameComplete()) {
      this.runSingleRound();
      this.numRoundsCompleted++;
    }
  }

  /**
   * Runs a single round of the game. If a player wins the game mid-round (the game is complete),
   * the round will stop short.
   */
  private void runSingleRound() {
    int playersInRound = state.getPlayers().size();
    int passes = 0;
    for(int i = 0; i < playersInRound && !this.isGameComplete(); i++) {
      if(!this.runTurn()){
        passes++;
      }
    }
    //Compares the number of players that passed to the number of surviving players
    if(passes == state.getPlayers().size()){
      this.allPlayersPassRound = true;
    }
  }

  /**
   * Runs the turn of the current player, kicking the player if its move is invalid.
   *
   * @return false if the player has passed their turn, true if not
   */
  private boolean runTurn() {
    PlayerData currentPlayer = state.whichPlayerTurn();
    SafePlayer safePlayerAPI = currentPlayer.getPlayerAPI().get();

    Optional<Action> action = safePlayerAPI.takeTurn(new PlayerStateWrapper(state, currentPlayer));

    if(action.isPresent() && state.canApplyAction(action.get())) {

      State nextState = state.applyActionWithoutChecking(action.get());

      state = getNextStateAfterNotify(nextState);

      this.notifyObserversOfNewState();

      return action.get().getPlannedBoardMove().isPresent();
    }
    else {
      state = state.kickCurrentPlayer();
      kickedPlayers.add(safePlayerAPI.getPlayer());
      this.notifyObserversOfNewState();
      return true; // getting kicked should not count towards pass calculation
    }
  }

  /**
   * Determines what the preceding state is from the current state. The given state is chosen if the
   * player correctly accepts the setup call or if there is no setup call. If the player does not
   * correctly accept the setup call (timeout/exception), then the current player is kicked from
   * the game.
   * @param nextState the state with the players action applied to it
   * @return the next state to become the current state
   */
  private State getNextStateAfterNotify(State nextState) {
    if(notifyPlayerIfReachedGoal(nextState)) {
      return nextState;
    }
    else {
      kickedPlayers.add(state.whichPlayerTurn().getPlayerAPI().get().getPlayer());
      return state.kickCurrentPlayer();
    }
  }

  /**
   * Notifies the current player if they reached their goal tile for the first time. If the player
   * does receive the setup call and does not respond correctly, this method returns false. Else,
   * the method returns true.
   * @param nextState the state that precedes this current state to check if the player moved onto
   *                 their goal tile for the first time
   * @return if the setup call was received successfully or if there was no setup call
   */
  private boolean notifyPlayerIfReachedGoal(State nextState) {
    PlayerData playerInfoBeforeMove = state.whichPlayerTurn();

    List<PlayerData> playersAfterNextState = nextState.getPlayers();
    PlayerData playerInfoAfterMove = playersAfterNextState.get(playersAfterNextState.size() - 1);
    SafePlayer safePlayerAfter = playerInfoAfterMove.getPlayerAPI().get();

    if(playerInfoBeforeMove.getNumGoalsVisited() < playerInfoAfterMove.getNumGoalsVisited()) {

      Optional<Object> response = safePlayerAfter.setup(Optional.empty(), playerInfoAfterMove.getGoalLocation());

      return response.isPresent();
    }
    return true;
  }

  /**
   * Determines which players won the game and notifies each player whether they won the game or not.
   * @return the list of players that won the game
   */
  private List<Player> calculateAndNotifyPlayersWhoWon() {
    List<PlayerData> playersInGame = state.getPlayers();
    List<Player> winners = calculateWinners();

    for(PlayerData playerData : playersInGame) {
      SafePlayer player = playerData.getPlayerAPI().get();
      Optional<Object> response = player.win(winners.contains(player.getPlayer()));
      if(response.isEmpty()) {
        winners.remove(player.getPlayer());
        kickedPlayers.add(player.getPlayer());
      }
    }
    return winners;
  }

  /**
   * Determines the players that have won the game.
   * @return the list of players that have won
   */
  private List<Player> calculateWinners() {

    List<PlayerData> players = state.getPlayers();

    if(players.isEmpty()) {
      return new ArrayList<>();
    }

    // Filter out players that don't have the maximum number of goals reached
    int maxNumGoalsReached = players.stream().mapToInt(PlayerData::getNumGoalsVisited).max().getAsInt();
    players = players.stream().filter(x -> x.getNumGoalsVisited() == maxNumGoalsReached).collect(Collectors.toList());

    //if a player in players has reached home, they are the sole winner
    Optional<PlayerData> winner = players.stream().filter(PlayerData::getHasReturnedHome).findFirst();

    if(winner.isPresent()) {
      return new ArrayList<>(List.of(winner.get().getPlayerAPI().get().getPlayer()));
    }
    else {
      ToIntFunction<PlayerData> distToTarget = (x -> x.getCurrentLocation().squareDistance(x.getGoalLocation()));
      return this.getClosestPlayersTo(players, distToTarget);
    }
  }

  /**
   * Determines the distance of each PlayerData using the given distance function and returns a
   * list of Player that contains the Player API for each PlayerData that has the lowest distance.
   *
   * @param players list of players
   * @param distanceFunc function to determine distance of player to tile
   * @return the players closest to a tile
   */
  private List<Player> getClosestPlayersTo(List<PlayerData> players, ToIntFunction<PlayerData> distanceFunc) {

    if(players.isEmpty()) {
      return new ArrayList<>();
    }

    List<Tuple<Integer, PlayerData>> distancesAndPlayers = players.stream()
        .map(x -> (new Tuple<>(distanceFunc.applyAsInt(x), x)))
        .sorted(Comparator.comparingInt(Tuple::getFirst))
        .collect(Collectors.toList());

    int closestDistance = distancesAndPlayers.get(0).getFirst();
    distancesAndPlayers.removeIf(currTuple -> currTuple.getFirst() != closestDistance);

    return distancesAndPlayers.stream()
        .map(x -> x.getSecond().getPlayerAPI().get().getPlayer())
        .collect(Collectors.toList());
  }


  /**
   * Notifies each observer that the referee has moved on to a new state. Each observer receives the
   * new current state.
   */
  private void notifyObserversOfNewState() {
    for(Observer observer : this.observers) {
      observer.notifyStateChange(this.state);
    }
  }

  /**
   * Notifies each observer that the game is over.
   */
  private void notifyObserversGameOver() {
    for(Observer observer : this.observers) {
      observer.notifyGameOver();
    }
  }
}
