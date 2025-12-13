import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 30000;

        try (Socket socket = new Socket(host, port)) {
            System.out.println("서버에 연결됨: " + host + ":" + port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            // 서버 초기 메시지 출력
            System.out.println(in.readLine());

            while (true) {
                System.out.print("메시지 입력: ");
                String msg = scanner.nextLine();
                out.println(msg);

                if (msg.equalsIgnoreCase("exit")) break;

                // 서버 응답 출력
                System.out.println(in.readLine());
            }

            System.out.println("클라이언트 종료.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
