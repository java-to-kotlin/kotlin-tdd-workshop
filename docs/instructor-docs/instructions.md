# Running the workshop

## Introduction

Who are we, and who are you?

Gauge level of experience

## Defining TDD

* Development? Design?
* Write a test, watch it fail, make it pass, refactor, repeat
* Early History of TDD
  * Smalltalk
  * Dynamic types - tests in places of types
  * Edit and continue
* Then Java
  * JUnit
  * Static types
  * IDEs with intentions to create types, methods etc on demand

* Then other Languages

But mostly, it must be said OO. TDD and OO (and patterns, and agile and..) grew up together.

And OO as in: using Objects and encapsulation to solve the problem of state.

The Goal of TDD: 

* Design
* Documentation
* Regression

How much Driven?

We don’t test everything, at least not individually

The role of design, and thinking, and feedback

TDD As If You Really Mean It

How much Test?

TDD improves design and specification quality, but different people have different focus

* Test-last prioritises documentation and regression
* Test-first tends to prioritise design and documentation

"Could I accidentally break this?" vs "Could I deliberately break this?"

Ultimately, TDD practitioners do use types to cut down on the amount of testing that they need to do, and to prevent regressions.

Tests are waste! They may be more efficient than an alternative, but they are not production code.

## Types

But there is also TDD - _Type_ Driven Design.

Communicating through types, type parameters and function signatures:

`fun <T> f(ts: List<T>): Int`

vs

`fun g(ts : List<String>): Int`

The function `f` depends on the shape of the list while function `g` depends on the elements of the list.  Type parameters _reduce_ the possible behaviours of a function.

Tends to be more of a thing in FP

“If you can get it to compile, it will work”

Types apply to syntax – static analysis. The most widely used formal method

Types specify the operations that can be applied to values in the program

Making illegal states unrepresentable

The type checker proves properties of our software

Start from designing the types from first principles, rather than driven by tests

What are the platonic truths about our system?

Traditionally (XP) had quite a bit of type design - it’s what CRC cards were about.

This workshop inspired by Scott Wlashin’s book Domain Modelling Made Functional (also https://www.youtube.com/watch?v=Up7LcbGZFuo).

Write the types, and the relationships between types, and the operations on types.

How can we augment / reconcile our TDD skills with types?

## Do We All Understand 10 Pin Bowling?



## Process

Don’t start with tests 😮

How far can we go with defining types and type relationships?

Write tests when:

* we need to implement an operation, and
* we cannot use types to prove the property we care about
* Focus on only writing tests to solve problems that we can’t solve with the typesystem.
* Write the minimum implementation to pass the tests
* Write another test to add functionality to existing operations
* Don’t be afraid of subtypes
* but limit implementation inheritance
* Fake it until you make it
* Don’t be afraid to revisit, refactor, reverse

## Lessons We Have Learned

* Frame is a very different state machine than Line
* Assertions on strings can be very useful
* … but stringly typed is hard to refactor
* Break the problem down into transformations between typed data structures
* Introduce new types to simplify the process via intermediate steps
  * E.g. Game state vs scores
* Making illegal states unrepresentable works well when our code is creating the states, but when interfacing with the external world, the external world can break our idealised laws.  Types help us cope with that boundary.

## Katas rely on repetition

Run the exercise again, trying different approaches, and compare the results:
* Object-Oriented: using mutable state and polymorphism
* Outside-in TDD: start by driving the application with the communication protocol
* TDD As If You Meant It: only create definitions when required to make a test pass
* Property-based tests
...

## Finally

@NatPryce
@DuncanMcG

Java to Kotlin - A Refactoring Guidebook
https://java-to-kotlin.dev


Refactoring to Kotlin on YouTube
https://youtube.com/@RefactoringDuncan

