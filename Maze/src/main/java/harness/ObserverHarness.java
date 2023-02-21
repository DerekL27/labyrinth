package harness;


import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import observer.GUIObserver;
import observer.Observer;

public class ObserverHarness {

  public static void main(String[] args) {
    InputStream inputStream = System.in;
    PrintStream outputStream = System.out;

    Observer observer = new GUIObserver();
    RefereeHarness.refereeTestHarness(inputStream, outputStream, Arrays.asList(observer));
  }

}
