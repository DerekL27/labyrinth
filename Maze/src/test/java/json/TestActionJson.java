package json;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.state.Action;
import model.state.BasicTurnAction;
import model.state.PassAction;
import org.junit.jupiter.api.Test;

public class TestActionJson {


  @Test
  public void testSerializePass() throws JsonProcessingException {

    ObjectMapper mapper = JsonUtils.getMapper();

    ActionJson actionJson = mapper.readValue("\"PASS\"", ActionJson.class);

    assertTrue(actionJson.build() instanceof PassAction);
  }

  @Test
  public void testSerializeBasicAction() throws JsonProcessingException {

    ObjectMapper mapper = JsonUtils.getMapper();

    ActionJson actionJson = mapper.readValue("[1, \"UP\", 90, { \"row#\": 1, \"column#\": 1}]", ActionJson.class);

    Action action = actionJson.build();

    assertTrue(action instanceof BasicTurnAction);
  }

}
