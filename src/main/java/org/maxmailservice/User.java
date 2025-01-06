package org.maxmailservice;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<Message> messages;
    private static final String MESSAGES_FILE_PREFIX = "messages_";

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.messages = new ArrayList<>();
        loadMessagesFromCSV();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
        saveMessagesToCSV();
    }

    public boolean removeMessage(int messageId) {
        boolean removed = messages.removeIf(message -> message.getId() == messageId);
        if (removed) {
            saveMessagesToCSV();
        }
        return removed;
    }

    private void loadMessagesFromCSV() {
        File file = new File(MESSAGES_FILE_PREFIX + username + ".csv");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 4);
                if (data.length == 4) {
                    int id = Integer.parseInt(data[0]);
                    String sender = data[1];
                    String recipient = data[2];
                    String content = data[3];
                    messages.add(new Message(id, sender, recipient, content));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMessagesToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MESSAGES_FILE_PREFIX + username + ".csv"))) {
            for (Message message : messages) {
                pw.println(message.getId() + "," + message.getSender() + "," + message.getRecipient() + "," + message.getContent().replace("\n", "\\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}