package org.maxmailservice;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private UserManager userManager;

    public Server(int port) {
        this.port = port;
        this.userManager = new UserManager();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            System.out.println("CSV files " + (userManager.csvFilesExist() ? "found" : "created"));

            new Thread(this::handleConsoleCommands).start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                new Thread(new ClientHandler(clientSocket, this)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserManager getUserManager() {
        return userManager;
    }

    private void handleConsoleCommands() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            if (command.startsWith("/adduser")) {
                String[] parts = command.split(" ");
                if (parts.length == 3) {
                    String username = parts[1];
                    String password = parts[2];
                    userManager.addUser(new User(username, password));
                    System.out.println("User " + username + " added");
                } else {
                    System.out.println("Usage: /adduser <username> <password>");
                }
            } else if (command.startsWith("/deluser")) {
                String[] parts = command.split(" ");
                if (parts.length == 2) {
                    String username = parts[1];
                    userManager.removeUser(username);
                    System.out.println("User " + username + " removed");
                } else {
                    System.out.println("Usage: /deluser <username>");
                }
            } else if (command.equals("/deldata")) {
                userManager.deleteAllData();
                System.out.println("All data deleted");
            } else if (command.startsWith("/port")) {
                String[] parts = command.split(" ");
                if (parts.length == 2) {
                    try {
                        int newPort = Integer.parseInt(parts[1]);
                        this.port = newPort;
                        System.out.println("Port changed to " + newPort);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid port number");
                    }
                } else {
                    System.out.println("Usage: /port <newPort>");
                }
            } else if (command.equals("/listusers")) {
                System.out.println("List of users:");
                for (String username : userManager.getUsers().keySet()) {
                    System.out.println(username);
                }
            } else {
                System.out.println("Unknown command");
            }
        }
    }
}