# Network Protocol

## Index
+ [Network Data](#network-data)
+ [Network Messages](#network-messages)
+ [Network Messages Protocols](#networkmessages-protocol-enumeration)
    + Clients to server
        + [Patient to Server](#from-patient-to-server)
            + [Patient login](#protocol-_patient_login_)
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

### Protocol _**PATIENT_LOGIN**_

* **Usage**: After a successful TCP connection, this should be the first message sent to the
    server by any patient client. Its intention is to check if the user is registered in the
    database as a patient and with the right password.
* **Requirements**: Requires a **Patient** object with at least DNI and password.
* **Possible answers**: 
    * [_**LOGIN_ACCEPT**_](#protocol-_login_accept_)
    * [_**LOGIN_DENY**_](#protocol-_login_deny_)
    * [_**ERROR**_](#protocol-_error_)

### Protocol: _**PUSH_MEASUREMENT**_

* **Usage**: Once the client has logged in with a patient DNI and password, they can 
    upload as many measurements as they want. It can't be done before login in with _PATIENT_LOGIN_
* **Requirements**: Requires a **ArrayList< Measurement >** of at least 1 measurement.
* **Possible answers**:
    * [_**ACK**_](#protocol-_ack_) 
    * [_**ERROR**_](#protocol-_error_)

### **From Server to Patient**

Currently there are no specific messages from the server to the patient client. 
        
### **From Doctor to Server**
### Protocol: _**DOCTOR_LOGIN**_

* **Usage**: After a successful TCP connection, this should be the first message sent to the
    server by any Doctor client. Its intention is to check if the user is registered in the
    database as a doctor and with the right password.
* **Requirements**: Requires a **Doctor** object with at least DNI and password.
* **Possible answers**: 
    * [_**LOGIN_ACCEPT**_](#protocol-_login_accept_)
    * [_**LOGIN_DENY**_](#protocol-_login_deny_)
    * [_**ERROR**_](#protocol-_error_)

### Protocol: _**GET_PATIENT_MEASURES**_

* **Usage**: When the Doctor chooses a patient to look the measurements of, this protocol is called
    to retrieve the measurements of the patient.
* **Requirements**: Requires a **Patient** object.
* **Possible answers**:
    * [_**PUSH_PATIENT_MEASURES**_](#protocol-_push_patient_measures_) 
    * [_**ERROR**_](#protocol-_error_)
        
### Protocol: _**PUSH_MEASUREMENT_COMMENT**_

* **Usage**: to add a comment to the measurement of a patient.
* **Requirements**: Requires a **Measurement** object.
* **Possible answers**:
    * [_**ACK**_](#protocol-_ack_) 
    * [_**ERROR**_](#protocol-_error_)
    
### **From Server to Doctor**
### Protocol: _**PUSH_PATIENT_MEASURES**_

* **Usage**: Answers when the doctor wants the measurements of a particular patient.
* **Requirements**: Requires an **ArrayList< Measurement >** to return. This list could be empty.
* **Answers to**: 
    * [_**GET_PATIENT_MEASURES**_](#protocol-_get_patient_measures_)

### **From Administrator to Server**
### Protocol: _**ADMIN_LOGIN**_

* **Usage**: After a successful TCP connection, this should be the first message sent to the
    server by any admin client. Its intention is to check if the user is registered in the
    database as a admin and with the right password.
* **Requirements**: Requires an **Administrator** object with at least DNI and password.
* **Possible answers**: 
    * [_**LOGIN_ACCEPT**_](#protocol-_login_accept_)
    * [_**LOGIN_DENY**_](#protocol-_login_deny_)
    * [_**ERROR**_](#protocol-_error_)
        
### Protocol: _**GET_DOCTORS**_

* **Usage**: retrieve all doctors' ids and names so a new patient can be assigned to one of them
* **Requirements**: no requirements;
* **Possible answers**:
    * [_**PUSH_DOCTORS**_](#protocol-_push_doctors_) 
    * [_**ERROR**_](#protocol-_error_)

### Protocol: _**REGISTER_PATIENT**_

* **Usage**: to add a new patient to the database by an admin
* **Requirements**: Requires a **Patient** object with the database requirements; when provided the
    server answers with _ACK_, otherwise with _ERROR_.
* **Possible answers**:
    * [_**ACK**_](#protocol-_ack_) 
    * [_**ERROR**_](#protocol-_error_)

### Protocol: _**REGISTER_DOCTOR**_

* **Usage**: to add a new patient to the database by an admin
* **Requirements**: Requires a **Doctor** object with the database requirements; when provided the
    server answers with _ACK_, otherwise with _ERROR_.
* **Possible answers**:
    * [_**ACK**_](#protocol-_ack_) 
    * [_**ERROR**_](#protocol-_error_)


### Protocol: _**REGISTER_ADMIN**_

* **Usage**: to add a new patient to the database by an admin
* **Requirements**: Requires a **Administrator** object with the database requirements; when provided the
    server answers with _ACK_, otherwise with _ERROR_.
* **Possible answers**:
    * [_**ACK**_](#protocol-_ack_) 
    * [_**ERROR**_](#protocol-_error_)


### Protocol: _**SERVER_SHUTDOWN**_

* **Usage**: When the administrator wants to shutdown the server
* **Requirements**: An **Administrator** object with the id and the password of the administrator
    requesting the shutdown, for extra security.
* **Possible answers**:
    * [_**ACK**_](#protocol-_ack_) 
    * [_**SERVER_SHUTDOWN_CONFIRM**_](#protocol-_server_shutdown_confirm_): Used in case there are other clients connected.
    * [_**ERROR**_](#protocol-_error_)


### Protocol: _**SERVER_CANCEL_SHUTDOWN**_

* **Usage**: When there are other clients connected to the server and the administrator decides not
    to shutdown the server.
* **Requirements**: none.
* **Possible answers**:
    * [_**ACK**_](#protocol-_ack_) 
    * [_**ERROR**_](#protocol-_error_)
 
        
### **From Server to Administrator**
### Protocol: _**SERVER_SHUTDOWN_CONFIRM**_

* **Usage**: To answer _SERVER_SHUTDOWN_ if there are any other clients connected. 
* **Requirements**: none
* **Answers to**: 
    * [_**SERVER_SHUTDOWN**_](#protocol-_server_shutdown_)

### Protocol: _**PUSH_DOCTORS**_

* **Usage**: To answer _GET_DOCTORS_ 
* **Returns**: A **LinkedList< Doctor >**. The list might be empty if no doctors have been registered. 
* **Answers to**: 
    * [_**GET_DOCTORS**_](#protocol-_get_doctors_)  
         
### **From Server to ANY Client**

### Protocol: _**LOGIN_ACCEPT**_

* **Usage**: When the DNI and password provided by a protocol _LOGIN_ matches with
    those registered in the database, this protocol will be used as answer. 
* **Returns**: 
    * A _Patient_ object will be returned, with all the information required, included the 
        measures taken in previous sessions if this was called _PATIENT_LOGIN_
    * A _Doctor_ object and a list of patients under their care if this was called from _DOCTOR_LOGIN_
* **Answers to**: 
    * [_**PATIENT_LOGIN**_](#protocol-_patient_login_)
    * [_**DOCTOR_LOGIN**_](#protocol-_doctor_login_)
    * [_**ADMIN_LOGIN**_](#protocol-_admin_login_)
        
### Protocol: _**LOGIN_DENY**_

* **Usage**: When the DNI and password provided by a protocol _LOGIN_  doesn't
     match with those registered in the database, this protocol will be used as answer.
     When this protocol is sent, the connection will be closed, so the client needs to
     connect again. 
* **Returns**: just the protocol.
* **Answers to**: 
    * [_**PATIENT_LOGIN**_](#protocol-_patient_login_)
    * [_**DOCTOR_LOGIN**_](#protocol-_doctor_login_)
    * [_**ADMIN_LOGIN**_](#protocol-_admin_login_)
        
### Protocol: _**ACK**_

* **Usage**: Answer to protocols that don't require anything else, when there was no error executing the protocol. 
* **Returns**: just the protocol.
* **Answers to**: 
    * [_**PUSH_MEASUREMENT**_](#protocol-_push_measurement_) 
    * [_**PUSH_MEASUREMENT_COMMENT**_](#protocol-_push_measurement_comment_)
    * [_**REGISTER_PATIENT**_](#protocol-_register_patient_)
    * [_**REGISTER_DOCTOR**_](#protocol-_register_doctor_)
    * [_**REGISTER_ADMIN**_](#protocol-_register_admin_)
    * [_**SERVER_SHUTDOWN**_](#protocol-_server_shutdown_)
    * [_**SERVER_CANCEL_SHUTDOWN**_](#protocol-_server_cancel_shutdown_)

### Protocol: _**ERROR**_

* **Usage**: Answer to messages from the client when there is any error not related 
    to them, usually this might be database errors. Meant to inform the client to try
    again some other time. 
* **Returns**: just the protocol.
* **Answers to**: 
    + _**Any message from a client to the server**_

    
### **Messages from either one**

### Protocol: _**DISCONNECT**_

* **Usage**: Whenever one side wants to close the connection for some reason.
* **Returns**: just the protocol.