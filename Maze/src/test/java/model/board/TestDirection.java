package model.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import util.Direction;
import util.Posn;

public class TestDirection {

  @Test
  public void testGetClockwiseRotation() {

    assertSame(Direction.LEFT, Direction.getClockwiseRotation(Direction.DOWN));
    assertSame(Direction.UP, Direction.getClockwiseRotation(Direction.LEFT));
    assertSame(Direction.RIGHT, Direction.getClockwiseRotation(Direction.UP));
    assertSame(Direction.DOWN, Direction.getClockwiseRotation(Direction.RIGHT));

  }

  @Test
  public void testOffsetPosnWithDirection() {
    Posn pos = new Posn(0, 0);
    Posn rightPos = new Posn(1, 0);
    Posn leftPos = new Posn(-1, 0);
    Posn upPos = new Posn(0, -1);
    Posn downPos = new Posn(0, 1);

    Posn outcome = Direction.offsetPosnWithDirection(Direction.RIGHT, pos, 1);
    assertEquals(rightPos, outcome);
    outcome = Direction.offsetPosnWithDirection(Direction.LEFT, pos, 1);
    assertEquals(leftPos, outcome);
    outcome = Direction.offsetPosnWithDirection(Direction.UP, pos, 1);
    assertEquals(upPos, outcome);
    outcome = Direction.offsetPosnWithDirection(Direction.DOWN, pos, 1);
    assertEquals(downPos, outcome);

    pos = new Posn(30, 30);
    Posn expected = new Posn(30, 50);
    outcome = Direction.offsetPosnWithDirection(Direction.DOWN, pos, 20);
    assertEquals(expected, outcome);

    pos = new Posn(5, 5);
    outcome = Direction.offsetPosnWithDirection(Direction.LEFT, pos, -10);
    expected = new Posn(15, 5);
    assertEquals(expected, outcome);

  }

  @Test
  public void testGetNClockwiseRotations() {
    // Tests single rotation
    assertEquals(Direction.LEFT, Direction.getNClockwiseRotations(Direction.DOWN, 1));
    assertEquals(Direction.UP, Direction.getNClockwiseRotations(Direction.LEFT, 1));
    assertEquals(Direction.RIGHT, Direction.getNClockwiseRotations(Direction.UP, 1));
    assertEquals(Direction.DOWN, Direction.getNClockwiseRotations(Direction.RIGHT, 1));

    // Tests multiple rotations
    assertEquals(Direction.UP, Direction.getNClockwiseRotations(Direction.DOWN, 2));
    assertEquals(Direction.RIGHT, Direction.getNClockwiseRotations(Direction.LEFT, 2));
    assertEquals(Direction.DOWN, Direction.getNClockwiseRotations(Direction.UP, 2));
    assertEquals(Direction.LEFT, Direction.getNClockwiseRotations(Direction.RIGHT, 2));

    // Tests multiple rotations that is greater than length of all Direction values
    assertEquals(Direction.UP, Direction.getNClockwiseRotations(Direction.DOWN, 6));
    assertEquals(Direction.RIGHT, Direction.getNClockwiseRotations(Direction.LEFT, 6));
    assertEquals(Direction.DOWN, Direction.getNClockwiseRotations(Direction.UP, 6));
    assertEquals(Direction.LEFT, Direction.getNClockwiseRotations(Direction.RIGHT, 6));
  }

//  @Test
//  public void testSpecialCharToDirections() {
//    assertEquals(Arrays.asList(Direction.UP, Direction.DOWN), Direction.specialCharToDirections('│'));
//    assertEquals(Arrays.asList(Direction.LEFT, Direction.RIGHT), Direction.specialCharToDirections('─'));
//    assertEquals(Arrays.asList(Direction.DOWN, Direction.LEFT), Direction.specialCharToDirections('┐'));
//    assertEquals(Arrays.asList(Direction.UP, Direction.RIGHT), Direction.specialCharToDirections('└'));
//    assertEquals(Arrays.asList(Direction.DOWN, Direction.RIGHT), Direction.specialCharToDirections('┌'));
//    assertEquals(Arrays.asList(Direction.UP, Direction.LEFT), Direction.specialCharToDirections('┘'));
//    assertEquals(Arrays.asList(Direction.DOWN, Direction.LEFT, Direction.RIGHT), Direction.specialCharToDirections('┬'));
//    assertEquals(Arrays.asList(Direction.UP, Direction.DOWN, Direction.RIGHT), Direction.specialCharToDirections('├'));
//    assertEquals(Arrays.asList(Direction.UP, Direction.LEFT, Direction.RIGHT), Direction.specialCharToDirections('┴'));
//    assertEquals(Arrays.asList(Direction.UP, Direction.DOWN, Direction.LEFT), Direction.specialCharToDirections('┤'));
//    assertEquals(Arrays.asList(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT), Direction.specialCharToDirections('┼'));
//  }


}
