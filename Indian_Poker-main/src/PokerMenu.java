import java.awt.*;
import javax.swing.*;

public class PokerMenu extends JFrame {

    private Image backgroundImage;

    public PokerMenu() {
        setTitle("Indian Poker - Select Mode");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        backgroundImage = new ImageIcon(getClass().getResource("/menu.jpg")).getImage();

		JPanel backgroundPanel = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		    }
		};

        backgroundPanel.setLayout(null); // 절대 좌표로 버튼 배치

        // 🎮 버튼 생성
        JButton btn2 = createStyledButton("2인용", 50, 150);
        JButton btn3 = createStyledButton("3인용", 50, 230);
        JButton btn4 = createStyledButton("4인용", 50, 310);
        JButton btnExit = createStyledButton("게임 종료", 50, 390);

        // 버튼 이벤트 (기능은 나중에 추가)
        btnExit.addActionListener(e -> System.exit(0));

        // 버튼 추가
        backgroundPanel.add(btn2);
        backgroundPanel.add(btn3);
        backgroundPanel.add(btn4);
        backgroundPanel.add(btnExit);

        add(backgroundPanel);
        setVisible(true);
    }

    // 🎨 버튼 스타일 공통화
    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 120, 50);
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 16));
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

    // ⚡ 실행용 main (테스트)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PokerMenu::new);
    }
}
