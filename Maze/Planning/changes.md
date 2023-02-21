TO: Mattias Felleisen and Ben Lerner

FROM: Derek Leung and Nicholas Selvitelli

DATE: November 7th, 2022

SUBJECT: Redesign Task

After reviewing the potential game changing features, we have produced an assessment on the impact
of each change. 

The addition of blank tiles for the board has been ranked as a 1 out of 5 level of 
difficulty. Our reasoning for this low ranking is that we would only need to create a new tile class
that implements the Tile interface. We estimate that there will be little to no changes to the board
code.

Allowing goals to be movable tiles will be more involved and has been ranked a 3 out of 5 level of
difficulty. This change will require the goal positions for each player to be updated after every 
action, which will require the addition of at least two helper methods. One method would loop 
through each player determining if their goal position is on a slide, the second method would adjust
the Player goals accordingly and notify each player of its goal change.

Asking a player to sequentially pursue several goals, one at a time has been ranked a 1 out of 5 
level of difficulty. This change should only require a signature change for the setup method in the 
Player interface.