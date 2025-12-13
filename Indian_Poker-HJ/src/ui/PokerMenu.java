package ui;

import javax.swing.*;
import java.awt.*;

public class PokerMenu extends JFrame {

    private String nickname;

    public PokerMenu(String nickname) {
        this.nickname = nickname;
        initUI();
    }

    public PokerMenu() {
        this("게스트");
    }

    private void initUI() {
        setTitle("Indian Poker - Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // 배경 패널
        MenuBackgroundPanel panel = new MenuBackgroundPanel("/img/menu.jpg");

        // 환영 메시지
        JLabel welcome = new JLabel(nickname + "님 환영합니다!");
        welcome.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        welcome.setForeground(Color.WHITE);
        welcome.setBounds(600, 20, 300, 30);
        panel.add(welcome);

        // 버튼 생성
        JButton btnStart = MenuButtonFactory.createButton("게임 시작", 50, 150);
        JButton btnSettings = MenuButtonFactory.createButton("설정", 50, 230);
        JButton btnExit = MenuButtonFactory.createButton("종료", 50, 310);

        // 이벤트 핸들러 분리
        MenuActionHandler handler = new MenuActionHandler(this, nickname);

        btnStart.addActionListener(e -> handler.handleStart());
        btnSettings.addActionListener(e -> handler.handleSettings());
        btnExit.addActionListener(e -> handler.handleExit());

        panel.add(btnStart);
        panel.add(btnSettings);
        panel.add(btnExit);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new PokerMenu("테스트유저");
    }
}
