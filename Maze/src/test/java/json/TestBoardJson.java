package json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import model.board.Board;
import model.board.Gem;
import model.board.Tile;
import org.junit.jupiter.api.Test;
import util.Direction;
import util.Posn;
import util.Util;

public class TestBoardJson {


  @Test
  public void testBuildBoardSimple() throws IOException {

    String simpleBoard = "{\n"
        + "    \"connectors\":\n"
        + "    [[\"┼\",\"┼\"],\n"
        + "      [\"┼\",\"┼\"]],\n"
        + "    \"treasures\":\n"
        + "    [[[\"zoisite\",\"zircon\"],[\"zircon\",\"yellow-jasper\"]],\n"
        + "      [[\"unakite\",\"tourmaline\"],[\"tourmaline\",\"tourmaline-laser-cut\"]]]\n"
        + "  }";


    BoardJson json = JsonUtils.deserialize(new ByteArrayInputStream(simpleBoard.getBytes()), BoardJson.class);

    Board board = json.buildBoard();

    assertEquals(2, board.getBoardWidth());
    assertEquals(2, board.getBoardHeight());
    assertTrue(board.isBoardBuilt());


    Tile topLeft = board.getTile(new Posn(0, 0)).get();
    assertEquals(EnumSet.allOf(Direction.class), topLeft.getTileDirections());
    assertTrue(Util.listsContainSameItems(Arrays.asList(Gem.ZOISITE, Gem.ZIRCON), topLeft.getGems()));
  }

  @Test
  public void testBuildBoardIllegalWidthDimension() throws IOException {

    String badBoard = "{\n"
        + "    \"connectors\":\n"
        + "    [[]],\n"
        + "    \"treasures\":\n"
        + "    [[]]\n"
        + "  }";

    BoardJson json = JsonUtils.deserialize(new ByteArrayInputStream(badBoard.getBytes()), BoardJson.class);

    try {
      Board board = json.buildBoard();
    }
    catch (IllegalArgumentException e) {
      return;
    }
    fail("Exception not caught");
  }

  @Test
  public void testBuildBoardIllegalHeightDimension() throws IOException {

    String badBoard = "{\n"
        + "    \"connectors\":\n"
        + "    [],\n"
        + "    \"treasures\":\n"
        + "    []\n"
        + "  }";

    BoardJson json = JsonUtils.deserialize(new ByteArrayInputStream(badBoard.getBytes()), BoardJson.class);

    try {
      Board board = json.buildBoard();
    }
    catch (IllegalArgumentException e) {
      return;
    }
    fail("Exception not caught");
  }

  @Test
  public void testSerializeBoard() throws IOException {

  }

}
