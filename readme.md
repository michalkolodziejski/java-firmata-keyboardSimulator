Firmata/Arduino keyboard simulator
==================================

## Build status
[![Build Status](https://buildhive.cloudbees.com/job/michalkolodziejski/job/java-firmata-keyboardSimulator/badge/icon)](https://buildhive.cloudbees.com/job/michalkolodziejski/job/java-firmata-keyboardSimulator/)
             
## Description
Reads pushbutton status from Arduino board and send as key events on host computer

## Circuit design
![image](https://raw.githubusercontent.com/michalkolodziejski/java-firmata-keyboardSimulator/master/images/circuit_arduino.png)

## Credits
*** Special thanks goes to a team that is responsible for [firmata4j Project](https://github.com/kurbatov/firmata4j).*** 
    
## Uploading Firmata To Arduino
Arduino IDE is shipped with an implementation of Firmata protocol so all you has
to do is upload that firmware. You can do it as follows:

- Plug your Arduino to the computer
- Launch Arduino IDE
- Select `File -> Examples -> Firmata -> StandardFirmata` in IDE's menu
- Select your board in `Tools -> Board`
- Select the port in `Tools -> Port` (it is already selected if you has uploaded something to your Arduino)
- Click on `Upload` button

## TODO
- GUI

## History

2014-09-25

* Initial version.

## Problems?

[Submit an issue](https://github.com/michalkolodziejski/java-firmata-keyboardSimulator/issues).
