import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientEx {
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        Scanner scanner = new Scanner(System.in);

        try {
            socket = new Socket("localhost", 9999);
            System.out.println("서버에 접속하였습니다...");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                System.out.print("텍스트 입력 >> ");
                String msg = scanner.nextLine();
                out.println(msg); // 서버에 전송

                if (msg.equalsIgnoreCase("bye")) {
                    System.out.println("연결을 종료합니다.");
                    break;
                }

                String echo = in.readLine(); // 서버가 돌려준 메시지
                if (echo != null)
                    System.out.println("서버로부터: " + echo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                scanner.close();
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
