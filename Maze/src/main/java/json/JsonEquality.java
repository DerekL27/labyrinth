package json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import harness.BadPlayerHarness;
import harness.BoardHarness;
import harness.RefereeHarness;
import harness.StateHarness;
import harness.StrategyHarness;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


/**
 * Simple class to ensure the json output of a directory of integration tests have the correct
 * expected output json.
 */
public class JsonEquality {

  // expects arg input: [harness-type] [path to tests]
  public static void main(String[] args) throws IOException {

    String harnessName = args[0];
    BiFunction<InputStream, PrintStream, Boolean> harness = pickHarness(harnessName);

    //fix space in file path issue
    StringBuilder testsDirectory = new StringBuilder(args[1]);
    for(int i = 2; i < args.length; i++) {
      testsDirectory.append(" ").append(args[i]);
    }

    if(!"/".equals(testsDirectory.substring(testsDirectory.length() - 1))) {
      testsDirectory.append("/");
    }


    List<String> fileNames = Arrays.stream(new File(testsDirectory.toString()).listFiles())
        .filter(file -> !file.isDirectory() && file.getAbsolutePath().contains(".json"))
        .map(File::getName).collect(Collectors.toList());

    List<String> tests = fileNames.stream()
        .map((x -> x.replaceFirst("-in.json", "").replaceFirst("-out.json", "")))
        .distinct()
        .collect(Collectors.toList());

    int numSucceeded = 0;

    for(String testName : tests) {

      InputStream expected = new FileInputStream(testsDirectory.toString() + testName + "-out.json");
      InputStream test = new FileInputStream(testsDirectory.toString() + testName + "-in.json");

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream output = new PrintStream(out);

      try {
        harness.apply(test, output);

        String harnessOutput = out.toString();

        ObjectMapper mapper = JsonUtils.getMapper();
        JsonParser inParser = JsonUtils.getJsonParser(new ByteArrayInputStream(harnessOutput.getBytes()), mapper);
        JsonParser exParser = JsonUtils.getJsonParser(expected, mapper);

        JsonNode input = mapper.readTree(inParser);
        JsonNode expectedOut = mapper.readTree(exParser);

        boolean success = input.equals(expectedOut);


        if(!success) {
          System.out.println("Test " + testName + " failed");
          System.out.println("expected:\n" + expectedOut
              + "\n\nreceived:\n" + input + "\n");
        }
        else {
          numSucceeded++;
        }
      }
      catch (Exception e) {
        System.out.println("test " + testName + " threw this exception: " + e.getMessage());

      }
    }

    System.out.println("\nRan the " + harnessName + " test harness with tests located at [" + testsDirectory + "]");
    System.out.println(numSucceeded + " out of " + tests.size() + " tests succeeded.");
    System.exit(0);
  }

  private static BiFunction<InputStream, PrintStream, Boolean> pickHarness(String harnessName) {
    switch(harnessName) {
      case "board":
        return BoardHarness::boardTestHarness;
      case "state":
        return StateHarness::stateTestHarness;
      case "strategy":
        return StrategyHarness::strategyTestHarness;
      case "referee":
        return RefereeHarness::refereeTestHarness;
      case "bad":
        return BadPlayerHarness::badPlayerTestHarness;
      default:
        throw new IllegalArgumentException("Unknown harness");
    }
  }

}
