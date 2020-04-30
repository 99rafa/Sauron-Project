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

The argument *i* represents the number of the server's replica. For example, if *i* = 1, the server port that will be used is 8081. If *i* = 2, the server port that will be used is 8082, and so on...

On the same terminal, go to the ```/A31-Sauron/silo-server``` directory and run the command:
```bash
$ mvn install
``` 
and then, for example:
```bash
$ ./target/appassembler/bin/silo-server localhost 2181 1 localhost 8081
```

That will start the server, and *silo* will have the address *localhost* and the port as *8081*.


### 1.3. *Eye*

Command definition:
 
 ```bash
 $ ./target/appassembler/bin/eye <zkhost> <zkport> <cameraName> <latitude> <longitude>
 ``` 

We will now regist 3 cameras and their observations. Each camera will have its own entry file with some observations already defined. To do that, go to the ```/A31-Sauron/eye``` directory and run the command:

```bash
$ ./target/appassembler/bin/eye localhost 2181 Tagus 38.737613 9.303164 < ../demo/e1.txt
$ ./target/appassembler/bin/eye localhost 2181 Alameda 30.303164 10.737613 < ../demo/e2.txt
$ ./target/appassembler/bin/eye localhost 2181 Lisboa 32.737613 15.303164 < ../demo/e3.txt

```

### 1.4. *Spotter*

Command definition:

```bash
$ ./target/appassembler/bin/spotter <zkhost> <zport>
``` 


## 2. Commands

### 2.1 *spot*

```bash
> spot <type> <id>
``` 

### 2.2 *trail*
 
```bash
> trail <type> <id>
```

###  2.3 *clear*

```bash
> clear
```

###  2.4 *ping*

```bash
> ping <message>
```

###  2.5 *init*

```bash
> init
```

## 3. Operations tests


### 3.1. *cam_join*

This operation was already tested in section 1.3. but we still need to test the output of some restrictions.

3.1.1 - Testing duplicated names:
```bash
$ ./target/appassembler/bin/eye localhost 2181 Tagus 10.0 10.0
``` 

Returns an exception. It happens because we previously added a camera named Tagus in the section 1.3.
```bash
Caught exception with description: The camera name must be unique
``` 

3.1.2 - Testing the size of the name (must be between 3 and 15):
```bash
$ ./target/appassembler/bin/eye localhost 8080 ab 10.0 10.0
$ ./target/appassembler/bin/eye localhost 8080 abcdefghijklmnop 10.0 10.0
```

### 3.2 *report*

To test this command, open a client spotter in the ```/A31-Sauron/spotter``` directory, running this command:

```bash
$ ./target/appassembler/bin/spotter localhost 2181
``` 

To test the operation *report*, run this:

```bash
> trail car 00AA00
``` 

It will return 2 observations of the camera named Tagus:

```bash
> car,00AA00,2020-04-21 11:55:12,Tagus,38.737613,9.303164
> car,00AA00,2020-04-21 11:55:07,Tagus,38.737613,9.303164
``` 


### 3.3. *track*

This operation can be tested using the command *spot* with an id.

3.3.1. - Testing with one person (returns empty because this person does not exist):

```bash
> spot person 14388236

``` 
3.3.2. - Testing with one person:

```bash
> spot person 123456789
person,123456789,2020-04-21 12:15:26,Alameda,30.303164,10.737613
``` 

3.3.3. - Testing with one car:

```bash
> spot car 20SD21
car,19SD19,2020-04-21 12:15:32,Lisboa,32.737613,15.303164
``` 


### 3.4. *trackMatch*

This operation will be tested using the command *spot* with a fraction of the id.

3.4.1. - Test with on person (returns empty because this person does not exist):

```bash
> spot person 143882*

``` 

3.4.2. - Tests with one person:

```bash
> spot person 111*
person,111111000,2020-04-21 12:15:16,Tagus,38.737613,9.303164

> spot person *000
person,111111000,2020-04-21 12:15:16,Tagus,38.737613,9.303164
 
> spot person 111*000
person,111111000,2020-04-21 12:15:16,Tagus,38.737613,9.303164
``` 

3.4.3. - Tests with two or more people:

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

3.4.4. - Tests with one car:

```bash
> spot car 00A*
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164

> spot car *A00
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164

> spot car 00*00
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164
```

3.4.5. - Tests with two or more cars:

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

### 3.5. *trace*

This operation will be tested using the command *trail* with an id.

3.5.1. - Test with one person (returns empty because this person does not exist):

```bash
> trail person 14388236

``` 


3.5.2. - Test with one person:

```bash
> trail person 123456789
person,123456789,2020-04-21 12:42:51,Tagus,38.737613,9.303164
person,123456789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
person,123456789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
```


3.5.3. - Test with one car (returns empty because this car does not exist):

```bash
> trail car 12XD34

```

3.5.4. - Test with one car:

```bash
> trail car 00AA00
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164
car,00AA00,2020-04-21 12:42:51,Tagus,38.737613,9.303164
```

----

