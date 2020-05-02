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

In which concerns faults our solution is capable of dealing with, we must highlight the following ones:

**On the server side,**
* F1. Query information that has been inserted in a different replica (project topic 2.2).
    * i.e. *for example, an eye client `e1` sends an update to a replica `r1`. If a spotter client `s1` queries replica `r2` for the information updated in `r1`, `r2` returns the given update to `s1`.*

* F2. Recover from replicas unavailable for a period of time and making them catch the other ones, regarding the updates they currently have, given the fault was transient.
    * i.e. *for example, a replica `r1` becomes unavailable for some time and, then, when it is operational again, other replicas send `r1` the updates it has missed out.*

**On the client side,**
* F3. Connect to a different replica when the current one is down.
    * i.e. *for example, an eye client `e1` is connected to a replica `r1`. When, sending a request to the server, `e1` notices that the connection has died, the client tries to set up a new connection to another replica `r2`. When it does, `e1` re-sends the previous request.*

* F4. Outdated view when spotter client queries a different replica for the same information (project topic 3.3).
    * i.e. *for example, a spotter client `s1` is connected to an updated replica `r1`, querying it for the trace path `t1` of a person. Then, `r1` goes down, so the procedure is triggered and `s1` 
    reconnects to replica `r2` ( which is yet to receive the update for that person). Spotter `s1` makes the same request as before. However, this time it receives a trace path `t2`,previous to `t1`. So, `s1` presents `t1` in the terminal.*

On the other hand, there are several faults from which our solution does not recover, being the major ones:

* F5. Zookeeper server being unavailable
    * i.e. *the solution was built assuming the name server Zookeeper is always operational.*

* F6. Replicas disconnected to the server without sending their updates to the rest of the graph.
    * i.e. *a replica `r1` receives updates from an eye client and immediately disconnects not having spread the previous updates. In fact,  the information was only stored on `r1`, so the updates are lost.*

* F7. Replicas disconnected to the server and reconnected, having lost all previous acquired information.
    * i.e. *a replica `r1` receives updates from an eye client and, after a while, goes down. When it reconnects, all the information stored before was lost so, it will no longer be able to have a consistent state as their peer replicas.*


## Solution

The course of action of our solution and its most relevant use cases can be described through the following diagram,
<img src="https://media.discordapp.net/attachments/690606370101264539/705557079535911013/Untitled_Diagram1.png" height="600" width="775"/>

First of all, the communication is initiated by starting 3 silo servers (silo 1, silo 2 and silo 3). 
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


## Replication protocol

#### Overall
In this project, we implement a variation of the *gossip architecture* protocol. More specifically, the solution implemented is not concerned about much of the causal dependencies' problem of the original algorithm.\
In order to keep track of the updates made throughout the graph of replicas, there is a timestamp structure which saves the current state of the given replica. 

#### Interaction Client-Server
When a client sends a request to a replica, it checks if the replica is operational. If not, it then tries to connect to another replica and, when successful, re-sends the request.\
\
**Note:** If the client is started with a given replica number to contact to, it exits if that replica is currently unavailable.\
\
Regarding eye requests ( updates ), the server receives the request and assigns a unique timestamp to identify the update, including it in the update log ( used for *gossipping* ) . It then applies the update, merging the update timestamp with replica's own timestamp.
Afterwards, it sends a response back to the client which merges the response timestamp with its timestamp, so that the client knows its current state.
\
Regarding spotter requests ( queries ), the server receives the request and immediately gathers the information queried, sending it to the client.
The frontend intercepts the information to check if the response timestamp is updated ( which means `query TS > client TS` ).
* If the condition is met, the response is stored in the `responses' cache` to later inconsistent queries. And the response is presented to the client.
* If the condition is not met, frontend reaches the ``responses' cache`` to get the last stable response, which is returned to the client.


#### Interaction Server-Server ( *Gossip* )
In our implementation, each replica sends a gossip to all available replicas. Each gossip contains all the updates in the log records. The log records list is cleared after each gossip, stopping 2 problems: Sending too much repeated information; Overcharging each replica with way too much memory for the log records. But, because of clearing the log records after each gossip, the problem of replicas that may be down during a certain gossip losing information arises. We solved this problem by keeping a list of unavailable replica Id's in each replica. This list is updated after each gossip. And also, each replica keeps a special gossip message ("wakeup gossip"), containing every update since the last succesful gossip (no replicas were down on the gossip). This will allow to send that "wakeup gossip", that contains all the information that is known to not have been sent, to replicas that were down on the last gossip, and send the updates on the log records (which are much less, due to them being regularly cleared) to all the replicas that were previously available and are available at the momment. In short, this makes it so that the server only sends large amounts of information in gossips to replicas that it knows dont have certain updates, instead of sending everything to all the replicas.


## Implementation options

#### Consistency over efficiency 
We decided to implement a system where each server could send a gossip message to every other server and, even though it is less
efficient, it makes the system much more consistent.

#### A cache for every client
Every single client has its own cache, which enables that each outdated message that a client receives, will not be read. On the other hand, it will be shown the last updated message read by that same client, instead of showing up the message returned by the server.

#### "Wake-up gossip"
 As soon as a gossip message is sent by a server, that same server will cleanup the update log. However, the server produces a list of replica's ids which it couldn't send in the gossip message (due to being unnavailable, for instance) and saves it until it is possible to send all the gossip messages with success. This type of gossip could be called a "wake-up gossip", since its job is to re-send a gossip message to a server that was unnavailable in the gossip message sent before. This implementation allows that repeated information is not sent to the replicas that already received updates, sending only those updates to the replicas that did not receive this information.
 
#### Reconnecting
When clients make a request to an unavailable server, they connect to an available replica and make the request again.
 
#### No more repeated requests
We implemented an execution table that prevents the server to process repeated requests. There is a randomly generated unique ID for each request that distiguishes them.

**Minor details:** We compressed messages in proto because there was a lot of repeated messages. For instance: every update had an empty response; Track, Trail and TrackMessage had a similar requeste message; etc...

## Closing remarks

As a final statement, it is important to underline that, by performing minor modifications to the code, it would be possible to convert our solution into a more robust one in which concerns the probable causal dependencies between updates of a real application.
