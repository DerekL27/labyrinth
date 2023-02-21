## Milestone 9 Programming Task Revisions

In the process of implementing the Milestone 9 Programming Task, revisions were made to the `State`, `Referee`, and `Player`.
Outlined below are the changes:

 - We added a list field to store and update the additional goals in the State. When generating a random initial State, this list of goals will be initialized with *number of Players* goals.
 - We renamed goal-checking boolean flags to more accurately represent a Player's progression through a Game with multiple goals rather than just one.
 - We changed the logic that occurs after a player has reached their goal, now the additional goals list is checked and a new goal is selected from there if one is present.

 Changes to Referee:
 - We changed the generate random state function to generate the random state with the goal in all player info objects set to null
 - We changed setup to assign goals to the player info objects in addition to calling setup on the actual player.
 - Since the setup players method is called right after the generate random state method, there is no possibility of the null leaking.

 Changes to the actual Player:
 - We did not change the actual player at all. The setup function that we implemented was already capable of handling multiple goals.