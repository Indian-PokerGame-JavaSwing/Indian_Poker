import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {
        int port = 30000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("서버 실행 중... 포트: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // 클라이언트 접속 대기
                System.out.println("클라이언트 연결됨: " + clientSocket.getInetAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                out.println("Welcome to Indian Poker Server!");

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("클라이언트: " + message);
                    out.println("서버 응답: " + message);

                    if (message.equalsIgnoreCase("exit")) {
                        System.out.println("클라이언트 종료 요청.");
                        break;
                    }
                }

                clientSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
