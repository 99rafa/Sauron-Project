# A31 - Sauron Application Demo Guide

## About

This is a demo file so the user can understand how the application works.

## 1. Instructions for using Maven and running the application


### 1.1. Compiling the project

First, open a terminal, go to the ```/A31-Sauron``` directory and run the command:
```bash
$ mvn clean install -DskipTests
``` 


Now, on the ```/A31-Sauron/contract``` directory, run this:
```bash
$ mvn install
``` 


### 1.2. *Silo*

The command definition to run a silo-server is this:

```bash
$ ./target/appassembler/bin/silo-server <zkhost> <zkport> <i> <host> <port> <timeBetweenGossips>*
```
**zkhost** = zooKeeper host\
**zkport** = zookeeper port\
**i** = number of the server's replica. For example, if *i* = 1, the server port that will be used is 8081. If *i* = 2, the server port that will be used is 8082, and so on...\
**host** = server host\ 
**port** = server port\
**timeBetweenGossips** = period between messagens sent from one replica to the others (in milliseconds)

On the same terminal, go to the ```/A31-Sauron/silo-server``` directory and run the command:
```bash
<<<<<<< HEAD
./target/appassembler/bin/silo-server <port>
=======
$ mvn install
>>>>>>> develop
``` 
and then, for example:
```bash
$ ./target/appassembler/bin/silo-server localhost 2181 1 localhost 8081
```

That will start the server on the address *localhost* with the port *8081*, with the zkhost as *localhost* with the zkport  *2181* and becomes the replica number *1*.

To close the server, just hit the **enter** button.


### 1.3. *Eye*

Command definition:
 
 ```bash
 $ ./target/appassembler/bin/eye <zkhost> <zkport> <cameraName> <latitude> <longitude> <i>*
 ``` 
 
**zkhost** = zooKeeper host\
**zkport** = zookeeper port\
**cameraName** = name of the camera which will be registered in the server\
**latitude** = latitude of the camera\
**longitude** = longitude of the camera\
**i** = number of the replica the client will try to connect to
 
 To use the eye client, go to the ```/A31-Sauron/eye``` directory and run, for example:
```bash
$ ./target/appassembler/bin/eye localhost 2181 camName 12.123456 12.123456 1
```

which will open a eye client connected to zkhost localhost and zkport 2181. Aditionally, it will try to connect to replica number 1, failing if it connection does not go through. In which case, the client returns an exception and closes:

```bash
> Spotter client started
Server could not be found or no servers available at the moment
> Closing client
``` 
If no replica number is provided, the client will try to connect to any server available, return an execption if not possible.

We will now regist 3 cameras and their observations. Each camera will have its own entry file with some observations already defined. To do that, go to the ```/A31-Sauron/eye``` directory and run the command:

```bash
$ ./target/appassembler/bin/eye localhost 2181 Tagus 38.737613 9.303164 < ../demo/e1.txt
$ ./target/appassembler/bin/eye localhost 2181 Alameda 30.303164 10.737613 < ../demo/e2.txt
$ ./target/appassembler/bin/eye localhost 2181 Lisboa 32.737613 15.303164 < ../demo/e3.txt

```

To close the server, just hit **CTRL+C** .

### 1.4. *Spotter*

Command definition:

```bash
$ ./target/appassembler/bin/spotter <zkhost> <zport> <i>*
``` 

**zkhost** = zooKeeper host\
**zkport** = zookeeper port\
**i** = number of the replica the client will try to connect to
 

To use the spotter client, go to the ```/A31-Sauron/spotter``` directory and run, for example:
```bash
$ ./target/appassembler/bin/spotter localhost 2181 1
``` 
which will open a spotter client connected to zkhost localhost and zkport 2181. Aditionally, it will try to connect to replica number 1, failing if it connection does not go through. In which case, the client returns an exception and closes:

```bash
> Spotter client started
Server could not be found or no servers available at the moment
> Closing client
``` 
If no replica number is provided, the client will try to connect to any server available, return an execption if not possible.


## 2. Operations tests


### 2.1. *cam_join*

This operation was already tested in section 1.3. but we still need to test the output of some restrictions.

2.1.1 - Testing duplicated names:
```bash
$ ./target/appassembler/bin/eye localhost 2181 Tagus 10.0 10.0
``` 

Returns an exception. It happens because we previously added a camera named Tagus in the section 1.3.
```bash
Caught exception with description: The camera name must be unique
``` 

2.1.2 - Testing the size of the name (must be between 3 and 15):
```bash
$ ./target/appassembler/bin/eye localhost 8080 ab 10.0 10.0
$ ./target/appassembler/bin/eye localhost 8080 abcdefghijklmnop 10.0 10.0
```

Returns an exception as well.
```bash
Caught exception with description: The camera name must be between 3 and 15 characters
``` 

