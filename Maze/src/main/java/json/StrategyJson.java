package json;

import model.strategy.EuclidStrategy;
import model.strategy.RiemannStrategy;
import model.strategy.Strategy;

/**
 * Given a Strategy Designation as a Json String, produce a valid strategy to use.
 * This is done with the private enum used to map names.
 * @see Strategy
 * @see <a href="https://course.ccs.neu.edu/cs4500f22/5.html#%28tech._strategy._designation%29">Strategy JSON Documentation</a>
 */
public class StrategyJson {

  private final String designation;

  public StrategyJson(String designation) {
    this.designation = designation;
  }


  public Strategy getStrategy() {
    return StrategyDesignation.valueOf(this.designation).getStrategy();
  }


  private enum StrategyDesignation {
    Euclid(new EuclidStrategy()),
    Riemann(new RiemannStrategy());

    private final Strategy strategy;

    StrategyDesignation(Strategy strategy) {
      this.strategy = strategy;
    }

    Strategy getStrategy() {
      return this.strategy;
    }

  }
}
