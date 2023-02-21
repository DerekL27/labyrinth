package model.strategy;

import java.util.Comparator;
import util.Posn;

/**
 * Keep the order of candidates from left to right, top to bottom.
 */
public class RiemannStrategy extends AbstractBasicStrategy {

  @Override
  protected Comparator<Posn> getCandidatesComparator(Posn target) {
    return Posn::compareTo;
  }
}
