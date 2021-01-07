# Network Protocol

## Index
+ [Network Data](#network-data)
+ [Network Messages](#network-messages)
+ [Network Messages Protocols](#networkmessages-protocol-enumeration)
    + Clients to server
        + [Patient to Server](#from-patient-to-server)
            + [Patient login](#protocol-patient_login)
            + [Push measurement](#protocol-_push_measurement_)
        + [Doctor to Server](#from-doctor-to-server)
            + [Doctor login](#protocol-_doctor_login_)
            + [Get patient measures](#protocol-_get_patient_measures_)
            + [Push measurement comment](#protocol-_push_measurement_comment_)
        + [Administrator to server](#from-administrator-to-server)
            + [Admin login](#protocol-_admin_login_)
            + [Get Doctors](#protocol-_get_doctors_)
            + [Register Patient](#protocol-_register_patient_)
            + [Register Doctor](#protocol-_register_doctor_)
            + [Register Administrator](#protocol-_register_admin_)
            + [Server shutdown](#protocol-_server_shutdown_)
            + [Server cancel shutdown](#protocol-_server_cancel_shutdown_)
    + Server to clients
        + [Server to Patient](#from-server-to-patient)
        + [Server to Doctor](#from-server-to-doctor)
            + [Push patient measures](#protocol-_push_patient_measures_)
        + [Server to Admin](#from-server-to-administrator)
            + [Push doctors](#protocol-_push_doctors_)
            + [Server shutdown confirm](#protocol-_server_shutdown_confirm_)
        + [Server to any](#from-server-to-any-client)
            + [Login accept](#protocol-_login_accept_)
            + [Login deny](#protocol-_login_deny_)
            + [ACK](#protocol-_ack_)
            + [Error](#protocol-_error_)
    + [From either one](#messages-from-either-one)
        + [Disconnect](#protocol-_disconnect_)
    
---

## **Network data**
* Server Address: **TO BE DETERMINED**
* Server Port: **22333**

---

## **Network Messages**
The server listens to objects of the type **NetworkMessage**. These messages have
a mandatory **Protocol** attribute which will determine what the message purpose is.
The type "Protocol" is an enumeration of the different protocols the server recognizes.
Depending on the protocol attribute, the **NetworkMessage** object will have to contain the
other attributes. 
---
## **NetworkMessages Protocol Enumeration** 
Here we will see what each protocol implies and what should be expected in the **NetworkMessage**
object with it.

### **From Patient to Server**

#### Protocol PATIENT_LOGIN

* **Usage**: After a successful TCP connection, this should be the first message sent to the
    server by any patient client. Its intention is to check if the user is registered in the
    database as a patient and with the right password.
* **Requirements**: Requires a **Patient** object with at least DNI and password.
* **Possible answers**: 
    * [_**LOGIN_ACCEPT**_](#protocol-login_accept)
    * [_**LOGIN_DENY**_](#protocol-login_deny)
    * [_**ERROR**_](#protocol-error)

#### Protocol: PUSH_MEASUREMENT

* **Usage**: Once the client has logged in with a patient DNI and password, they can 
    upload as many measurements as they want. It can't be done before login in with _PATIENT_LOGIN_
* **Requirements**: Requires a **ArrayList< Measurement >** of at least 1 measurement.
* **Possible answers**:
    * [_**ACK**_](#protocol-ack) 
    * [_**ERROR**_](#protocol-error)

### **From Server to Patient**

Currently there are no specific messages from the server to the patient client. 
        
### **From Doctor to Server**
#### Protocol: DOCTOR_LOGIN

* **Usage**: After a successful TCP connection, this should be the first message sent to the
    server by any Doctor client. Its intention is to check if the user is registered in the
    database as a doctor and with the right password.
* **Requirements**: Requires a **Doctor** object with at least DNI and password.
* **Possible answers**: 
    * [_**LOGIN_ACCEPT**_](#protocol-login_accept)
    * [_**LOGIN_DENY**_](#protocol-login_deny)
    * [_**ERROR**_](#protocol-error)

#### Protocol: GET_PATIENT_MEASURES

* **Usage**: When the Doctor chooses a patient to look the measurements of, this protocol is called
    to retrieve the measurements of the patient.
* **Requirements**: Requires a **Patient** object.
* **Possible answers**:
    * [_**PUSH_PATIENT_MEASURES**_](#protocol-push_patient_measures) 
    * [_**ERROR**_](#protocol-error)
        
#### Protocol: PUSH_MEASUREMENT_COMMENT

* **Usage**: to add a comment to the measurement of a patient.
* **Requirements**: Requires a **Measurement** object.
* **Possible answers**:
    * [_**ACK**_](#protocol-ack) 
    * [_**ERROR**_](#protocol-error)
    
### **From Server to Doctor**
#### Protocol: PUSH_PATIENT_MEASURES

* **Usage**: Answers when the doctor wants the measurements of a particular patient.
* **Requirements**: Requires an **ArrayList< Measurement >** to return. This list could be empty.
* **Answers to**: 
    * [_**GET_PATIENT_MEASURES**_](#protocol-get_patient_measures)

### **From Administrator to Server**
#### Protocol: ADMIN_LOGIN

* **Usage**: After a successful TCP connection, this should be the first message sent to the
    server by any admin client. Its intention is to check if the user is registered in the
    database as a admin and with the right password.
* **Requirements**: Requires an **Administrator** object with at least DNI and password.
* **Possible answers**: 
    * [_**LOGIN_ACCEPT**_](#protocol-login_accept)
    * [_**LOGIN_DENY**_](#protocol-login_deny)
    * [_**ERROR**_](#protocol-error)
        
#### Protocol: GET_DOCTORS

* **Usage**: retrieve all doctors' ids and names so a new patient can be assigned to one of them
* **Requirements**: no requirements;
* **Possible answers**:
    * [_**PUSH_DOCTORS**_](#protocol-push_doctors) 
    * [_**ERROR**_](#protocol-error)

#### Protocol: REGISTER_PATIENT

* **Usage**: to add a new patient to the database by an admin
* **Requirements**: Requires a **Patient** object with the database requirements; when provided the
    server answers with _ACK_, otherwise with _ERROR_.
* **Possible answers**:
    * [_**ACK**_](#protocol-ack) 
    * [_**ERROR**_](#protocol-error)

#### Protocol: REGISTER_DOCTOR

* **Usage**: to add a new patient to the database by an admin
* **Requirements**: Requires a **Doctor** object with the database requirements; when provided the
    server answers with _ACK_, otherwise with _ERROR_.
* **Possible answers**:
    * [_**ACK**_](#protocol-ack) 
    * [_**ERROR**_](#protocol-error)


#### Protocol: REGISTER_ADMIN

* **Usage**: to add a new patient to the database by an admin
* **Requirements**: Requires a **Administrator** object with the database requirements; when provided the
    server answers with _ACK_, otherwise with _ERROR_.
* **Possible answers**:
    * [_**ACK**_](#protocol-ack) 
    * [_**ERROR**_](#protocol-error)


#### Protocol: SERVER_SHUTDOWN

* **Usage**: When the administrator wants to shutdown the server
* **Requirements**: An **Administrator** object with the id and the password of the administrator
    requesting the shutdown, for extra security.
* **Possible answers**:
    * [_**ACK**_](#protocol-ack) 
    * [_**SERVER_SHUTDOWN_CONFIRM**_](#protocol-server_shutdown_confirm): Used in case there are other clients connected.
    * [_**ERROR**_](#protocol-error)


#### Protocol: SERVER_SHUTDOWN_CONFIRM

* **Usage**: To answer _SERVER_SHUTDOWN_CONFIRM sent by the server. Shut downs the server even though other clients are connected. 
* **Requirements**: none
* **Answers to**: 
    * [_**SERVER_SHUTDOWN_CONFIRM**_](#protocol-server_shutdown_confirm)


#### Protocol: SERVER_CANCEL_SHUTDOWN

* **Usage**: When there are other clients connected to the server and the administrator decides not
    to shutdown the server.
* **Requirements**: none.
* **Possible answers**:
    * [_**ACK**_](#protocol-ack) 
    * [_**ERROR**_](#protocol-error)
 
        
### **From Server to Administrator**
#### Protocol: SERVER_SHUTDOWN_CONFIRM

* **Usage**: To answer _SERVER_SHUTDOWN_ if there are any other clients connected. 
* **Requirements**: none
* **Answers to**: 
    * [_**SERVER_SHUTDOWN**_](#protocol-server_shutdown)

#### Protocol: PUSH_DOCTORS

* **Usage**: To answer _GET_DOCTORS_ 
* **Returns**: A **LinkedList< Doctor >**. The list might be empty if no doctors have been registered. 
* **Answers to**: 
    * [_**GET_DOCTORS**_](#protocol-get_doctors)  
         
### **From Server to ANY Client**

#### Protocol: LOGIN_ACCEPT

* **Usage**: When the DNI and password provided by a protocol _LOGIN_ matches with
    those registered in the database, this protocol will be used as answer. 
* **Returns**: 
    * A _Patient_ object will be returned, with all the information required, included the 
        measures taken in previous sessions if this was called _PATIENT_LOGIN_
    * A _Doctor_ object and a list of patients under their care if this was called from _DOCTOR_LOGIN_
* **Answers to**: 
    * [_**PATIENT_LOGIN**_](#protocol-patient_login)
    * [_**DOCTOR_LOGIN**_](#protocol-doctor_login)
    * [_**ADMIN_LOGIN**_](#protocol-admin_login)
        
#### Protocol: LOGIN_DENY

* **Usage**: When the DNI and password provided by a protocol _LOGIN_  doesn't
     match with those registered in the database, this protocol will be used as answer.
     When this protocol is sent, the connection will be closed, so the client needs to
     connect again. 
* **Returns**: just the protocol.
* **Answers to**: 
    * [_**PATIENT_LOGIN**_](#protocol-patient_login)
    * [_**DOCTOR_LOGIN**_](#protocol-doctor_login)
    * [_**ADMIN_LOGIN**_](#protocol-admin_login)
        
#### Protocol: ACK

* **Usage**: Answer to protocols that don't require anything else, when there was no error executing the protocol. 
* **Returns**: just the protocol.
* **Answers to**: 
    * [_**PUSH_MEASUREMENT**_](#protocol-push_measurement) 
    * [_**PUSH_MEASUREMENT_COMMENT**_](#protocol-push_measurement_comment)
    * [_**REGISTER_PATIENT**_](#protocol-register_patient)
    * [_**REGISTER_DOCTOR**_](#protocol-register_doctor)
    * [_**REGISTER_ADMIN**_](#protocol-register_admin)
    * [_**SERVER_SHUTDOWN**_](#protocol-server_shutdown)
    * [_**SERVER_CANCEL_SHUTDOWN**_](#protocol-server_cancel_shutdown)

#### Protocol: ERROR

* **Usage**: Answer to messages from the client when there is any error not related 
    to them, usually this might be database errors. Meant to inform the client to try
    again some other time. 
* **Returns**: just the protocol.
* **Answers to**: 
    + _**Any message from a client to the server**_

    
### **Messages from either one**

#### Protocol: DISCONNECT

* **Usage**: Whenever one side wants to close the connection for some reason.
* **Returns**: just the protocol.