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

Then, on the same terminal, go to the ```/A31-Sauron/silo-server``` directory and run the command:
```bash
$ mvn install
``` 
and then,
```bash
$ ./target/appassembler/bin/silo-server 8080
```


That will start the server, and *silo* will have the address *localhost* and the port as *8080*.


### 1.3. *Eye*


We will now regist 3 cameras and their observations. Each camera will have its own entry file with some observations already defined. To do that, go to the ```/A31-Sauron/eye``` directory and run the command:

```bash
$ ./target/appassembler/bin/eye localhost 8080 Tagus 38.737613 9.303164 < ../demo/e1.txt
$ ./target/appassembler/bin/eye localhost 8080 Alameda 30.303164 10.737613 < ../demo/e2.txt
$ ./target/appassembler/bin/eye localhost 8080 Lisboa 32.737613 15.303164 < ../demo/e3.txt

```


## 2. Operations tests


### 2.1. *cam_join*

This operation was already tested in section 1.3. but we still need to test the output of some restrictions.

2.1.1 - Testing duplicated names:
```bash
$ ./target/appassembler/bin/eye localhost 8080 Tagus 10.0 10.0
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

### 2.3 *report*

To test this command, open a client spotter in the ```/A31-Sauron/spotter``` directory, running this command:

```bash
$ ./target/appassembler/bin/spotter localhost 8080
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


### 2.4. *track*

This operation can be tested using the command *spot* with an id.

2.4.1. - Testing with one person (returns empty because this person does not exist):

```bash
> spot person 14388236

``` 
2.4.2. - Testing with one person:

```bash
> spot person 123456789
person,123456789,2020-04-21 12:15:26,Alameda,30.303164,10.737613
``` 

2.4.3. - Testing with one car:

```bash
> spot car 20SD21
car,19SD19,2020-04-21 12:15:32,Lisboa,32.737613,15.303164
``` 


### 2.5. *trackMatch*

This operation will be tested using the command *spot* with a fraction of the id.

2.5.1. - Test with on person (returns empty because this person does not exist):

```bash
> spot person 143882*

``` 

2.5.2. - Tests with one person:

```bash
> spot person 111*
person,111111000,2020-04-21 12:15:16,Tagus,38.737613,9.303164

> spot person *000
person,111111000,2020-04-21 12:15:16,Tagus,38.737613,9.303164
 
> spot person 111*000
person,111111000,2020-04-21 12:15:16,Tagus,38.737613,9.303164
``` 

2.5.3. - Tests with two or more people:

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

2.5.4. - Tests with one car:

```bash
> spot car 00A*
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164

> spot car *A00
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164

> spot car 00*00
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164
```

2.5.5. - Tests with two or more cars:

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

## 2.6. *trace*

This operation will be tested using the command *trail* with an id.

2.6.1. - Test with one person (returns empty because this person does not exist):

```bash
> trail person 14388236

``` 


2.6.2. - Test with one person:

```bash
> trail person 123456789
person,123456789,2020-04-21 12:42:51,Tagus,38.737613,9.303164
person,123456789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
person,123456789,2020-04-21 12:42:46,Alameda,30.303164,10.737613
```


2.6.3. - Test with one car (returns empty because this car does not exist):

```bash
> trail car 12XD34

```

2.6.4. - Test with one car:

```bash
> trail car 00AA00
car,00AA00,2020-04-21 12:42:56,Tagus,38.737613,9.303164
car,00AA00,2020-04-21 12:42:51,Tagus,38.737613,9.303164
```

----

