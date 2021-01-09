# Doctor Manual

## Index
+ [Introduction](#introduction)
+ [IP chooser](#ip-chooser)
+ [login](#login)
+ [Patient chooser](#patient-chooser)
+ [Measurement chooser](#measurement-chooser)
+ [Measurement view](#measurement-view)



### Introduction 

This application has been programmed in the Java language. Therefore, in order to run it, you must make sure you have Java installed.

### IP chooser

This first window is to choose the ip of the server you want to access too. Enter a valid ip address to continue. 
Once you click the button connect the program will try to connect to the server. If it is unable to do so, either 
because of an incorrect ip address was introduced or the server is unavailable, a pop-up will appear indicating so. 
If this happens please try again later, and if it happens repeatedly, contact the administrator. 

### Login

A login menu will appear once the program is able to connect to the server, in which the user is required to log in with
their Identification Number (eight numbers  and the letter which is case sensitive so you have to be careful and put it in capital letter) and a password given to them by the administrator. 
* DNI: **11111111Y**
* Password: **Craneos**

### Patient Chooser

Once authenticated by the server the Doctor will be given a list of the patients assigned to them. The list can be
sorted by different categories, such as name, date of birth and even sex. If the table is clicked, and there is a patient
selected, then the table will shift to the right of the screen and a [new table](#measurement-chooser) will appear with 
the measurements that particular patient has taken. The Patient list that is still on the right will still react to 
choosing a new patient, automatically bringing up the measurements taken by the new patient.

### Measurement Chooser

This is a table of the measurements taken by the Patient. This table can only be sorted by the date the measurements 
were taken. The table isn't showing all the information of the measurement, it shows the date the measurement was taken, 
the heart rate, the oxygen saturation level, the temperature and the symptoms of the patient; to see the rest of the 
data of the measurement the doctor will have to double click on any of the items of the table and then the [extra 
information}](#measurement-view) will appear below. If the doctor then double clicks on another item of the table, the 
information below will update to that of the new measurement.

### Measurement View

Once clicked on the table a small panel will appear below, with three elements. The first is a Line chart that will 
show the ECG record ( if there was any ) taken during the measurement. The second element is a field in which the doctor
can see the comment associated to that measurement, if there was any. If the doctor feels like the measurement requires 
a comment, or wants to update a previous comment, they can do so writing in this area, but the changes won't take effect 
until the third item is pressed; this third item is the submit button, which will indicate the program that the doctor 
has finished redacting their comment about the measurement. 