### 2.2 *report*

This operation was already tested in section 1.3. but we still need to test the output of some restrictions.

2.2.1 - Testing invalid type of object :
```bash
> unknown,1
``` 

Returns an exception. It happens because type of the object is neither car nor person.
```bash
Caught exception with description: The camera name must be unique
``` 

2.2.2 - Testing invalid id for person:
```bash
> person,1A
```

Returns an exception as well. The person id must a number
```bash
The id is invalid for the given type PERSON
``` 

2.2.3 - Testing invalid id for car:
```bash
> car,11AAAAA
```

Returns an exception as well. The car id must a pair of letters and 2 pairs of numbers
```bash
The id is invalid for the given type CAR
``` 



### 2.3. *track*

This operation can be tested using the command *spot* with an id.

2.3.1. - Testing with one person (returns empty because this person does not exist):

```bash
> spot person 14388236

``` 
2.3.2. - Testing with one person:

```bash
> spot person 123456789
person,123456789,2020-04-21 12:15:26,Alameda,30.303164,10.737613
``` 

2.3.3. - Testing with one car:

```bash
> spot car 20SD21
car,19SD19,2020-04-21 12:15:32,Lisboa,32.737613,15.303164
``` 


### 2.4. *trackMatch*

This operation will be tested using the command *spot* with a fraction of the id.

2.4.1. - Test with on person (returns empty because this person does not exist):

```bash
> spot person 143882*
The object with id 143882* does not exist

``` 

2.4.2. - Tests with one person:

```bash
> spot person 111*
person,111111000,2020-04-21 12:15:16,Tagus,38.737613,9.303164

> spot person *000
person,111111000,2020-04-21 12:15:16,Tagus,38.737613,9.303164
 
> spot person 111*000
person,111111000,2020-04-21 12:15:16,Tagus,38.737613,9.303164
``` 

2.4.3. - Tests with two or more people:

```bash
> spot person 123*
person,123111789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
person,123222789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
person,123456789,2020-04-21 12:42:51,Tagus,38.737613,9.303164

> spot person *789
person,123111789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
person,123222789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
person,123456789,2020-04-21 12:42:51,Tagus,38.737613,9.303164

> spot person 123*789
person,123111789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
person,123222789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
person,123456789,2020-04-21 12:42:51,Tagus,38.737613,9.303164
``` 

2.4.4. - Tests with one car:

```bash
> spot car 00A*
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164

> spot car *A00
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164

> spot car 00*00
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164
```

2.4.5. - Tests with two or more cars:

```bash
> spot car 20SD*
car,20SD20,2020-04-21 12:42:46,Alameda,30.303164,10.737613
car,20SD21,2020-04-21 12:42:46,Alameda,30.303164,10.737613
car,20SD22,2020-04-21 12:42:46,Alameda,30.303164,10.737613

> spot car *XY20
car,66XY20,2020-04-21 12:42:33,Lisboa,32.737613,15.303164
car,67XY20,2020-04-21 12:42:46,Alameda,30.303164,10.737613
car,68XY20,2020-04-21 12:42:56,Tagus,38.737613,9.303164

> spot car 19SD*9
car,19SD19,2020-04-21 12:42:33,Lisboa,32.737613,15.303164
car,19SD29,2020-04-21 12:42:33,Lisboa,32.737613,15.303164
car,19SD39,2020-04-21 12:42:33,Lisboa,32.737613,15.303164
car,19SD49,2020-04-21 12:42:33,Lisboa,32.737613,15.303164
car,19SD59,2020-04-21 12:42:33,Lisboa,32.737613,15.303164
car,19SD69,2020-04-21 12:42:33,Lisboa,32.737613,15.303164
car,19SD79,2020-04-21 12:42:33,Lisboa,32.737613,15.303164
car,19SD89,2020-04-21 12:42:33,Lisboa,32.737613,15.303164
car,19SD99,2020-04-21 12:42:33,Lisboa,32.737613,15.303164
```

### 2.5. *trace*

This operation will be tested using the command *trail* with an id.

2.5.1. - Test with one person (returns empty because this person does not exist):

```bash
> trail person 14388236
The object with id 14388236 does not exist
``` 


2.5.2. - Test with one person:

```bash
> trail person 123456789
person,123456789,2020-04-21 12:42:51,Tagus,38.737613,9.303164
person,123456789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
person,123456789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
```


2.5.3. - Test with one car (returns empty because this car does not exist):

```bash
> trail car 12XD34
The object with id 12XD24 does not exist
```

2.5.4. - Test with one car:

```bash
> trail car 00AA00
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164
car,00AA00,2020-04-21 12:42:51,Tagus,38.737613,9.303164
```

### 2.6. *ping*

To run ping command, just type

```bash
> ping
```

