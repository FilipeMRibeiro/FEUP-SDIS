# FEUP-SDIS LAB 1

## SERVER

Open a terminal. Use the following command to run:

java Server <port_number>

where <port_number> is the port number on which the server waits for requests.


You may need to compile before running by using:

javac Server.java


## CLIENT

Open a different terminal. Use the following command to run:

java Server <host_name> <port_address> [register <plate_number> <owner_name> | lookup <plate_number>]

where <host_name> is the name of the host running the server;
      
      <port_number> is the server port;
      
      <plate_number> is the plate number you which to register or lookup; 
      
      <owner_name> is the name of the owner you wish to register.


You may need to compile before running by using:

javac Client.java

