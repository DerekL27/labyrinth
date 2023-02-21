TO: Mattias Felleisen and Ben Lerner

FROM: Akshay Dupuguntla and Nicholas Selvitelli

DATE: October 19, 2022

SUBJECT: The Player Protocol Design

We have devised a protocol for communication between each player and the referee. First, each player
must request to join the referee's game. This request also contains the player information needed to
populate the PlayerData stored for a player in each state. If the player can join the game, the
referee will send a simple response to confirm that the player has joined the game. When the game
is in play, for each player's turn, the referee will send parts of the state specific to that player.
That is, the player will only receive the information that they are supposed to know. The player 
can then send a response, which is its action, back to the referee. If the action is acceptable,
the referee updates its state with the player's action. If the action is unacceptable, then the state
sends an error message to the player, indicating that the player must send a new response. The 
referee then moves on to the next player and sends and receives information specific to that player. 
If the player does not respond in time to the request, then the referee sends the player a 
kick message and kicks the player from the game. This continues until a game over state is reached.
When a game over state is reached, the referee sends a message to each player indicating that the 
game is over and who the winner is. Pictured below is an illustration of our protocol design.




####UML Diagram
```
Referee                  Player 1                     Player 2                     Player 3
   |       reqJoin(Pdata1)  |                            |                            |
   |<-----------------------+                            |                            |
   |      accepted()        |                            |                            |
   +----------------------->|                            |                            |
   |                        |        reqJoin(Pdata2)     |                            |
   |<---------------------<   <--------------------------+                            |
   |                        |          accepted()        |                            |
   +---------------------->   >------------------------->|                            |
   |                        |                            |       reqJoin(Pdata3)      |
   |<---------------------<   <------------------------>   >--------------------------+
   |                        |                             |       accepted()          |
   +---------------------->   >------------------------>   >------------------------->|
   ...
   |      QM(PState 1)      |                            |                            |
   +----------------------->|                            |                            |
   |    Response(PAction)   |                            |                            |
   |<-----------------------+                            |                            |
   |                        |        QM(PState 2)        |                            |
   +---------------------->   >------------------------->|                            |
   |                        | Response(invalid PAction)  |                            |
   |<---------------------<   <--------------------------+                            |
   |                                    error()          |                            |
   +---------------------->   >------------------------->|                            |
   |                        | Response(valid PAction)    |                            |
   |<---------------------<   <--------------------------+                            |
   |                        |                            |         QM(PState 3)       |
   +---------------------->   >------------------------>   >------------------------->|
   ...
   |                        |                            |         kicked()           |
   +---------------------->   >------------------------>   >------------------------->|
   |      QM(PState 1)      |                            |                            |
   +----------------------->|                            |                            |
   |    Response(PAction)   |                            |                            |
   |<-----------------------+                            |                            |
   ...
   | kickGameOver(winner)   |                            |                            |
   +----------------------->|                            |                            |
   +---------------------->   >------------------------->|                            |
```
