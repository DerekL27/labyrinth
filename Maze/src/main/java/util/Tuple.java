package util;

public class Tuple<T, V> {

  private T first;
  private V second;

  public Tuple(T first, V second) {
    this.first = first;
    this.second = second;
  }

  public T getFirst() {
    return first;
  }

  public V getSecond() {
    return second;
  }

  @Override
  public boolean equals(Object other) {
    if(other instanceof Tuple) {
      Tuple o = (Tuple)other;
      return first.equals(o.first) && second.equals(o.second);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return first.hashCode() + second.hashCode();
  }
}
