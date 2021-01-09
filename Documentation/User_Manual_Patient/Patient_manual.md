# Patient Manual

## Index
+ [Introduction](#introduction)
+ [IP chooser](#ip-chooser)
+ [login](#login)
+ [Bitalino Configuration](#bitalino-configuration)
+ [Record a new measurement](#record-a-new-measurement)
+ [List all measurements](#list-all-measurements)
+ [Measurement View](#measurement-view)

### Introduction 

This application has been programmed in the Java language. Therefore, in order to run it, you must make sure you have a Java compiler installed.


If you are a client and a patient you have to run the class Main.java in the gui package. 

### IP Chooser
First of all the application will ask you about the direction IP of the server you want to connect.
You have to introduce a valid ipv4 address or "localhost" is also allow.  If it is unable to do so, either because of an incorrect ip address was
introduced or the server is unavailable, a pop-up will appear indicating so. 

### Login 
Then another window will open where you must enter your ID (eight numbers and the letter which is case sensitive so you have to be careful and put it in capital letter) and password.
* DNI: **33333333Y**
* Password: **Craneos**

### Bitalino Configuration

Once clicked on bitalino button in the top left  you can configure the MAC address, and the different channels for starting recording your  biological signals.

### Record a new measurement

You can start recording a new measurement to send it to the hospital by clicking on the "new measurement" button. To do this, you will have had to correctly configure the bitalino.

You can indicate the temperature, press the record button to start recording your heart rate, oxygen saturation and ecg, it also has a check list to mark your symptoms.
* *Temperature*: it must be a real number
* *Record heart rate and oxygen saturation*: once clicked on the record button, the bitalino
will start recording for a minute and 10s for accuracy purposes. After recording the mean rate and
%O2 will be displayed. While recording the interface will not respond to other events.
* *Symptoms checklist*: the patient should check all the symptoms that he/she is experiencing. 
If none, then keep them all unchecked.
* *ECG*: once the record button is pressed the bitalino will start recording the ECG for 5s. The graph
with the resulting signal will be displayed afterwards. It is not mandatory to record the ECG

**Recording ECG**
To record the ECG here are some recommendations for the electrode placement.

![Configuration 1](../../images/ecg1.png)

For ECG we suggest a triangular electrode placement such as the image above illustrates
 where the ground should be the electrode in the middle (I). 
 The order of the other two electrodes is irrelevant (J and K).
 
 ![Configuration 2](../../images/ecg2.png)
 
 The other option is the Einthoven configuration. Here the ground electrode corresponds to the letter M, 
 the cathode to the letter N and the anode to the letter L.

### List all measurements


Clicking on the records button will appear all your measurements. Rows indicate a measurement and columns  show the date the measurement was taken, 
the heart rate, the oxygen saturation level, the temperature and the symptoms of the patient, and comments provided by a doctor.

To see the rest of the data of the measurement the patient will have to double click on any of the items of the table and then the [extra 
information}](#measurement-view) will appear below. The last column is a field where the doctor can write a comment associated to that measurement.
The patient cannot modify it. 

### Measurement View

You can see in detail the selected measurement.