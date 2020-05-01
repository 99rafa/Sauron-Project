# Sauron project report

Distributed Systems 2019-2020, 2nd semester


## Authors

**Group A31**


 Number | Name              | User                                 | Email                                        |
| -------|-------------------|--------------------------------------| ---------------------------------------------|
| 89401  | Afonso Paredes    | <https://github.com/afonsoparedes>   | <mailto:afonsoparedes@tecnico.ulisboa.pt>    |
| 89535  | Pedro Nunes       | <https://github.com/pedronunes99>    | <mailto:pedro.m.s.nunes@tecnico.ulisboa.pt>  |
| 89538  | Rafael Alexandre  | <https://github.com/99rafa>          | <mailto:rafael.alexandre@tecnico.ulisboa.pt> |


Afonso Paredes             |  Pedro Nunes              |  Rafael Alexandre
:-------------------------:|:-------------------------:|:-------------------------:
<img src="https://cdn.discordapp.com/attachments/690606370101264539/705488145478713384/afonso.png" height="100" width="100" />  |  <img src="https://cdn.discordapp.com/attachments/690606370101264539/705492917573124127/unknown.png" height="100" width="100"/>  | <img src="https://cdn.discordapp.com/attachments/690606370101264539/705493092815601694/unknown.png" height="100" width="100"/>

## 1st part improvements

Improvements made to the 1st part of the project are as follows:

- [IT tests coverage improved for track, trackMatch and trace](https://github.com/tecnico-distsys/A31-Sauron/commit/a0d94a62e0efbb3681c137ab293f38f8d504f6a9)
- [Eye - sending a set of observations](https://github.com/tecnico-distsys/A31-Sauron/commit/df0bd9507912e2b90716a313ad171d8be5272081)
- [Spotter - sort spot* by id](https://github.com/tecnico-distsys/A31-Sauron/commit/b2e033ddcbc90f57ea342050fdb4c9ebea6497aa)
- Sauron - silo demo: Complete guide - [Improvement 1](https://github.com/tecnico-distsys/A31-Sauron/commit/0bd3581f06ccaf6ad1073689d3822a4ed6058065), [Improvement 2](https://github.com/tecnico-distsys/A31-Sauron/commit/fe70070fccca8e714606eb432fee6f332aafc85b)
- Handling exceptions - [Improvement 1](https://github.com/tecnico-distsys/A31-Sauron/commit/58108d99b8ce4b4b60b2e768665b196b270ba09a
), [Improvement 2](https://github.com/tecnico-distsys/A31-Sauron/commit/61d6be16fa70d4540ea636066d3c1a9918f357c0
)
- [Correct Synchronization of shared variables](https://github.com/tecnico-distsys/A31-Sauron/commit/a0d94a62e0efbb3681c137ab293f38f8d504f6a9)
 
## Fault model
_(que faltas são toleradas, que faltas não são toleradas)_

<img src="https://media.discordapp.net/attachments/690606370101264539/705557079535911013/Untitled_Diagram1.png" height="600" width="775"/>

First of all, we initiate the communications by starting 3 silo servers (silo 1, silo 2 and silo 3). 
Then, we run client eye 1. After that, silo 1 initiates gossip communication with every one of other silos and
silos 2 and 3 receive gossip messages with updates. At instant 3, silo 1 goes down
and becomes unavailable for communication. Spotter 1 tries to query silo 1 unsuccessfully and
looks for another one to communicate, finding silo 2 which it queries with the same request 
and instant 5. The response is updated so the result is presented to the client which saves it in
the cache. At instant 7, silo 2 sends gossips to the other silos being only successful in 
communicating with replica 3. After that, eye 2 sends an update to silo 3 and spotter 2 
queries silo 3 for that same update. The spotter once again presents the response and saves it in 
the cache. Then, it sends the same query but silo 3 has just become unavailable which makes the 
spotter 2 reconnect to silo 2 and re-send the query. As there were no gossips from silo 3 to silo 2 in 
bettwen [U,8] and [Q,13], the response [R,14] is outdated which makes spotter 2 go to the cache to retrieve the 
last stable response. 

## Solution

_(Solution diagram of fault tolerance)_


### Diagram 1: Communication between replicas
<img src="https://cdn.discordapp.com/attachments/690606370101264539/705798887255834624/unknown.png" height="600" width="800" />

### Diagram 2: Eye client - Silo Server Communication
<img src="https://cdn.discordapp.com/attachments/690606370101264539/705799065622937651/unknown.png" height="600" width="800" />

### Diagram 3: Spotter client - Silo Server Communication
<img src="https://cdn.discordapp.com/attachments/690606370101264539/705799401096085564/unknown.png" height="600" width="900" />


_(Breve explicação da solução, suportada pela figura anterior)_


## Replication protocol

_(Explicação do protocolo)_

_(descrição das trocas de mensagens)_


## Implementation options

_(Descrição de opções de implementação, incluindo otimizações e melhorias introduzidas)_

#### Consistency over efficiency 
We decided to implement a system where each server could send a gossip message to every other server and, even though it is less
efficient, it makes the system much more consistent.

#### A cache for every client
Every single client has its own cache, which enables that each outdated message that a client receives, will not be read. On the other hand, it will be shown the last updated message read by that same client, instead of showing up the message returned by the server.

#### "Wake-up gossip"
 As soon as a gossip message is sent by a server, that same server will cleanup the update log. However, the server produces a list of replica's ids which he couldn't send in the gossip message (due to being unnavailable, for instance) and saves it until it is possible to send all the gossip messages with success. This type of gossip could be called a "wake-up gossip", since its job is to re-send a gossip message to a server that was unnavailable in the gossip message sent before. This implementation allows that repeated information is not sent to the replicas that already received updates, sending only those updates to the replicas that did not receive this information.
 
#### Reconnecting
When clients make a request to an unavailable server, they connect to an available replica and make the request again.
 
#### No more repeated requests
We implemented an execution table that prevents the server to process repeated requests. There is a randomly generated unique ID for each request that distiguishes them.

**Minor details:** We compressed messages in proto because there was a lot of repeated messages. For instance: every update had an empty response; Track, Trail and TrackMessage had a similar requeste message; etc...

## Closing remarks

_(Algo mais a dizer?)_
