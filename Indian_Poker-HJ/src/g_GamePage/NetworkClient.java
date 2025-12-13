package g_GamePage;

import java.io.*;
import java.net.Socket;

public class NetworkClient {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private MessageHandler handler;

    public NetworkClient(String ip, int port, MessageHandler handler) throws Exception {
        this.handler = handler;
        socket = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        startReceiver();
    }

    public void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReceiver() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    handler.handle(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void close() {
        try { socket.close(); } catch (Exception ignored) {}
    }
}
