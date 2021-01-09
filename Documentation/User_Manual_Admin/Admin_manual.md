# Admin Manual

## Index
+ [Introduction](#introduction)
+ [IP chooser](#ip-chooser)
+ [Login](#login)
+ [Display Menu](#display-menu)	
	+ [Register patient](#register-patient)
	+ [Register doctor](#register-doctor)
	+ [Register Admin](#register-admin)
	+ [Shutdown server](#shutdown-server)



## Introduction 

This application has been programmed in the Java language. Therefore, in order to run it, you must make sure you have a Java compiler installed.

### IP Chooser
First of all the application will ask you about the direction IP of the server you want to connect.
The IP is either an IPv4 address or "localhost"

### Login 
Then the program will ask you about your identification. You must enter your ID (eight numbers and the letter which is case sensitive so you have to be careful and put it as registered) and password.

By default, if no database is created an admin with the following credentials will be added:
* DNI: **11012021A**
* Password: **Sangre**

The program will ask you for these 2 fields as long as the login is not correct

## Display Menu
For all functions that involve registration of a new user, the process can be stopped
by introducing an x. The administrator will be shown the main menu again.

### Register patient
This option will send a new patient to the server to register it in the server's database.

You will be asked for the following fields: 
* *Name*: no special requirements for the name field
* *DNI*: it has to be 8 digits and a letter
* *Password*: no special requirements for the password field
* *Sex*: only m or f are allowed as possible options for the sex field
* *Date of birth*: it has to be introduced as YYYY-MM-DD and no dates greater than the current date are allowed
* *Doctor id*: the administrator will be provided with all the doctors registered and their ids. Out of all of these doctors, 
one has to be assigned to the new patient.
* *Risk factors*: the administrator will be asked to introduce y (yes) or n (no)
for a list of risk factors that the patient might have

**Note**: it is important to know that if no doctors are registered in the server, no patient 
will be able to be registered. If this is the case, first [register a doctor](#register-doctor)

### Register doctor
This option will send a new doctor to the server to register it in the server's database

The administrator will be asked to provide the following fields: 
* *Name*: no special requirements for the name field
* *DNI*: it has to be 8 digits and a letter
* *Password*: no special requirements for the password field

### Register admin
This option will send a new administrator to the server to register it in the server's database

The administrator will be asked to provide the following fields: 
* *DNI*: it has to be 8 digits and a letter
* *Password*: no special requirements for the password field

### Shutdown server
This option allows the administrator to shutdown the server.

If other clients are connected to the server, then the administrator will be asked
to confirm the shutdown. It can then cancel or continue with the process. 
