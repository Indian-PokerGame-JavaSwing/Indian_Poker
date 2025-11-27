import java.io.*;
import java.net.*;
import java.util.*;

public class ServerEx {
    static volatile boolean isRunning = true;

    public static void main(String[] args) {
        BufferedReader in = null;
        BufferedWriter out = null;
        ServerSocket listener = null;
        Socket socket = null;
        Scanner scanner = new Scanner(System.in, "CP949");
        try {
            listener = new ServerSocket(9999);
            System.out.println("연결을 기다리고 있습니다...");
            socket = listener.accept();
            System.out.println("연결되었습니다.");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            Thread receiver = new Thread(new Receiver(in, socket));
            receiver.start();

            while (isRunning) {
                System.out.print(">>");
                if (!scanner.hasNextLine()) break;
                String outputMessage = scanner.nextLine();
                out.write(outputMessage + "\n");
                out.flush();

                if (outputMessage.equalsIgnoreCase("bye") || outputMessage.equalsIgnoreCase("끝")) {
                    System.out.println(">>클라이언트와의 연결을 종료합니다.");
                    isRunning = false;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                scanner.close();
                if (socket != null) socket.close();
                if (listener != null) listener.close();
            } catch (IOException e) {}
        }
    }

    static class Receiver implements Runnable {
        private BufferedReader in;
        private Socket socket;

        public Receiver(BufferedReader in, Socket socket) {
            this.in = in;
            this.socket = socket;
        }

        public void run() {
            try {
                while (isRunning) {
                    String inputMessage = in.readLine();
                    if (inputMessage == null) break;

                    if (inputMessage.equalsIgnoreCase("bye") || inputMessage.equalsIgnoreCase("끝")) {
                        System.out.println(">>클라이언트에서 bye로 연결을 종료하였음");
                        isRunning = false;
                        try { socket.close(); } catch (IOException e) {}
                        break;
                    }

                    System.out.println("클라이언트 : " + inputMessage);
                    System.out.print(">>");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
