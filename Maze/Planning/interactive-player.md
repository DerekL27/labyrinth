TO: Mattias Felleisen and Ben Lerner

FROM: Akshay Dupuguntla and Nicholas Selvitelli

DATE: October 27th, 2022

SUBJECT: Interactive Player Mechanism

For a human to act as a player for this game, an interactive player mechanism is needed for the human
to interact with the game. To accomplish this, there are parts of this mechanism that are needed for
a player to make decisions and make a turn. 

First, the full board of tiles and the spare tile need to be displayed. On each tile, 
the paths that are available to be taken and the gems (treasure) need to be visible to the player.
Next, it should be indicated what the next target tile is (either goal or home), so the player knows
where they must go next. The current locations of players and their home locations should also be displayed
over the board on the correct tile.

For the player to be able to make a turn, the player should be able to select what type of turn they would like
to make, either a pass or an action. If they would like to make an action, they need to be able to 
select a row/column, the direction they wish to slide, and rotate the spare tile that they are sliding in. 
This turn should be indicated in some way, so the player has clear understanding of the total move. 
If they do wish to make an action, they should be able to check whether the action is valid so that 
they don't make an invalid move and end up being kicked.