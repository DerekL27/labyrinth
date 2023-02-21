
Design & implement a data representation for the referee’s game states. 

This game state comprises **three essential pieces** of knowledge: the 
- current state of the board 
- the spare tile 
- and the players’ assigned avatars, homes, goal tiles, and current positions.

The data representations should implement the following pieces of functionality:
rotate the spare tile by some number of degrees;

shift a row/column by inserting the spare tile plus moving the player from the newly created spare 
tile to the just inserted one if needed;

determine whether the currently active player can reach a given tile;

check whether the active player’s move has placed it on its goal tile;

kick out the currently active player.

If you think other pieces of functionality will be needed, you may implement them.

Think A player’s knowledge about the game state differs from that of the referee. How? How would you change the representation to reflect this difference?


---

GameState:
- deep, immutable copy of the board state
- list of players in the current game
- int consecutivePlayerPasses
- Tile spareTile
- GameState previous state

GameState(Board b, List<PlayerData> players, int consecutivePlayerPasses, Tile spareTile, GameState previousState) { ... }
- constructor to take in all the data
- checks if this state is a winning state
  - notifies players of winner

GameState(List<PlayerData> players)
- constructor to:
 - generate the game
 - designate player order
 - instantiates the first spare tile

PlayerData whichPlayerTurn()
- returns playerWithCurrentTurn

boolean canApplyAction(Player p, Action a)
- checks that the player doing the action is the player with the current turn
- checks that the action is valid with Action.isValid(...)

GameState applyActionSafely(PlayerData p, Action a)
- Visitor pattern for action
- checks that canApplyAction(...) is true before running
- returns the next GameState that comes from using this action
- next game state takes 'this' as the prev game state
- pass in a deep copy of board to be able to detect board changes
- pass in spare tile returned from action.accept(...)
- if tile is equal to previous spare tile, then we consider the action as a pass
 - increment the passCounter for the next GameState
 - if not, reset the passCounter

boolean isGameOver()

Optional<PlayerData> getWinner()



PlayerData:
- name
- avatar
- age
- current location
- target gem
- home base location
- boolean visitedTargetGem
- Connection playerConnection

getters and setters

void sendToConnection(GameState)
Action listenForConnectionFeedback(GameState currentState)
..yada yada



Action:
action is one of:
- slide column, rotate spare, insert spare, move player piece
- pass
- stores data from ActionBuilder

boolean isValidActionOn(Board board, Tile spare, Player player)
- gets a deep copy of the board
- enacts all sub-actions on the board copy
- returns true if all sub-actions are able to work on the board copy

Tile accept(Board board, Tile spare, Player player)
- ensures all sub-actions are legal before enacting any sub-actions
- mutates the board, (optional) spare tile, and player
- steps:
 - if isValid(), then enact all actions on the real board
 - return the spare Tile

ActionBuilder:
- either pass,
- or:
 - rotate spare tile
 - slide column and insert spare (slideSafely)
 - move player

ActionBuilder pass()
ActionBuilder rotateSpare(n)
ActionBuilder slideAndAdd(Posn pos, Direction dir)
ActionBuilder movePlayer(Posn target)
Action build()
