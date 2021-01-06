# Network Protocol

## Network data
* Server Address: **TO BE DETERMINED**
* Server Port: **22333**

## Network Messages
The server listens to objects of the type **NetworkMessage**. These messages have
a mandatory **Protocol** attribute which will determine what the message purpose is.
The type "Protocol" is an enumeration of the different protocols the server recognizes.
Depending on the protocol attribute, the **NetworkMessage** object will have to contain the
other attributes. 

### NetworkMessages Protocol Enumeration 
Here we will see what each protocol implies and what should be expected in the **NetworkMessage**
object with it.

###### From Patient to Server
* Protocol: _**PATIENT_LOGIN**_

    * **Usage**: After a successful TCP connection, this should be the first message sent to the
    server by any patient client. Its intention is to check if the user is registered in the
    database as a patient and with the right password.
    * **Requirements**: Requires a **Patient** object with at least DNI and password.
    * **Possible answers**: 
        * _**LOGIN_ACCEPT**_
        * _**LOGIN_DENY**_
        * _**ERROR**_

* Protocol: _**PUSH_MEASUREMENT**_

    * **Usage**: Once the client has logged in with a patient DNI and password, they can 
    upload as many measurements as they want. It can't be done before login in with _PATIENT_LOGIN_
    * **Requirements**: Requires a **ArrayList< Measurement >** of at least 1 measurement.
    * **Possible answers**:
        * _**ACK**_ 
        * _**ERROR**_
        
###### From Doctor to Server
* Protocol: _**DOCTOR_LOGIN**_

    * **Usage**: After a successful TCP connection, this should be the first message sent to the
    server by any Doctor client. Its intention is to check if the user is registered in the
    database as a doctor and with the right password.
    * **Requirements**: Requires a **Doctor** object with at least DNI and password.
    * **Possible answers**: 
        * _**LOGIN_ACCEPT**_
        * _**LOGIN_DENY**_
        * _**ERROR**_

* Protocol: _**GET_PATIENT_MEASURES**_

    * **Usage**: When the Doctor chooses a patient to look the measurements of, this protocol is called
    to retrieve the measurements of the patient.
    * **Requirements**: Requires a **Patient** object.
    * **Possible answers**:
        * _**PUSH_PATIENT_MEASURES**_ 
        * _**ERROR**_
        
* Protocol: _**PUSH_MEASUREMENT_COMMENT**_

    * **Usage**: to add a comment to the measurement of a patient.
        * **Requirements**: Requires a **Measurement** object.
        * **Possible answers**:
            * _**ACK**_ 
            * _**ERROR**_
    
###### From Server to Doctor
* Protocol: _**PUSH_PATIENT_MEASURES**_

    * **Usage**: Answers when the doctor wants the measurements of a particular patient.
    * **Requirements**: Requires an **ArrayList< Measurement >** to return. This list could be empty.
    * **Answers to**: 
        * _**GET_PATIENT_MEASURES**_

###### From Admin to Server
* Protocol: _**ADMIN_LOGIN**_

    * **Usage**: After a successful TCP connection, this should be the first message sent to the
    server by any admin client. Its intention is to check if the user is registered in the
    database as a admin and with the right password.
    * **Requirements**: Requires an **Administrator** object with at least DNI and password.
    * **Possible answers**: 
        * _**LOGIN_ACCEPT**_
        * _**LOGIN_DENY**_
        * _**ERROR**_
        
* Protocol: _**GET_DOCTORS**_

    * **Usage**: retrieve all doctors' ids and names so a new patient can be assigned to one of them
    * **Requirements**: no requirements;
    * **Possible answers**:
        * _**PUSH_DOCTORS**_ 
        * _**ERROR**_

* Protocol: _**REGISTER_PATIENT**_

    * **Usage**: to add a new patient to the database by an admin
    * **Requirements**: Requires a **Patient** object with the database requirements; when provided the
    server answers with _ACK_, otherwise with _ERROR_.
    * **Possible answers**:
        * _**ACK**_ 
        * _**ERROR**_

