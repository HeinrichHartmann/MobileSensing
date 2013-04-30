Hints for developers:
=====================
1. 
This test project was developed and tested with the Eclipse Versions 3.6.2 (Helios) 
and Version 3.5.1 (Galileo) for the Android Api Level 7 ( Revision 2.1 ).
The related Android SDK version is 15.


2. 
You need the "SDCFramework" Project to execute the tests. 

3.
In Eclipse right click on a package and choose Debug As - Android JUnit Test 
to run the tests of a test package.

4.
Use the existing launch configuration "TestSDCFrameworkConfiguration.launch" 
to just validate the current XML configuration file in the SDCFramework project.

5. 
Emma code coverage is available for Ant 1.8 or higher. 
The build.xml ant script parameters are: "clean emma debug install test uninstall".
If you got errors with this command, try it in two steps: "clean emma debug" 
followed by "emma install test uninstall" .