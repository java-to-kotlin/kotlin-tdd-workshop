# Protocols

## Components

Deployment for testing the Controller locally

```plantuml
@startuml
component {
    component Console
    component Multiplexer
    component FakePinsetter
    
    Console - Multiplexer : pipe
    Multiplexer - FakePinsetter : pipe
}

component Controller
Controller -- Multiplexer : pipe
@enduml
```


Deployment in the field

```plantuml
@startuml

actor Player

node Pinsetter {
    component Firmware
}

node LaneUnit {
    component NetcatServer
    component Controller
    component Multiplexer
}

node ConsoleUnit { 
    component Console
}

Player -d- Console
Console -r- NetcatServer : "tcp/ip"
NetcatServer -r- Multiplexer : pipe
Controller -d- Multiplexer : pipe
Multiplexer -r- Firmware : serial

@enduml
```

## Sequence diagram of message flows

Messages between the Controller and the Multiplexer

```plantuml
@startuml
participant Multiplexer
participant Controller

Multiplexer -> Controller : START <player-count>

loop for each player
    Multiplexer <- Controller : PLAYER <score-line> <total>
end
Multiplexer <- Controller : NEXT 0

Controller -> Multiplexer : RESET
Controller <- Multiplexer : READY

loop until end of game
    Controller <- Multiplexer : PINFALL <pin-count>
    
    alt end of frame
        Controller -> Multiplexer : SET FULL
    else continue frame
        Controller -> Multiplexer : SET PARTIAL
    end
    
    loop for each player
        Multiplexer <- Controller : PLAYER <score-line> <total>
    end
    alt end of game
        Multiplexer <- Controller : WINNER <player-index>...
    else continue game
        Multiplexer <- Controller : NEXT <player-index>
    end
end
@enduml
```

Message flow between all components: 

```plantuml
@startuml
participant Console
participant Pinsetter
participant Multiplexer
participant Controller

Console -> Multiplexer : START <player-count>
Multiplexer -> Controller : START <player-count>

loop for each player
    Multiplexer <- Controller : PLAYER <score-line> <total>
    Console <- Multiplexer : PLAYER <score-line> <total>
end
Multiplexer <- Controller : NEXT 0
Console <- Multiplexer : NEXT 0

Controller -> Multiplexer : RESET
Multiplexer -> Pinsetter : RESET
Multiplexer <- Pinsetter : READY
Controller <- Multiplexer : READY

loop until end of game
    Multiplexer <- Pinsetter : PINFALL <pin-count>
    Controller <- Multiplexer : PINFALL <pin-count>
    
    alt end of frame
        Controller -> Multiplexer : SET FULL
        Multiplexer -> Pinsetter : SET FULL
    else continue frame
        Controller -> Multiplexer : SET PARTIAL
        Multiplexer -> Pinsetter : SET PARTIAL
    end
    
    loop for each player
        Multiplexer <- Controller : PLAYER <score-line> <total>
        Console <- Multiplexer : PLAYER <score-line> <total>
    end
    alt end of game
        Multiplexer <- Controller : WINNER <player-index>...
        Console <- Multiplexer : WINNER <player-index>...
    else continue game
        Multiplexer <- Controller : NEXT <player-index>
        Console <- Multiplexer : NEXT <player-index>
    end
end
@enduml
```



