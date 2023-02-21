The Test Harnesses
---

## Purpose

This directory contains all Integration Test Harnesses. Each file in this directory is responsible
for a single Test Harness. The main structure for a Test Harness file looks like the following:

```java
  public static void main(String[] args) {
    InputStream inputStream = System.in;
    PrintStream outputStream = System.out;
    testHarness(inputStream, outputStream);
  }


  public static boolean testHarness(InputStream inputStream, PrintStream outputStream) {
    ...
  }
```

## Contents

- [BadPlayerHarness](BadPlayerHarness.java)
- [BoardHarness](BoardHarness.java)
- [ObserverHarness](ObserverHarness.java)
- [RefereeHarness](RefereeHarness.java)
- [StateHarness](StateHarness.java)
- [StrategyHarness](StrategyHarness.java)

### File Descriptions

**BadPlayerHarness**: Located in [7](../../../../../7) and [8](../../../../../8)
- **Task:** Creates Players based on their specified strategies and runs a complete game on the given
  RefereeState to completion. Some given players will throw an exception or run in an infinite loop when a method is called on 
  them, they must appear in the kicked list at the end of the game.
- **Input:** *BadPlayerSpec*, *RefereeState* **OR** *BadPlayerSpec2*, *RefereeState*
- **Output:** An array of 2 arrays. The first sub-array contains the names of the winners of the game
  sorted in alphabetical order. The second sub-array contains the names of the kicked players sorted 
  in alphabetical order.

**BoardHarness**: Located in [3](../../../../../3)
- **Task:** Find all reachable Tiles in the board from the given *Coordinate*
- **Input:** *Board*, *Coordinate*
- **Output:** An array of *Coordinates* sorted in row-column order

**ObserverHarness**: Located in [6](../../../../../6)
- **Task:** The same as the RefereeHarness but this test has a single Observer subscribed to the 
  Referee.
- **Input:** *PlayerSpec*, *RefereeState*
- **Output:** An array consisting of the names of the winners of the game sorted in alphabetical order

**RefereeHarness**: Located in [6](../../../../../6)
- **Task:** Creates Players based on their specified strategies and runs a complete game on the given
  RefereeState to completion.
- **Input:** *PlayerSpec*, *RefereeState*
- **Output:** An array consisting of the names of the winners of the game sorted in alphabetical order

**StateHarness**: Located in [4](../../../../../4)
- **Task:** Enacts the given action on the state and then computes the coordinates the current 
  player can move to.
- **Input:** *State*, *Index*, *Direction*, *Degree*
- **Output:** An array of *Coordinates* sorted in row-column order

**StrategyHarness**: Located in [5](../../../../../5)
- **Task:** Compute the given strategy's action based on the given state and goal position.
- **Input:** *Strategy Designation*, *State*, *Coordinate*
- **Output:** *Choice*
