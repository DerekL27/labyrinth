package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestUtil {


  @Test
  public void testListsContainSameItems() {

    List<Integer> a = Arrays.asList(1, 2, 3, 4, 5);
    List<Integer> b = Arrays.asList(5, 4, 2, 3, 1);

    assertTrue(Util.listsContainSameItems(a, b));
    assertTrue(Util.listsContainSameItems(b, a));

    b = Arrays.asList(1, 2, 3, 4, 5);
    assertTrue(Util.listsContainSameItems(a, b));

    b = Collections.emptyList();
    assertFalse(Util.listsContainSameItems(a, b));

    b = Arrays.asList(1, 2, 3, 5);
    assertFalse(Util.listsContainSameItems(a, b));

    b = Arrays.asList(10);
    assertFalse(Util.listsContainSameItems(a, b));
  }

  @Test
  public void testReverseList() {

    List<Integer> list1 = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
    List<Integer> expected1 = new ArrayList<>(Arrays.asList(5, 4, 3, 2, 1));

    Util.reverseList(list1);
    assertEquals(expected1, list1);
  }
}