* Protocol: _**REGISTER_DOCTOR**_

    * **Usage**: to add a new patient to the database by an admin
    * **Requirements**: Requires a **Doctor** object with the database requirements; when provided the
    server answers with _ACK_, otherwise with _ERROR_.
    * **Possible answers**:
        * _**ACK**_ 
        * _**ERROR**_


* Protocol: _**REGISTER_ADMIN**_

    * **Usage**: to add a new patient to the database by an admin
    * **Requirements**: Requires a **Administrator** object with the database requirements; when provided the
    server answers with _ACK_, otherwise with _ERROR_.
    * **Possible answers**:
        * _**ACK**_ 
        * _**ERROR**_


* Protocol: _**SERVER_SHUTDOWN**_

    * **Usage**: When the administrator wants to shutdown the server
    * **Requirements**: An **Administrator** object with the id and the password of the administrator
    requesting the shutdown, for extra security.
    * **Possible answers**:
        * _**ACK**_ 
        * _**SERVER_SHUTDOWN_CONFIRM**_: Used in case there are other clients connected.
        * _**ERROR**_


* Protocol: _**SERVER_CANCEL_SHUTDOWN**_

    * **Usage**: When there are other clients connected to the server and the administrator decides not
    to shutdown the server.
    * **Requirements**: none.
    * **Possible answers**:
        * _**ACK**_ 
        * _**ERROR**_
 
        
###### From Server to Admin
* Protocol: _**SERVER_SHUTDOWN_CONFIRM**_

    * **Usage**: To answer _SERVER_SHUTDOWN_ if there are any other clients connected. 
    * **Requirements**: none
    * **Answers to**: 
        * _**SERVER_SHUTDOWN**_

* Protocol: _**PUSH_DOCTORS**_

    * **Usage**: To answer _GET_DOCTORS_ 
    * **Returns**: A **LinkedList< Doctor >**. The list might be empty if no doctors have been registered. 
    * **Answers to**: 
        * _**GET_DOCTORS**_  
         
###### From Server to ANY Client

* Protocol: _**LOGIN_ACCEPT**_

    * **Usage**: When the DNI and password provided by a protocol _LOGIN_ matches with
    those registered in the database, this protocol will be used as answer. 
    * **Returns**: 
        * A _Patient_ object will be returned, with all the information required, included the 
        measures taken in previous sessions if this was called _PATIENT_LOGIN_
        * A _Doctor_ object and a list of patients under their care if this was called from _DOCTOR_LOGIN_
    * **Answers to**: 
        * _**PATIENT_LOGIN**_
        * _**DOCTOR_LOGIN**_
        * _**ADMIN_LOGIN**_
        
* Protocol: _**LOGIN_DENY**_

    * **Usage**: When the DNI and password provided by a protocol _LOGIN_  doesn't
     match with those registered in the database, this protocol will be used as answer.
     When this protocol is sent, the connection will be closed, so the client needs to
     connect again. 
    * **Returns**: just the protocol.
    * **Answers to**: 
        * _**PATIENT_LOGIN**_
        * _**DOCTOR_LOGIN**_
        * _**ADMIN_LOGIN**_
        
* Protocol: _**ACK**_

    * **Usage**: Answer to _PUSH_MEASUREMENT_ when there was no error storing the measures 
    * **Returns**: just the protocol.
    * **Answers to**: 
        * _**PUSH_MEASUREMENT**_

* Protocol: _**ERROR**_

    * **Usage**: Answer to messages from the client when there is any error not related 
    to them, usually this might be database errors. Meant to inform the client to try
    again some other time. 
    * **Returns**: just the protocol.
    * **Answers to**: 
        * _**PATIENT_LOGIN**_
        * _**DOCTOR_LOGIN**_
        * _**ADMIN_LOGIN**_
        * _**PUSH_MEASUREMENT**_
        * _**GET_DOCTORS**_

    
###### Messages from either one

* Protocol: _**DISCONNECT**_

    * **Usage**: Whenever one side wants to close the connection for some reason.
    * **Returns**: just the protocol.