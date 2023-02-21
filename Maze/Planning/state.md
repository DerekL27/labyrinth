TO: Mattias Felleisen and Ben Lerner

FROM: Akshay Dupuguntla and Nicholas Selvitelli

DATE: October 6, 2022

SUBJECT: Game State Data Representation Design

We have designed an initial draft of the game state. The game state consists of a central "GameState" data representation coupled with a few auxiliary data representations (PlayerData and Action).

The GameState keeps track of the current board state, an ordered list of PlayerData, an int representing the number of consecutive passes before this GameState, and the previous GameState.

With this data, the GameState has the following methods to allow the referee to run an entire game from start to finish: one to determine which player is allowed to take their turn, one that checks if a given Action and Player can be enacted on the board, one that uses the given action and player on the board, one that determines if the game is over, and one that determines who the winner is.

The GameState's method to check if a given action can be enacted ensures that the given player is the player with the current turn and that the given action is valid. The method to act uses the given action on the current GameState and then returns a new GameState to be the following GameState from this one.

The PlayerData is a class used to hold specific information about each player. Most importantly, the PlayerData is responsible for knowing an individual player's age (int), current location (Posn), home location (Posn), target treasure, and whether the player has already picked up their treasure (boolean).

Aside from simple getters and setters, the PlayerData is responsible for sending and receiving messages from the player controller.

The Action is an interface to execute a complete action on the Board with a specified player (PlayerData) and spare Tile via the Visitor Pattern. An Action has two methods required to complete an action for a single game state. One method checks if the action can be applied on the given board with the player and spare tile. The second method acts on the board, mutating both the Board and player while returning the new spare Tile. At the moment, an Action is either a "Pass" (no change) or a complete player move (slide a row/column, insert a (rotated) tile, and move the Player piece).
