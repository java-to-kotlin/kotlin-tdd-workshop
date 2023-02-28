## Bowling workshop

## How to simulate serial comms with the pinsetter hardware

1. Run `./gradlew` to compile the components and install the components and scripts in the `workshop/` directory.
2. Before the workshop, switch to the mobbing branch in which participants write code to implement the controller
3. Run `./workshop/run-components` to start the console, fake-pinsetter and multiplexer
4. The controller must read from the file `workshop/pipes/multiplexer-to-controller` and write to `workshop/pipes/controller-to-multiplexer`. 

After running `./workshop/run-components` you can run `./workshop/stdio-controller` in another terminal to manually demo the behaviour of the controller. Lines sent to the controller will be displayed on the terminal (in red), and you can enter commands to the pinsetter and console to drive the game logic.


## Talking points

### How do we slice the problem?  

1. Inside-out: work layer by layer -- turn taking, scoring, I/O, app state machine, main?
2. Outside-in: grow a continually running app -- order to gradually increase player fun: can roll balls at pins (e.g. start game and control pinsetter after each roll), manage progression of frames, take turns, simple scoring, score strikes and spares, end-game condition, final bonus balls, report winner(s)

### Type-driven development

* Types to define the operations you can perform on a value
  * E.g. PinCount is not an Int. It doesn't make sense to multiply a PinCount 
* Types to guarantee constraints  
  * E.g. PinCount is between 0 and 10
* Types to prevent invalid structures
  * E.g. cannot have a frame preceded by a partially complete frame


### Modelling in Kotlin

* sealed type hierarchies (algebraic types) vs interfaces and open classes (object-oriented polymorphism)
* methods vs extensions: what is the "platonic ideal" and what is application specific?
* information hiding vs extensibility


### You are meant to repeat the kata

In this workshop we will explore:

* the interplay between types and tests
* making invalid states unrepresentable
* using functional programming to manage states and I/O 
* the functional-core/imperative-shell architecture

We strongly encourage you to repeat kata in your own organisations to compare and contrast different approaches:

* outside-in vs inside-out
* mutable state vs immutable state
* minimal domain model vs type-level modelling
* object-oriented polymorphism vs algebraic data types and pattern matching
* driving with example-based tests vs property-based tests

The right approach _for you_ depends on the people working on the system, and _will_ change over time.
