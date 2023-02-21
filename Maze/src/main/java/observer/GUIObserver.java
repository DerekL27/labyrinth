package observer;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import json.JsonUtils;
import json.StateJson;
import model.state.State;
import view.DrawObserverView;

/**
 * Represents an observer that visualizes the state using a GUI.
 */
public class GUIObserver implements Observer {

  private Queue<State> states;
  private boolean isGameOver;
  private DrawObserverView view;

  public GUIObserver() {
    states = new LinkedList<>();
    isGameOver = false;
    view = new DrawObserverView(this);
  }

  /**
   * Displays the next state if there is one available.
   */
  public void nextState() {
    // do not remove the final state
    if(states.size() > 1) {
      states.remove();
    }
    if(!states.isEmpty()) {
      State current = states.peek();
      view.drawView(current);
    }
  }

  /**
   * Saves the current state as the given file.
   * @param file the file to save to
   */
  public void saveState(File file) {
    if(!states.isEmpty()) {
      JsonNode stateNode = StateJson.serializeRefereeState(states.peek());
      try {
        String stateString = JsonUtils.writeObjectToJson(stateNode);
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write(stateString.getBytes());
      }
      catch (IOException e) {
        System.out.println("Unable to save file");
      }
    }
  }

  @Override
  public void notifyStateChange(State state) {
    if(!this.isGameOver){
      if(this.states.isEmpty()) { // draw initial state when it appears
        this.view.drawView(state);
      }
      this.states.add(state);
    }
  }

  @Override
  public void notifyGameOver() {
    this.isGameOver = true;
  }
}
