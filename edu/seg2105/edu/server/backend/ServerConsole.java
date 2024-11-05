package edu.seg2105.edu.server.backend;

import java.io.IOException;
import java.util.Scanner;

import edu.seg2105.client.common.ChatIF;

public class ServerConsole implements ChatIF {
    private EchoServer server;

    public ServerConsole(int port) {
        server = new EchoServer(port);
        try {
            server.listen();
        } catch (IOException e) {
            System.out.println("Error: Can't listen for clients.");
        }
    }

    public void accept() throws IOException {
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                String message = scanner.nextLine();
                if (message.startsWith("#")) {
                    processCommand(message);
                } else {
                    server.handleMessageFromServerConsole(message);
                }
            }
        } finally {
            scanner.close();
        }
    }
    

    private void processCommand(String command) {
        String[] parts = command.split(" ");
        String cmd = parts[0];
    
        switch (cmd) {
            case "#quit":
                System.exit(0);
                break;
    
            case "#stop":
                server.stopListening();
                break;
    
            case "#close":
                try {
                    server.close();
                } catch (IOException e) {
                    System.out.println("Error closing the server: " + e.getMessage());
                }
                break;
    
            case "#setport":
                if (!server.isListening()) {
                    server.setPort(Integer.parseInt(parts[1]));
                } else {
                    System.out.println("Error: Stop server before setting port.");
                }
                break;
    
            case "#start":
                if (!server.isListening()) {
                    try {
                        server.listen();
                    } catch (IOException e) {
                        System.out.println("Error starting server: " + e.getMessage());
                    }
                } else {
                    System.out.println("Error: Server is already listening.");
                }
                break;
    
            case "#getport":
                System.out.println("Current port: " + server.getPort());
                break;
    
            default:
                System.out.println("Unknown command: " + cmd);
                break;
        }
    }    

    @Override
    public void display(String message) {
        System.out.println(message);
    }
}

