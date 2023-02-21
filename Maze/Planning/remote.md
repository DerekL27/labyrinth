TO: Client

FROM: Akshay Dupuguntla and Nicholas Selvitelli

CC: Mattias Felleisen

DATE: November 3rd, 2022

SUBJECT: Remote-Proxy Design

Our plan for the implementation of remotely connected players requires minor refactoring to the
Referee and Player to function in a way that allows for both remote and "AI" players to participate
in a Referee's game. There will be a new Player implementation called the TCP-Player that inherits
all methods required of the Player API. The TCP-Player is responsible for connecting with a single 
remote player, handling TCP requests and responses, and serializing and deserializing GameState data
to and from Json. The component in charge of collecting players for a Referee is called the 
GameOrganizer.

The GameOrganizer takes in an amount of players required to run a single game and then creates a 
Referee with a list of players that are ready to play the game. This component deals with the protocol
to gather players. As soon as the GameOrganizer is given its quota of players to collect, it will 
listen on a specified port for connect requests to play the game. A connect request is simply a Json string: `"connect"`.
For each connect request the GameOrganizer receives, it will instantiate a new TCP-Player that only
talks to the player that sent the connect request and also send a Json String `"accepted"` in response to tell the player they have been added to a game. 
The newly created TCP-Player is then added to a list of players that will join the game. Once the GameOrganizer has accumulated enough connected players, it will 
stop listening for additional connect requests and create the Referee with the list of Players. 
The Referee is then in charge of the game protocol and reporting the results of the game. 


The Json requests and responses used by the TCP-Player will all have the same format to keep parsing the data simple. Each 
request will be written in Json as an object that contains a String parameter with the name of the 
request, mapped to an array of the request's parameters serialized to their Json forms. The request
syntax is written below for each of the Player's methods:

```json
{"name": []}
{"proposeBoard0": [rows, columns] }
{"setup": [State, Coordinate] }
{"take-turn": [State] }
{"won": [Boolean]}
```

Note: The syntax of the parameters are taken from the definitions of the Json written in the 
Specification for Harness Tests.

The TCP-Player implementation will expect the player's Json responses to be written in a similar
format. Each response will be a single Json object with a single String parameter (with the name of
the request this response is replying to) mapped to the Json serialization of the expected return
type. The response syntax for each request is written below:

```json
{"name": String}
{"proposeBoard0": Board }
{"take-turn": Choice }
```

As the TCP-Player is responsible for deserializing a player's responses, it must be in charge of 
determining if each response is well-formed. If a response is not well-formed/the player did not 
send a response in time, the TCP-Player will return an empty optional. This will signify to the 
Referee that the player has made an invalid response, so the player must be kicked from the game.
