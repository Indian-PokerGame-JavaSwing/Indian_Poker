package server;

import java.net.ServerSocket;
import java.net.Socket;

public class IndianPokerServer {

    private static final int PORT = 50000;

    public static void main(String[] args) {
        System.out.println("IndianPokerServer: waiting on port " + PORT);

        try (ServerSocket listener = new ServerSocket(PORT)) {

            Socket p1 = listener.accept();
            System.out.println("Player1 connected");

            Socket p2 = listener.accept();
            System.out.println("Player2 connected");

            new GameRoom(p1, p2).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