which will return the following:

```bash
Hello!
The server is running!
```

### 2.7. *clear*

To run ping command, just type

```bash
> clear
```
which will return the following:

```bash
System is now empty!
```

### 2.8. *init*

To run ping command, just type

```bash
> init
```

which will return the following:

```bash
Nothing to be configured!
```

### 2.9. *help*

To run ping command, just type

```bash
> help
```

which will return the following:

```bash
-----------------------------
Spotter commands:
spot -> spot <type> <id> 
trail -> trail <type> <id> 
ping -> ping <name>
clear -> clear
init -> init
-----------------------------
```

### 2.10. *help*

To exit spotter client, just type

```bash
> exit
```

## 3. Replication and fault tolerance

### 3.1 *Gossip messages*

For this demonstration, go to the /A31-Sauron/silo-server directory and start 2 servers like this. (For more info see section 1.2)

```bash
$ ./target/appassembler/bin/silo-server localhost 2181 1 localhost 8081
$ ./target/appassembler/bin/silo-server localhost 2181 2 localhost 8082
```
This will start 2 servers with a default 30 seconds of time between gossips. (to set a different time insert another argument in the executable command in miliseconds)

now go to the /A31-Sauron/silo-server directory and start the "eye" client. (For more info see section 1.3)

```bash
$ ./target/appassembler/bin/eye localhost 2181 Lisboa 12.1 12.1
```

This will connect to a random replica from the 2 started above, and will display the following message.

```bash
Connected to replica 1 at localhost:8081
```
or

```bash
Connected to replica 2 at localhost:8082
```
depending on which it connected to.

Next, insert some observations,

```bash
person,1
car,11AA11
person,2
```

Now, after waiting for the replica to send a gossip, it will appear on the sending replica terminal,

```bash
Replica 1 initiating gossip…
Contacting replica at localhost:8082 sending updates...
Contact with replica at localhost:8082 successful
```

Then, the updates will be made on the other replicas,

It will appear the following message on the  replica 2,

```bash
Gossip message Received
Added observation for object id:1 and Type:PERSON on <timestamp> in camera Lisboa
Added observation for object id:11AA11 and Type:CAR on <timestamp> in camera Lisboa
Added observation for object id:2 and Type:PERSON on <timestamp> in camera Lisboa

```

### 3.2 *Replica unavailability*

3.2.1 - Client sends request to an unavailable replica

For this demo, stop the server (*ctrl-c*) which the client is connected to, and try to make update in the "eye" client, for example.

```bash
person,5
car,AO1212
person,6
```

The client will then see that the current replica is down and connect to another replica, since there is only on left it will connect to replica 2. After it connects, the client will run the update it was supposed to make on the unavailable server.

It will appear the following messages reconnecting and saving the reports,

```bash
Sending observation for id 5 of type PERSON... 
Sending observation for id AO1212 of type CAR... 
Sending observation for id 6 of type PERSON... 
Replica 1 at localhost:8081 is down
Trying to reconnect to another replica
Reconnected to replica 2 at localhost:8082
Observations successfully saved!
```

3.2.2 - Server sending gossip to an unavailable replica

Now, start another replica as follows
```bash
$ ./target/appassembler/bin/silo-server localhost 2181 3 localhost 8083
```

After waiting for client 2 to send a gossip, it will appear a message confirming that it skipped the unavailble replica (nº1) like so

```bash
Replica 2 initiating gossip…
Contacting replica at localhost:8081 sending updates...
Caught exception while contacting replica at localhost:8081.Skipping...
```

### 3.3 *Client Cache*

For this demo, go to the /A31-Sauron/spotter directory and start a spotter as follows

```bash
$ ./target/appassembler/bin/eye localhost 2181
```

This will connect a spotter client to replica 1

Next, go to the eye client and add yet another observation.

```bash
person,10
```

Next, go to the spotter client and query for that observation as follows.

```bash
spot person 10
```

This will save the request in cache and display the response as follows.

```bash
person,10,<timestamp>,Lisboa,12.1,12.1
```


Now, before the replica 2 sends a gossip, stop the replica 2 (*ctrl-c*)

Finally, go to the spotter and query the server as follows

```bash
spot person 10
```

This will make the client migrate to replica 3 and make the same query, but because that replica is outdated, the client will instead resort to its cache and display the previous response to that request instead of the response the server gave. Like so


Spotter migrating to replica 3

```bash
Replica 2 at localhost:8082 is down
Trying to reconnect to another replica
Reconnected to replica 3 at localhost:8083
```

Spotter displaying updated info from cache
```bash
Response outdated.
Retrieving last stable entry from cache...
person,10,2020-05-02 03:14:04,Lisboa,12.1,12.1
```


----
## 4. Closing Remarks
 For more information, contact one of the team members.
