import java.io.*;
import java.net.*;

public class ServerEx {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("서버입니다. 클라이언트를 기다립니다...");

            clientSocket = serverSocket.accept(); // 클라이언트 접속 대기
            System.out.println("연결되었습니다.");

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            while (true) {
                String msg = in.readLine();  // 클라이언트로부터 입력
                if (msg == null) break;

                if (msg.equalsIgnoreCase("bye")) {
                    System.out.println("접속을 종료합니다.");
                    break;
                }

                System.out.println("... " + msg); // 서버 화면에 출력
                out.println(msg); // 클라이언트에게 그대로 돌려줌
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
