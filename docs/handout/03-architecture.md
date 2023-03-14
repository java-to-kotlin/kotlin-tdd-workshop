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
* `stdio-controller` can be used to manually explore the protocol.  It connects your terminal to the named pipes, displays messages sent by the multiplexer and lets you enter messages in the terminal to the multiplexer.  It displays the inputs and outputs in different colours, so that you can more easily understand the message flow.


