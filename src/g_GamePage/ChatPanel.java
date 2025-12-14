package g_GamePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ChatPanel extends JPanel {
    
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private BufferedWriter out;
    
    public ChatPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280, 600));
        setBackground(new Color(35, 87, 60));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        initUI();
    }
    
    private void initUI() {
        // 상단: 채팅 제목
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(35, 87, 60));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("💬 채팅", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setForeground(new Color(255, 215, 0));
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // 중앙: 채팅 메시지 표시 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatArea.setBackground(new Color(20, 40, 30));
        chatArea.setForeground(new Color(240, 255, 240));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setText("환영합니다! 🎮\n게임을 즐기세요!\n");
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // 하단: 입력 영역
        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setBackground(new Color(35, 87, 60));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        
        inputField = new JTextField();
        inputField.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        inputField.setBackground(new Color(255, 255, 255));
        inputField.setForeground(new Color(30, 30, 30));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        sendButton = new JButton("전송");
        sendButton.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        sendButton.setBackground(new Color(46, 125, 50));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setPreferredSize(new Dimension(70, 40));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 전송 버튼 호버 효과
        sendButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (sendButton.isEnabled()) {
                    sendButton.setBackground(new Color(56, 142, 60));
                }
            }
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(new Color(46, 125, 50));
            }
        });
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        add(inputPanel, BorderLayout.SOUTH);
        
        // 이벤트 리스너
        setupListeners();
    }
    
    private void setupListeners() {
        // 전송 버튼 클릭
        sendButton.addActionListener(e -> sendMessage());
        
        // Enter 키로 전송
        inputField.addActionListener(e -> sendMessage());
    }
    
    /**
     * 메시지 전송
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        
        if (message.isEmpty()) {
            return;
        }
        
        if (out != null) {
            try {
                // 채팅 소켓으로 메시지만 전송 (CHAT: 프리픽스 불필요)
                out.write(message + "\n");
                out.flush();
                
                // 내 메시지 표시
                appendMyMessage(message);
                inputField.setText("");
                
            } catch (IOException e) {
                appendSystemMessage("메시지 전송 실패");
            }
        } else {
            appendSystemMessage("서버 연결 안 됨");
        }
    }
    
    /**
     * 내가 보낸 메시지 표시
     */
    public void appendMyMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("나: " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    /**
     * 상대방 메시지 표시
     */
    public void appendOpponentMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("상대: " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    /**
     * 시스템 메시지 표시
     */
    public void appendSystemMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("[시스템] " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    /**
     * 채팅 전용 출력 스트림 설정
     */
    public void setChatOutputStream(BufferedWriter out) {
        this.out = out;
    }
    
    /**
     * 채팅창 초기화
     */
    public void clearChat() {
        chatArea.setText("");
    }
}
