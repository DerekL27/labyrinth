JSON
---

## Purpose

This directory contains the main logic to serialize and deserialize JSON objects specified by the spec.

## Contents

- [ActionJson](ActionJson.java)
- [BoardJson](BoardJson.java)
- [JsonEquality](JsonEquality.java)
- [JsonUtils](JsonUtils.java)
- [PlayerAPIJson](PlayerAPIJson.java)
- [PlayerJson](PlayerJson.java)
- [StateJson](StateJson.java)
- [StrategyJson](StrategyJson.java)
- [TileJson](TileJson.java)

### File Descriptions
**ActionJson:** A class that serializes an Action into a [*Choice*](https://course.ccs.neu.edu/cs4500f22/5.html#%28tech._choice%29)

**BoardJson:** A class that serializes and deserializes a [*Board*](https://course.ccs.neu.edu/cs4500f22/3.html#%28tech._board%29)

**JsonEquality** A convenience class that can bulk run tests given a test harness and directory that contains tests

**JsonUtils** A class that constructs helper objects from Jackson to help serialize/deserialize

**PlayerAPIJson** A class that handles deserialization of [*PS*](https://course.ccs.neu.edu/cs4500f22/6.html#%28tech._p%29) and [*BadPS*](https://course.ccs.neu.edu/cs4500f22/7.html#%28tech._badp%29)

**PlayerJson** A class that handles serialization and deserialization of [*Player*](https://course.ccs.neu.edu/cs4500f22/4.html#%28tech._player%29). The class also deserializes [*RefereePlayer*](https://course.ccs.neu.edu/cs4500f22/6.html#%28tech._refereeplayer%29)

**StateJson** A class that handles serialization and deserialization of [*State*](https://course.ccs.neu.edu/cs4500f22/4.html#%28tech._state%29). The class also deserializes [*RefereeState*](https://course.ccs.neu.edu/cs4500f22/6.html#%28tech._refereestate%29)

**StrategyJson:** A class that handles deserialization of [*Strategy*](https://course.ccs.neu.edu/cs4500f22/6.html#%28tech._strategy%29)

**TileJson** A class that handles serialization and deserialization of [*Tile*](https://course.ccs.neu.edu/cs4500f22/4.html#%28tech._tile%29)
