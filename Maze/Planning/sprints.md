TO: Client

FROM: Akshay Dupuguntla and Nicholas Selvitelli

CC: Mattias Felleisen

DATE: September 30, 2022

SUBJECT: Sprint planning

The work planned for the first three sprints of the project are laid out as the following:

For the first sprint, we will focus on mainly setting up the project and creating the board representation.
This will entail setting up the project to build and test with Maven and JUnit, as well as creating basic
project hierarchy. After initial setup, we will then begin with the data representation of the maze.
This will include the board itself, tiles, gems, player pieces, and home tiles. We will also 
implement methods to create the board that is populated with a random variation of tiles. 

For the second sprint, we will build off the first one by adding tile movement to the board. This 
includes handling tile rotation as well as adding and removing a tile from a row/column. This sprint
will also focus on populating tiles with their pieces. In addition, logic will be added to move pieces
(e.g. moving the player from one tile to another). Following direct tile to tile movement, we will
add methods to move the player from one tile to a connected tile, ensuring that there is a valid
path for the player piece to move along.

For the third sprint, we will implement the notion of game states and player states. Game states 
refer to the information stored on the board after any given player's turn. This information
includes the board state, which player's turn it is, what the previous sliding action was, how many
turns it has been since the board state has changed, if the game state is a valid goal state, etc.
This sprint will also contain work that deals with the player state. The player state handles the
information needed to track a player between game states. This information includes a notion of the 
player's piece location, home location, the required gem to obtain, a notion of whether the gem has 
been obtained, etc.
