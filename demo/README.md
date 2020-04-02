# A31 - Sauron Application Demo Guide

## About

This is a demo file so the user can understand how the application works.

## Instructions for using Maven and running the application

First, open a terminal, go to the ```/A31-Sauron/silo-server``` directory and run the command 
```bash
mvn compile:exec
``` 
That will start the server.
Then, open another terminal and to start the eye client, go to the ```/A31-Sauron/eye``` directory and run the command ```./target/appassembler/bin/eye [args]*```  .

## Instructions to run the test files

First, open a terminal, go to the ```/A31-Sauron/silo-server``` directory and run the command ```mvn compile:exec```. That will start the server.

Then, to run the first 2 tests (insert100cars.txt and insert100persons.txt), open a terminal in the ```/A31-Sauron/eye``` directory and run the command ```./target/appassembler/bin/eye [args]* < ../demo/[nameOfTest]``` .

Example to run the test insert100cars.txt -> ```./target/appassembler/bin/eye localhost 8080 camara 30 30 < ../demo/insert100cars.txt``` .

To run the other tests (track.txt, trackMatch.txt and trail.txt), open a terminal in the ```/A31-Sauron/spotter``` directory and run the command ```./target/appassembler/bin/spotter [args]* < ../demo/[nameOfTest]``` .

Example to run the test track.txt -> ```./target/appassembler/bin/eye localhost 8080 < ../demo/track.txt``` 

----

