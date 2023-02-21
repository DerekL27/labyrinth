package model.state;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.Color;
import java.util.Optional;
import util.Direction;
import org.junit.jupiter.api.Test;
import util.Posn;
import util.Tuple;

public class TestPlayerData {


  @Test
  public void testUpdateCurrentLocationIfOnSlide() {

    PlayerData player1 = new PlayerData(
        Color.RED,
        new Posn(0,0),
        new Posn(1,0), 0, false
    );
    Optional<Tuple<Integer, Direction>> move = Optional.of(new Tuple<>(1, Direction.RIGHT));
    PlayerData updatePlayer1 = player1.updateCurrentLocationIfOnSlide(move, 7, 7);

    assertEquals(player1, updatePlayer1);

    move = Optional.of(new Tuple<>(0, Direction.RIGHT));
    updatePlayer1 = player1.updateCurrentLocationIfOnSlide(move, 7, 7);

    assertNotEquals(player1, updatePlayer1);
    assertEquals(new Posn(1, 0), updatePlayer1.getCurrentLocation());
  }


}
