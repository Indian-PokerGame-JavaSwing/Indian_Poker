import java.awt.*;
import javax.swing.*;

import g_GamePage.PlayIndianPoker;

public class PokerMenu extends JFrame {

    private Image backgroundImage;

    public PokerMenu() {
        setTitle("Indian Poker - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // 🔹 배경 이미지 로드
        backgroundImage = new ImageIcon(getClass().getResource("/menu.jpg")).getImage();

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(null); // 절대 위치 배치

        // 🎮 버튼 생성 (위에서부터 게임 시작 / 설정 / 종료)
        JButton btnStart = createStyledButton("게임 시작", 50, 150);
        JButton btnSettings = createStyledButton("설정", 50, 230);
        JButton btnExit = createStyledButton("종료", 50, 310);

        // ==========================
        // 버튼 기능 연결
        // ==========================

        // 🟢 게임 시작 버튼 → PlayIndianPoker 실행
        btnStart.addActionListener(e -> {
            dispose();  // 메뉴 창 닫기
            new PlayIndianPoker(); // 게임 실행
        });

        // ⚙ 설정 버튼 → 아직 기능 없음
        btnSettings.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "설정 기능은 곧 추가됩니다!",
                "설정",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        // 🔴 종료 버튼
        btnExit.addActionListener(e -> System.exit(0));

        // 버튼 추가
        backgroundPanel.add(btnStart);
        backgroundPanel.add(btnSettings);
        backgroundPanel.add(btnExit);

        add(backgroundPanel);
        setVisible(true);
    }

    // 🎨 공통 버튼 스타일
    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 150, 50);
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(85, 50, 150)); // 보라색 계열
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 마우스 오버 효과
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(110, 70, 180));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(85, 50, 150));
            }
        });

        return button;
    }

    // ⚡ 실행 테스트용 main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PokerMenu::new);
    }
}
