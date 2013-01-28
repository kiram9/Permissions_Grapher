Permissions_Grapher
===================
A set of three applications to show the transmission from a protected resource on an androi device to the internet



This is a visualization application that allows you to see potential interactions 
between multiple android applications - Eg. this way if you wanted to be sure that 
no information could leak from one part of your android phone to the internet 
you could run this to find all potential paths through multiple applications that 
would allow information to leak from one protected resource. 

For Example - your address book may be very important to you - and an enemy 
wishes to read this information. They may write an application that has 
permission to read your address book and also connect to the internet. 
Using this application they can fetch your addresses and upload them to 
their command and control server. 

Now lets say you are very smart - and know they will do this - so you never 
install a program that can read your address book and also connect to the internet.
Well your enemy needs to come up with a better plan. They invent a way to do this. 

They can share the information through a shared resource such as your file system
and use two applications to sneakily leak data through a shared resource. 

Eg: 
Application1 has access to address book and SD card. 
Applcation2 has access to SD card and internet. 

Now your addresses can move from Address book -> SD card -> Internet.

Permissions grapher is used to figure out any potential route from a protected 
resource on your phone to the internet through a graphical map. Enjoy!

The Stealcontacts1 and Stealcontacts2 are demo applications used to push addresses from a users address book into a php form online. 
