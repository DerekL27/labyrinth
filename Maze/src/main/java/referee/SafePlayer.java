package referee;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import model.board.Board;
import model.state.Action;
import model.state.PlayerStateWrapper;
import util.Posn;

/**
 * This is a Player decorator that safely runs each method of the given player. If the given player
 * throws an exception for any called method or the call to the method times out (computes for
 * longer than the MAX_WAIT_TIME), this SafePlayer will return an empty optional which
 * signifies an invalid move as described by the Referee. Infinite loops will be caught and handled
 * properly but the thread that runs the infinite loop will not be able to be killed and may cause
 * the program to run forever.
 *
 */
public class SafePlayer {

  private final Player player;

  private static final int MAX_WAIT_TIME = 4;
  private static final TimeUnit WAIT_TIME_UNIT = TimeUnit.SECONDS;

  public SafePlayer(Player player) {
    this.player = player;
  }

  /**
   * Returns the player that is decorated by this class
   */
  public Player getPlayer() {
    return this.player;
  }

  public Optional<String> name() {
    Callable<String> nameCallable = this.player::name;
    return this.safelyExecuteCallToPlayer(nameCallable);
  }

  public Optional<Board> proposeBoard0(int rows, int columns) {
    Callable<Board> proposeCallable = () -> this.player.proposeBoard0(rows, columns);
    return this.safelyExecuteCallToPlayer(proposeCallable);
  }

  public Optional<Object> setup(Optional<PlayerStateWrapper> state0, Posn goal) {
    Callable<Object> setupCallable = () -> this.player.setup(state0, goal);
    return this.safelyExecuteCallToPlayer(setupCallable);
  }

  public Optional<Action> takeTurn(PlayerStateWrapper s) {
    Callable<Action> takeTurnCallable = () -> this.player.takeTurn(s);
    return this.safelyExecuteCallToPlayer(takeTurnCallable);
  }

  public Optional<Object> win(Boolean won) {
    Callable<Object> winCallable = () -> this.player.win(won);
    return this.safelyExecuteCallToPlayer(winCallable);
  }

  /**
   * Safely executes a method call on the given player. The method is called within a future. If
   * the future throws an exception (due to timeout or a thrown exception), then the result is an
   * empty optional.
   * @param callable the method to call safely
   * @param <T> the return type of the method
   * @return the value of the called method if nothing went wrong; empty if something did go wrong
   */
  private <T> Optional<T> safelyExecuteCallToPlayer(Callable<T> callable) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<T> future = executor.submit(callable);
    try {
      Optional<T> ans =  Optional.of(future.get(MAX_WAIT_TIME, WAIT_TIME_UNIT));
      executor.shutdownNow();
      return ans;
    }
    catch (Exception | Error e) {
      future.cancel(true);
      executor.shutdownNow();
      return Optional.empty();
    }
  }
}
