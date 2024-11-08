package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port) 
  {
    try 
    {
      client= new ChatClient(host, port, this);
      
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        client.handleMessageFromClientUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  /**
   * anything that starts with # should be considered a command.
   * @param message
   */
  public void handleUserInput(String message) {
    if (message.startsWith("#")) {
        processCommand(message);
    } else {
        try {
            client.sendToServer(message);
        } catch (IOException e) {
            System.out.println("Error: Could not send message to server. Disconnected.");
        }
    }
  }

  /**
   * implement specified commands 
   * @param command
   */
  private void processCommand(String command) {
    String[] parts = command.split(" ");
    String cmd = parts[0];
    
    switch (cmd) {
      case "#quit":
        quit();
        break;

      case "#logoff":
          try {
              client.closeConnection();
          } catch (IOException e) {
              System.out.println("Error: Could not log off from server.");
          }
          break;

      case "#sethost":
          if (!client.isConnected()) {
              client.setHost(parts[1]);
          } else {
              System.out.println("Error: Disconnect before setting a new host.");
          }
          break;

      case "#setport":
          if (!client.isConnected()) {
              client.setPort(Integer.parseInt(parts[1]));
          } else {
              System.out.println("Error: Disconnect before setting a new port.");
          }
          break;

      case "#login":
          if (!client.isConnected()) {
              try {
                  client.openConnection();
              } catch (IOException e) {
                  System.out.println("Error: Could not connect to server.");
              }
          } else {
              System.out.println("Error: Already connected to server.");
          }
          break;

      case "#gethost":
          System.out.println("Current host: " + client.getHost());
          break;

      case "#getport":
          System.out.println("Current port: " + client.getPort());
          break;

      default:
          System.out.println("Unknown command: " + cmd);
          break;
    }
}

private void quit() {
    try {
        client.closeConnection();
    } catch (IOException e) {
        System.out.println("Error: Could not disconnect from server.");
    } finally {
        System.exit(0);
    }
}

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    String host = args.length > 0 ? args[0] : "localhost"; // Default will be localhost
    int port = args.length > 1 ? Integer.parseInt(args[1]) : ChatClient.DEFAULT_PORT; // this will be default port if not provided

    ClientConsole console = new ClientConsole(host, port);
    console.accept();


    try
    {
      host = args[0];
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      host = "localhost";
    }
    ClientConsole chat= new ClientConsole(host, DEFAULT_PORT);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
