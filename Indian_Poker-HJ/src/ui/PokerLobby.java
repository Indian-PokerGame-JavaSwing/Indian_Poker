package ui;

import javax.swing.*;

public class PokerLobby extends JFrame {

    public PokerLobby() {

        setTitle("Indian Poker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // 🔹 배경 패널로 분리된 클래스 사용
        BackgroundPanel panel = new BackgroundPanel("/img/lobby.png");
        panel.setLayout(null);

        // 🔹 버튼 이미지
        ImageIcon startIcon = new ImageIcon(getClass().getResource("/img/play.png"));
        ImageIcon quitIcon = new ImageIcon(getClass().getResource("/img/exit.png"));

        JButton btnStart = new JButton(startIcon);
        btnStart.setBounds(50, 480, 350, 100);
        btnStart.setBorderPainted(false);
        btnStart.setContentAreaFilled(false);
        btnStart.setFocusPainted(false);

        btnStart.addActionListener(e -> openLoginWindow());

        JButton btnQuit = new JButton(quitIcon);
        btnQuit.setBounds(470, 480, 350, 100);
        btnQuit.setBorderPainted(false);
        btnQuit.setContentAreaFilled(false);
        btnQuit.setFocusPainted(false);
        btnQuit.addActionListener(e -> System.exit(0));

        panel.add(btnStart);
        panel.add(btnQuit);

        add(panel);
        setVisible(true);
    }

    private void openLoginWindow() {
        new LoginWindow(nickname -> {

            dispose(); // 로비 닫기

            // 메뉴 실행
            new PokerMenu(nickname);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PokerLobby::new);
    }
}
