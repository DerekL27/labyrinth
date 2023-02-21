The Maze
---

## Purpose

This contains all the working code to run a game of Labyrinth. All code in folders outside the src folder are symbolically linked 
to their equivalent inside the src folder.

## Contents

[Common](Common)

[Planning](Planning)

[Players](Players)

[Referee](Referee)

[src](src)

### Component Diagrams

#### Game components

![Static components](https://scontent-bos5-1.xx.fbcdn.net/v/t1.15752-9/318600839_820940919117088_2004847757434679291_n.jpg?stp=dst-jpg_p1080x2048&_nc_cat=105&ccb=1-7&_nc_sid=ae9488&_nc_ohc=isDgRWZAjzIAX-4z2pJ&tn=tRHoFE3LwlUiSoZj&_nc_ht=scontent-bos5-1.xx&oh=03_AdSJ3-QYPFe92Deiu3eZPX-47e2BBlM30NgRl9fHDUZv9w&oe=63B8E55C "Static components")

#### Remote components

![Remote components](https://scontent-bos5-1.xx.fbcdn.net/v/t1.15752-9/318876693_2160791144112957_444829049707499684_n.jpg?_nc_cat=107&ccb=1-7&_nc_sid=ae9488&_nc_ohc=lAXSt53pnzUAX-Civ6r&_nc_ht=scontent-bos5-1.xx&oh=03_AdTW983rrg5HZUj1MIsBTwT-kS0ytznS1I-1JXhuOY7lZA&oe=63B8E74C)



### File/Directory Descriptions

**Common:** The code that runs the model for the game Labyrinth is stored in this folder.

**Planning:** This folder contains all the documents involved in the planning task of all the milestones.

**Players:** This folder contains all the code that pertains to strategies and players, including AI players that run predefined strategies.

**Referee:** This folder contains all the code that pertains to the running and observing a game of Labyrinth.

**src:** The entire code structure of the project.
 - **src/main:** Contains all application files.
 - **src/test:** Contains all unit tests.

**pom.xml:** The Maven pom file used to organize dependencies and build plugins.

**xtests:** This file is the testing executable.

## Run Unit Tests

To run all project unit tests, simply run the executable `xtests`.
