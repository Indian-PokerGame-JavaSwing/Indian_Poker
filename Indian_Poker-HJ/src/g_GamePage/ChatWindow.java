package g_GamePage;

import javax.swing.*;
import java.awt.*;

public class ChatWindow extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    private NetworkClient net;

    public ChatWindow(NetworkClient net) {
        this.net = net;

        setTitle("채팅");
        setSize(350, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // =====================
        // 채팅 표시 영역
        // =====================
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(chatArea);
        add(scroll, BorderLayout.CENTER);

        // =====================
        // 입력 영역
        // =====================
        inputField = new JTextField();
        sendButton = new JButton("전송");

        sendButton.addActionListener(e -> sendChat());
        inputField.addActionListener(e -> sendChat());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    // =================================================
    // 🔥 채팅 전송
    //  - 클라이언트 → 서버: "CHAT 메시지"
    // =================================================
    private void sendChat() {
        String msg = inputField.getText().trim();
        if (msg.isEmpty()) return;

        net.send("CHAT " + msg);   // ⭐ 닉네임 절대 붙이지 말 것
        inputField.setText("");
    }

    // =================================================
    // 🔥 채팅 수신 (MessageHandler에서 호출)
    // =================================================
    public void addMessage(String nick, String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(nick + " : " + msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength()); // 자동 스크롤
        });
    }
}
