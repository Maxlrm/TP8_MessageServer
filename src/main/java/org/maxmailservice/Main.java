package org.maxmailservice;

public class Main {
    public static void main(String[] args) {
        int port = 53245; // Port par défaut
        Server server = new Server(port);
        server.start();
    }
}