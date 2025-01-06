package org.maxmailservice;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, User> users;
    private static final String USERS_FILE = "users.csv";

    public UserManager() {
        this.users = new HashMap<>();
        loadUsersFromCSV();
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
        saveUsersToCSV();
    }

    public void removeUser(String username) {
        users.remove(username);
        saveUsersToCSV();
    }

    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public boolean deleteMessage(String messageId) {
        for (User user : users.values()) {
            if (user.removeMessage(Integer.parseInt(messageId))) {
                saveUsersToCSV();
                return true;
            }
        }
        return false;
    }

    public void deleteAllData() {
        File usersFile = new File(USERS_FILE);
        if (usersFile.exists()) {
            usersFile.delete();
        }
        for (User user : users.values()) {
            File messagesFile = new File("messages_" + user.getUsername() + ".csv");
            if (messagesFile.exists()) {
                messagesFile.delete();
            }
        }
        users.clear();
    }

    public boolean csvFilesExist() {
        File usersFile = new File(USERS_FILE);
        return usersFile.exists();
    }

    private void loadUsersFromCSV() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    String username = data[0];
                    String password = data[1];
                    users.put(username, new User(username, password));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsersToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                pw.println(user.getUsername() + "," + user.getPassword());
                user.saveMessagesToCSV();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}