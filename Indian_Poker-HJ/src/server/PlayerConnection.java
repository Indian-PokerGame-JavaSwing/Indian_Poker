package server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PlayerConnection {

    private BufferedReader in;
    private BufferedWriter out;

    private BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

    public PlayerConnection(Socket socket) throws Exception {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        startReaderThread();
    }

    private void startReaderThread() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    commandQueue.offer(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public String take() throws InterruptedException {
        return commandQueue.take(); // 🔥 non-blocking-safe
    }

    public void send(String msg) throws IOException {
        out.write(msg + "\n");
        out.flush();
    }
}
