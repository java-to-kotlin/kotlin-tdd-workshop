## Bowling workshop

## How to fake serial comms with fake hardware

1. Run `./gradlew` to compile the components and install the components and scripts in the `workshop/` directory.
2. Before the workshop, switch to the mobbing branch in which participants write code to implement the controller
3. Run `./workshop/run-components` to start the console, fake-pinsetter and multiplexer
4. The controller must read from the file `workshop/pipes/multiplexer-to-controller` and write to `workshop/pipes/controller-to-multiplexer`. 

After running `./workshop/run-components` you can run `./workshop/stdio-controller` in another terminal to manually demo the behaviour of the controller. Lines sent to the controller will be displayed on the terminal, and you can enter commands to the pinsetter and console to drive the game logic.
