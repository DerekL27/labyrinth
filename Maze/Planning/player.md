TO: Mattias Felleisen and Ben Lerner

FROM: Akshay Dupuguntla and Nicholas Selvitelli

DATE: October 13, 2022

SUBJECT: Player Data Representation and Player Interface Design

We have designed our initial draft of our PlayerDataRep, which is the data a player needs to know to
be able to act. The data contained in PlayerDataRep would be a deep copy of some of the information
that the Referee knows. This data includes the board and the spare tile, which is necessary for the
player to determine how to make their next move, the current location of all the players, the home
tiles of all the players, and a boolean representation of whether it is the player's turn or not.

PlayerDataRep
- Board: board
- Tile: spare tile
- All Players' current locations
- all player home tiles
    - PlayerDataObserver: all player information (only viewable, Observer pattern)
- boolean: if it is their turn


Our plan for the Player interface consists of two main mechanics that drive the functionality of a
Player. The Player must be able to receive information about the game and then compute a turn for
the player to take based on the information provided. The Player must also be able to join and
leave the game. With the two mechanics in mind, our wishlist looks like the following:

Player Interface:
- PlayerAction computeAction(PlayerData data)
    - method referee calls to the player to receive the player's action
    - consumes PlayerDataRep, computes action
- void leaveTheGame()
    - consumes: nothing
    - computes: requests the Referee to leave the game
- ClientConnection joinTheGame(Referee server)
    - consumes: which Referee to ask to join their game
    - computes: a client socket connected to the server

PlayerAction is one of:
- PassPlayerAction
- BasicTurnPlayerAction
    - holds information needed to execute a basic turn on the server
    - how many degrees to rotate the spare tile
    - which col/row to slide
    - the sliding direction
    - the target position to move the player

The action computing mechanism uses a PlayerAction data representation to hold the information
needed for the Referee to build and execute an Action. This action mechanism has been broken up into
two methods. The first method, "computeAction," consumes the PlayerData representation (described above)
and computes the action the Player will take based on the given information. The player must also
be able to join and exit a game instance. This mechanism is handled with the two methods "joinTheGame" and
"leaveTheGame." The join method consumes a Referee, so it can request to join that specific Referee's game.
The leave method takes no parameters as we assume the Player has saved a connection to the Referee and
then stops its connection with the Referee.
