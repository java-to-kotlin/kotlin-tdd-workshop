# Software components

The following software components run the games for a single bowling lane.

* The _Console_ is a graphical user interface with which the players' enter their names, start the game, and see the current scoreboard and next player to bowl. The console also communicates with a text-based protocol.
* The _Pinsetter_ firmware controls the lane's pinsetter machine, which automatically detects fallen pins, clears fallen pins from the pin deck, sets bowling pins back in their original positions, and returns bowling balls to the front of the alley.  The Pinsetter firmware communicates over serial cable with a text-based protocol.
* The _Controller_ runs the game logic.  It keeps track of the players' turns and their scores.  It controls the _Pinsetter_ and updates the _Console_ as the game progresses.  It has a single input and output stream, and relies on the _Multiplexer_ to route messages to/from the other components.
* The _Multiplexer_ routes messages between the _Controller_, _Console_ and _Pinsetter_.

```{.plantuml width=75% caption="Deployment in the field"}
node Pinsetter {
    component Firmware
}

node LaneUnit {
    component Controller
    component Multiplexer
    component Console
}

Console -r- Multiplexer : pipe
Controller -d- Multiplexer : pipe
Multiplexer -r- Firmware : serial

actor  Bowler
Bowler -u- Console
```

In this workshop we are going to write the _Controller_.

## Testing the Controller locally during development

We simulate the Pinsetter in software on the developer machine and communicate with the fake pinsetter over Unix named pipes.

```{.plantuml width=75% caption="Deployment for testing the Controller"}
node MacBook {
    component {
        component Console
        component Multiplexer
        component FakePinsetter
        
        Console - Multiplexer : pipe
        Multiplexer - FakePinsetter : pipe
    }
    
    component Controller
    Controller -- Multiplexer : pipe
}

actor You
You -u- Console
You -u- FakePinsetter
```

In the workshop/ directory there are two scripts for use when testing:

* `run-components` creates the named pipes and runs all the components except the Controller.  When the named pipes exist, run the Controller, reading its input from the named pipe `workshop/pipes/multiplexer-to-controller` and writing its output to the named pipe `workshop/pipes/controller-to-multiplexer`.  (To run the Controller from within the IDE, its main function will have to open the input and output named pipes for reading and writing respectively).   
* `stdio-controller` can be used to manually explore the protocol.  It connects your terminal to the named pipes, displays messages sent by the multiplexer and lets you enter messages in the terminal to the multiplexer.  It displays the inputs and outputs in different colours so you can more easily understand the message flow.


# Communication protocols

## Messages to/from the Console

```{.plantuml width=100%}
hide footbox
skinparam lifelineStrategy solid

participant Console
participant Peer

Console -> Peer : START <player-count>
note right : When the user has entered the player names\nand started a new game

loop until end of game
    |||
    note over Peer : Sends empty scores to acknowledge the START message\nand initialise the scoreboard display 
    
    loop for each player
        Peer -> Console : PLAYER <score-line>
        note right : Report per-frame scores and total score
    end
    |||
    alt game in progress
        Peer -> Console : NEXT <player-index>
        note right : Report the next player to bowl
    else end of game
        Peer -> Console : WINNER <player-index> ...
         note right : Report that the game is over and identify the winner(s)\nThere can be more than one in the case of a draw
    end
end
```

```{.plantuml width=65%}
@startebnf
Outputs = "START", space, player_count, endl;

player_count = ? [1-9][0-9]* ?;

Inputs = player_score_lines, next_action;

player_score_lines = player_score_line, {player_score_line};

player_score_line = "PLAYER", space, score_line, endl;

score_line = frame_scores, score;

frame_scores = [frame_score, {space, frame_score}];

frame_score = rolls, ",", [score];

rolls = numeric_rolls | symbolic_spare | symbolic_strike;

@endbnf
```

```{.plantuml width=65%}
@startebnf

numeric_rolls = [score], ",", [score];

symbolic_spare = score, ",", "/";

symbolic_strike = "X", "," | ",", "X";

next_action =
    "NEXT", space, player_index, endl
  | "WINNER", space, player_index_list, endl;

player_index_list = player_index, {space, player_index};

player_index = ? [0-9]+ ?;

score =  ? [0-9]+ ?;

space = " " (* space character *); 
endl = "\n" (* newline character *); 

@endebnf
```

## Messages to/from the Pinsetter

```{.plantuml width=100%}
hide footbox
skinparam lifelineStrategy solid

participant Peer
participant Pinsetter

loop for each game 
    Peer -> Pinsetter : RESET
    note right : Handshake to (re)sync state of peer & hardware
    note over Peer : Peer ignores all messages \nuntil it receives READY 
    Pinsetter -> Peer : READY
    note right : All pins standing, ready for first roll
    loop
        Pinsetter -> Peer : PINFALL <n>
        note right : Report pins knocked down by roll
        alt continue frame
            Pinsetter <- Peer : SET PARTIAL
            note right : Clear the lane of fallen pins\nSet pins that were standing for next roll
        else end of frame or bonus roll of 10 after final strike, continue game
            Pinsetter <- Peer : SET FULL 
            note right : Clear the lane of fallen pins\nSet all pins for next roll
        else end of game
            note over Peer : no message to Pinsetter
        end
    end
end
```

```{.plantuml width=65%}
@startebnf
Input = 
    "RESET", endl
  | "SET", space, "PARTIAL", endl
  | "SET", space, "FULL", endl;

Output = 
    "READY", endl
  | "PINFALL", space, pin_count, endl;

pin_count = ? [1-9][0-9]* ?;

space = " " (* space character *); 
endl = "\n" (* newline character *); 
@endebnf
```


## Messages to/from the Controller

The Controller performs the Peer side of both the Pinsetter and Console protocols and relies on the Multiplexer to route messages to/from the other components.

```{.plantuml width=100%}
@startuml
hide footbox
skinparam lifelineStrategy solid

participant Controller
participant Multiplexer

Multiplexer -> Controller : START <player-count>

|||

Controller -> Multiplexer : RESET
note right: handshake to (re)sync with Pinsetter
Controller <- Multiplexer : READY

|||

loop for each player
    Multiplexer <- Controller : PLAYER <score-line>
end
Multiplexer <- Controller : NEXT 0
note right: report first player to bowl

|||

loop until end of game
    Controller <- Multiplexer : PINFALL <pin-count>
    
    |||
    
    alt continue frame
        Controller -> Multiplexer : SET PARTIAL
    else end of frame or bonus roll of 10 after final strike, continue game
        Controller -> Multiplexer : SET FULL
    else end of game
        note over Controller : no message to Pinsetter via Multiplexer
    end
    
    |||
    
    loop for each player
        Multiplexer <- Controller : PLAYER <score-line>
    end
    alt continue game
        Multiplexer <- Controller : NEXT <player-index>
    else end of game
        Multiplexer <- Controller : WINNER <player-index>...
    end
end
@enduml
```

