import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatServer extends JFrame implements ActionListener {
    private BufferedReader in = null;
    private BufferedWriter out = null;
    private ServerSocket listener = null;
    private Socket socket = null;
    private Receiver receiver; // 클라이언트에서 받은 메시지를 표시할 JTextArea
    private JTextField sender; // 서버가 보낼 메시지를 입력할 JTextField

    public ChatServer() {
        setTitle("서버 채팅 창");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        receiver = new Receiver();
        receiver.setEditable(false);
        sender = new JTextField();
        sender.addActionListener(this);

        add(new JScrollPane(receiver), BorderLayout.CENTER);
        add(sender, BorderLayout.SOUTH);

        setSize(400, 200);
        setVisible(true);

        try {
            setupConnection();
        } catch (IOException e) {
            handleError(e.getMessage());
        }

        Thread th = new Thread(receiver);
        th.start();
    }

    private void setupConnection() throws IOException {
        listener = new ServerSocket(9999);
        receiver.append("클라이언트 연결 대기 중...\n");
        socket = listener.accept(); // 클라이언트 연결 수락
        receiver.append("클라이언트로부터 연결 완료\n");

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private static void handleError(String msg) {
        System.out.println("Error: " + msg);
        System.exit(1);
    }

    // 서버가 직접 입력해서 메시지를 보내는 경우
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sender) {
            String msg = sender.getText();
            try {
                out.write(msg + "\n");
                out.flush();

                receiver.append("\n서버: " + msg);
                int pos = receiver.getText().length();
                receiver.setCaretPosition(pos);
                sender.setText(null);
            } catch (IOException e1) {
                handleError(e1.getMessage());
            }
        }
    }

    // 메시지를 수신하고, 번역해서 응답을 보내는 스레드
    private class Receiver extends JTextArea implements Runnable {
        @Override
        public void run() {
            String msg = null;
            while (true) {
                try {
                    msg = in.readLine(); // 클라이언트로부터 메시지 수신
                } catch (IOException e) {
                    handleError(e.getMessage());
                }

                if (msg == null) continue;

                // 클라이언트 메시지를 표시
                this.append("\n클라이언트: " + msg);
                int pos = this.getText().length();
                this.setCaretPosition(pos);

                // 번역 처리
                String translated = translateToKorean(msg);

                try {
                    // 번역 결과를 클라이언트로 전송
                    out.write(translated + "\n");
                    out.flush();

                    // 서버 화면에도 번역 결과 출력
                    this.append("\n서버(번역): " + translated);
                    pos = this.getText().length();
                    this.setCaretPosition(pos);
                } catch (IOException e) {
                    handleError(e.getMessage());
                }
            }
        }

        // 간단한 영어 → 한글 사전
        private String translateToKorean(String eng) {
            switch (eng.toLowerCase()) {
                case "java": return "자바";
                case "apple": return "사과";
                case "cat" : return "고양이";
                case "dog" : return "개";
                //case "
                default: return "조금 쉬운 단어를 보내주세요.";
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }
}
