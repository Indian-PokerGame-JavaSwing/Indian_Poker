package g_GamePage;

import javax.swing.*;

public class PlayIndianPoker extends JFrame {

    private GameUI ui;
    private NetworkClient net;
    private MessageHandler handler;
    private ChatWindow chatWindow;

    private final String IP = "localhost";
    private final int PORT = 50000;

    private String nickname;

    public PlayIndianPoker(String nickname) {
        this.nickname = nickname;

        setTitle("Indian Poker - " + nickname);
        setSize(820, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ui = new GameUI(nickname, "상대");
        handler = new MessageHandler(ui, this);

        try {
            net = new NetworkClient(IP, PORT, handler);
            net.send("NICK " + nickname);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "서버 연결 실패!");
            return;
        }

        wireButtonEvents();
        add(ui.rootPanel);
        setVisible(true);
    }

    private void wireButtonEvents() {
    	
    	ui.btnChat.addActionListener(e -> {
    		if (chatWindow == null) {
    			chatWindow = new ChatWindow(net);
    		} else {
    			chatWindow.setVisible(true);
    			chatWindow.toFront();
    		}
    	});
        ui.btnCall.addActionListener(e -> net.send("CALL"));
        ui.btnFold.addActionListener(e -> net.send("FOLD"));
        ui.btnAllIn.addActionListener(e -> net.send("ALLIN"));

        ui.btnRaise.addActionListener(e -> {
            String text = ui.txtRaise.getText().trim();
            if (!text.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "숫자만 입력하세요!");
                return;
            }
            net.send("RAISE " + text);
        });
    }
    
    
    public void deliverChat(String sender, String message) {
        if (chatWindow != null) {
            chatWindow.addMessage(sender, message);
        }
    }    
    public void showGameOver(String winner) {
        JOptionPane.showMessageDialog(this, winner + " 승리!");
        System.exit(0);
    }

    public static void main(String[] args) {
        new PlayIndianPoker("Player");
    }
}
