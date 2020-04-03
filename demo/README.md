# A31 - Sauron Application Demo Guide

## About

This is a demo file so the user can understand how the application works.

## Instructions for using Maven and running the application

First, open a terminal, go to the ```/A31-Sauron``` directory and run the command:
```bash
mvn install
``` 


Do the same thing on the ```/A31-Sauron/contract``` directory.


Then, on the same terminal, go to the ```/A31-Sauron/silo-server``` directory and run the command:
```bash
mvn install
``` 
and then,
```bash
./arget/appassembler/bin/silo-server <port>
``` 



That will start the server.

Then, open another terminal and to start the eye client, go to the ```/A31-Sauron/eye``` directory and run the command:
```bash
./target/appassembler/bin/eye <args>*
```

To start the spotter client is similar, but instead go to the ```/A31-Sauron/spotter``` directory and run the command:
```bash
./target/appassembler/bin/spotter <args>*
```

## Instructions to run the test files

First, open a terminal, go to the ```/A31-Sauron``` directory and run the command:
```bash
mvn install
``` 


Do the same thing on the ```/A31-Sauron/contract``` directory.


Then, on the same terminal, go to the ```/A31-Sauron/silo-server``` directory and run the command:
```bash
mvn install
``` 
and then,
```bash
./target/appassembler/bin/silo-server <port>
``` 

That will start the server.


Then, to run the first two tests (insert100cars.txt and insert100persons.txt), open a terminal in the ```/A31-Sauron/eye``` directory and run the commands:
```bash
mvn install
``` 
```bash
./target/appassembler/bin/eye [args]* < ../demo/<nameOfTest>
```


Example to run the test insert100cars.txt: 

```bash
./target/appassembler/bin/eye localhost 8080 camara 30 30 < ../demo/insert100cars.txt
```

To run the other tests (track.txt, trackMatch.txt and trail.txt), open a terminal in the ```/A31-Sauron/spotter``` directory and run the commands:
```bash
mvn install
``` 
```bash
./target/appassembler/bin/spotter [args]* < ../demo/<nameOfTest>
```

Example to run the test track.txt:
```bash
./target/appassembler/bin/spotter localhost 8080 < ../demo/track.txt
``` 

## Test files

**insert100cars.txt** 

  This file tests the cam_join and report functions in the eye client.
  
**insert100persons.txt**

  This file tests the cam_join and report functions in the eye client.
  
**track.txt**

  This file tests the track function in the spotter client.
  
**trackMatch.txt**

  This file tests the trackMatch function in the spotter client.
  
**trail.txt**

  This file tests the trace function in the spotter client.
  
**init.txt**

  This file tests the init function in the spotter client.
  
**clear.txt**

  This file tests the clear function in the spotter client.
  
**ping.txt**

  This file tests the ping function in the spotter client.
 
----

