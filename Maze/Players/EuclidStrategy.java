package model.strategy;

import java.util.Comparator;
import java.util.List;
import util.Posn;

/**
 * Order the candidates by their distance to the target posn.
 */
public class EuclidStrategy extends AbstractBasicStrategy {

  @Override
  protected Comparator<Posn> getCandidatesComparator(Posn target) {
    return Comparator.comparingInt(x -> x.squareDistance(target));
  }
}
