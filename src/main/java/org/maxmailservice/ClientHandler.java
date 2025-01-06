package org.maxmailservice;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private BufferedReader input;
    private PrintWriter output;
    private String currentUser;

    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.currentUser = null;
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String request;
            while ((request = input.readLine()) != null) {
                handleRequest(request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequest(String request) {
        String[] parts = request.split(" ");
        String command = parts[0];

        switch (command) {
            case "LOGIN":
                handleLogin(parts);
                break;
            case "REGISTER":
                handleRegister(parts);
                break;
            case "SEND":
                handleSend(parts);
                break;
            case "DELETE":
                handleDelete(parts);
                break;
            case "LOAD_MESSAGES":
                handleLoadMessages(parts);
                break;
            case "LOGOUT":
                handleLogout();
                break;
            default:
                output.println("UNKNOWN COMMAND");
                break;
        }
    }

    private void handleLogin(String[] parts) {
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];
            boolean success = server.getUserManager().authenticate(username, password);
            if (success) {
                currentUser = username;
            }
            output.println(success ? "SUCCESS" : "FAILURE");
        } else {
            output.println("INVALID COMMAND");
        }
    }

    private void handleRegister(String[] parts) {
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];
            User user = new User(username, password);
            server.getUserManager().addUser(user);
            output.println("SUCCESS");
        } else {
            output.println("INVALID COMMAND");
        }
    }

    private void handleSend(String[] parts) {
        if (parts.length >= 3) {
            String recipient = parts[1];
            String content = String.join(" ", (CharSequence[]) java.util.Arrays.copyOfRange(parts, 2, parts.length)).replace("\\n", "\n");
            User recipientUser = server.getUserManager().getUser(recipient);
            User senderUser = server.getUserManager().getUser(currentUser);
            if (recipientUser != null && senderUser != null) {
                Message message = new Message(currentUser, recipient, content);
                recipientUser.addMessage(message);
                senderUser.addMessage(message); // Add the message to the sender's list as well
                output.println("SUCCESS");
            } else {
                output.println("FAILURE");
            }
        } else {
            output.println("INVALID COMMAND");
        }
    }

    private void handleDelete(String[] parts) {
        if (parts.length == 2) {
            String messageId = parts[1];
            User user = server.getUserManager().getUser(currentUser);
            if (user != null && user.removeMessage(Integer.parseInt(messageId))) {
                output.println("SUCCESS");
            } else {
                output.println("FAILURE");
            }
        } else {
            output.println("INVALID COMMAND");
        }
    }

    private void handleLoadMessages(String[] parts) {
        if (parts.length == 2) {
            String messageType = parts[1];
            User user = server.getUserManager().getUser(currentUser);
            if (user != null) {
                for (Message message : user.getMessages()) {
                    if ((messageType.equals("RECEIVED") && message.getRecipient().equals(currentUser)) ||
                            (messageType.equals("SENT") && message.getSender().equals(currentUser))) {
                        output.println(message.getFormattedMessage());
                    }
                }
                output.println("END_OF_MESSAGES");
            } else {
                output.println("FAILURE");
            }
        } else {
            output.println("INVALID COMMAND");
        }
    }

    private void handleLogout() {
        currentUser = null;
        output.println("SUCCESS");
    }
}