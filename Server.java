import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static Set<String> users = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static Set<Server.Handler> usersList = new HashSet<>();

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(100);

        try (ServerSocket listener = new ServerSocket(5678)) {
            while (true) {
                Server.Handler newUser = new Server.Handler(listener.accept());
                pool.execute(newUser);
                usersList.add(newUser);
            }
        }
    }

  
    private static class Handler implements Runnable {
        private Socket socket;
        private String name;
        private Scanner in;
        private PrintWriter out;

       
        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void printUsers()
        {
            if(users != null && !users.isEmpty())
            {
                out.println("LIST " + users.size());
                for (String username : users) 
                    out.println(username);
            }   
            else
                out.println("There is no user yet");
        }
      
        public void printPrivateMsg(String private_msg)
        {
            if (private_msg.contains("[") && private_msg.contains("]"))
                {
                  String[] parts = private_msg.split("]"); //split names and message
                  parts[0] = parts[0].substring(1);        // cut the [ symbol
                  String[] private_users = parts[0].split(" "); //split usernames
                  String message = parts[1];              //save private message to send
                        for(String pr_user : private_users)
                        {
                            if(users.contains(pr_user))
                            {
                               for(Server.Handler user : usersList)
                               {
                                 if(user.name.equals(pr_user))
                                 {  
                                     user.out.println("Private Message");
                                     user.out.println(user.name + ": " + message);                                
                                 }
                                }
                            }
                            else out.println("user "+ pr_user + " doesn't exist");
                        }                            
                }
            else 
                out.println("Please use [ ] to indicate users");   
        }
        
        
        
        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("WHO");
                    name = in.nextLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (users) {                        
                        if ( name != null  && name.startsWith("IAM")) 
                        {
                            name = name.substring(4);
                            if(users.contains(name))
                               out.println("This username is already taken ... ");
                            else if(name.contains(" ")) 
                            {                                                                
                                out.println("Please write a username without space");
                            }                                
                            else
                            {  
                                users.add(name);
                                break;
                            }
                        }                       
                        else if(name != null && name.equals("WHO"))
                        {
                            printUsers();
                        }
                    }
                }

                out.println("Welcome " + name);
                for (PrintWriter writer : writers)
                {
                    writer.println("ARV " + name );
                }
                System.out.println(name + " joined the chat");
                writers.add(out);

                while (true) 
                {
                    String input = in.nextLine();
                    
                    if (input.equals("BYE")) 
                    {
                        out.println("GOOD BYE" );
                        //System.exit(0);
                            return;
                    }
                    else if(input.startsWith("MSG"))
                    {
                        for (PrintWriter writer : writers) 
                        {
                            //if(writer != this.out)
                                writer.println(name + ": " + input.substring(4));
                        }
                    }
                     else if(input.startsWith("PRV"))
                     {
                        String private_msg = input.substring(4);
                        printPrivateMsg(private_msg);                
                     }
                     else if(input.equals("WHO"))
                     {
                        printUsers();
                     }
                    else    
                    {
                          out.println("Unknown MESSAGE " );       
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                    usersList.remove(this);
                }
                if (name != null) 
                {
                    users.remove(name);
                    for (PrintWriter writer : writers) 
                    {
                        writer.println( name + " has left ");
                    }
                   System.out.println(name + " has left the chat");
                 
                }
                try 
                { 
                    socket.close(); 
                } 
                catch (IOException e)
                {
                    System.out.println(e);
                }
            }
        }
    }
}