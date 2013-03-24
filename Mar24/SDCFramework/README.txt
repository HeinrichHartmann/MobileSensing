General:
========
*
This project does use Simple XML ( http://simple.sourceforge.net ),
published under the Apache License. 
Along with the source the license is provided in a subdirectory of the "lib" folder.

Hints for developers:
=====================
1. 
This project was developed and tested with the Eclipse Versions 3.6.2 (Helios) 
and Version 3.5.1 (Galileo) for the Android Api Level 7 ( Revision 2.1 ) and Target Level 8 ( Revision 2.1 ).

2. 
If you change the configuration file "SDCConfig.xml" in the asset folder 
do clean the project before deploying the application again. 
Otherwise it may happen, that the asset files won't be uploaded as expected.

3.
For the usage of RSA encryption the public key should be generated and stored using the Encryption 
class in the package "de.unikassel.android.sdcframework.util.facade". 
The generated public key file "public.key" must be stored in the asset folder to be available 
for the deployed service. The corresponding "private.key" is used to decrypt transferred samples 
on the server side. The related methods can be found in the Encrypt class. It is part of the 
"sdcframework-X.X.X-independent.jar" library, which is intended to be used for deserialisation 
and decryption in pure java environments.