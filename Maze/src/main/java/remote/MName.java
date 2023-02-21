package remote;


/**
 * This represents the method names that will be sent over JSON
 */
public enum MName {
  SETUP("setup"), TAKE_TURN("take-turn"), WIN("win");

  private final String name;

  MName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }

  public static MName fromString(String str) {
    for(MName val : values()) {
      if(str.equals(val.toString())) {
        return val;
      }
    }
    throw new IllegalArgumentException("Given string that is not contained in this enum.");
  }
}