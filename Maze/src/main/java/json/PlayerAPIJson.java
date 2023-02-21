package json;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Optional;
import referee.BadPlayer;
import referee.Player;
import referee.StrategyPlayer;

/**
 * Class for deserializing an actual Player (not player info).
 * @see Player
 * @see StrategyPlayer
 * @see BadPlayer
 * @see <a href="https://course.ccs.neu.edu/cs4500f22/8.html#%28tech._badps2%29">BadPS2 JSON Dcoumentation</a>
 */
@JsonFormat(shape = Shape.ARRAY)
@JsonPropertyOrder({"name", "strategy", "badFM", "count"})
public class PlayerAPIJson {

  private String name;
  private StrategyJson strategy;
  private Optional<BadFM> badFM;
  private int count;

  @JsonCreator
  public PlayerAPIJson(@JsonProperty("name") String name, @JsonProperty("strategy") StrategyJson strategy) {
    this.name = name;
    this.strategy = strategy;
    this.badFM = Optional.empty();
    this.count = 0;
  }

  @JsonProperty("badFM")
  public void setBadFM(String fm) {
    this.badFM = Optional.of(BadFM.valueOf(fm));
  }

  @JsonProperty("count")
  public void setCount(int count) {
    this.count = count;
  }

  public Player build() {
    if(badFM.isPresent()) {
      return new BadPlayer(this.name, this.strategy.getStrategy(), this.badFM.get(), this.count);
    }
    return new StrategyPlayer(this.name, this.strategy.getStrategy());
  }


  public enum BadFM {
    setUp, takeTurn, win
  }
}
