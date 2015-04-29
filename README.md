# EECS3214-A2
## EECS 3214 (Computer Network Protocols and Applications) Assignment 2 [MIRROR]

### Requirements

The objective of this lab is to synchronize several clients to connect to a server 
at a predetermined time. This is a part of the requirements of a Distributed Denial
of Service (DDoS) attack.

The parties involved in this project are 
* a coordinator who communicates to the attackers a time of attack 
* attackers who serve as clients to open a connection to the server and
* the server, who allows TCP connections on some designated port. 
  The objective of the attackers is to open a TCP connection (each) to the 
  server at as close a time to the time specified by the coordinator as possible.
  Notice that for this functionality to be supported the attackers must be in server
  mode (listening on a port) until they hear from the coordinator and then they switch
  to being in client mode, connecting to the server at the specified time.

Demonstrate that your program works by using one coordinator, 4 clients and a server.
Record the times of opening the connections at the server as accurately as you can.
Keep each connection alive 10-30 seconds to allow overlap between connections.
Have the server write the log of connections to a file.

For details, see `REQUIREMENTS.html` or https://bitbucket.org/vwchu/eecs3214-a2/downloads/02.pdf
Also see the report.pdf for implementation and design details.

### Note

This project will not be maintained.
Use at your own risk.
