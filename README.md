# Client-server-protocole
Simple multiuser chat application with Java and swing
 
   To compile :
 * run javac Server.java
 * run javac Client.java     
 
  Note: By default the host is 127.0.0.1 and the port is 5678, But it's changeable. 
  
  To execute:
 * java Server
 * java Client (with different port it would something like this: java Client localhost 8888)
 
 Usage :
 
* To identify user after server request WHO respond  with the message  IAM "username" .    
 
    Note: username should not contain any space
* Type WHO to see all the connected users
* Send public messages with the following syntax MSG "text"
* To send private messages write PRV [user1 user2] "text"
* To live the chat simply type BYE
* In any other cases server will respond the message: "Unknown MESSAGE ".

  It means message was not sent and the user should use one of the command from the above sections.
