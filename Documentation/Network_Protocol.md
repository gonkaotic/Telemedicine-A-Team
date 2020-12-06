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

###### From Client to Server
* Protocol: _**GET_PATIENT**_

    * **Usage**: After a successful connection, this should be the first message sent to the
    server by any client. Its intention is to check if the user is registered in the
    database and with the right password.
    * **Requirements**: Requires a **Patient** object with at least DNI and password.
    * **Possible answers**: 
        * _**PUSH_PATIENT**_
        * _**DENY_PATIENT**_
        * _**ERROR**_

* Protocol: _**PUSH_MEASUREMENT**_

    * **Usage**: Once the client has logged in with a patient DNI and password, they can 
    upload as many measurements as they want. it can't be done before loggin with _GET_PATIENT_
    * **Requirements**: Requires a **ArrayList< Measurement >** of at least 1 measurement.
    * **Possible answers**:
        * _**ACK**_ 
        * _**ERROR**_
    
###### From Server to Client

* Protocol: _**PUSH_PATIENT**_

    * **Usage**: When the DNI and password provided by protocol _GET_PATIENT_ matches with
    those registered in the database, this protocol will be used as answer. 
    * **Returns**: a _Patient_ object will be returned, with all the information 
    required, included the measures taken in previous sessions.
    * **Answers to**: 
        * _**GET_PATIENT**_
        
* Protocol: _**DENY_PATIENT**_

    * **Usage**: When the DNI and password provided by protocol _GET_PATIENT_ doesn't
     match with those registered in the database, this protocol will be used as answer.
     When this protocol is sent, the connection will be closed, so the client needs to
     connect again. 
    * **Returns**: just the protocol.
    * **Answers to**: 
        * _**GET_PATIENT**_
        
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
        * _**GET_PATIENT**_
        * _**PUSH_MEASUREMENT**_

    
###### Messages from either one

* Protocol: _**DISCONNECT**_

    * **Usage**: Whenever one side wants to close the connection for some reason.
    * **Returns**: just the protocol.