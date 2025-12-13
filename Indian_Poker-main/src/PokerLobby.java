import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PokerLobby extends JFrame {

    private Image backgroundImage;

    public PokerLobby() {
        setTitle("Indian Poker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // 배경 이미지 로드
        backgroundImage = new ImageIcon(getClass().getResource("/lobby.png")).getImage();

        // 배경 패널 생성
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);

        // ✅ 버튼 이미지 불러오기
        ImageIcon startIcon = new ImageIcon(getClass().getResource("/play.png"));
        ImageIcon quitIcon = new ImageIcon(getClass().getResource("/exit.png"));

        // ✅ START 버튼
        JButton startButton = new JButton(startIcon);
        startButton.setBounds(50, 480, 350, 100);
        startButton.setBorderPainted(false);       // 테두리 제거
        startButton.setContentAreaFilled(false);   // 배경 제거
        startButton.setFocusPainted(false);        // 클릭 시 테두리 제거
        startButton.addActionListener(e -> openLoginWindow());
        backgroundPanel.add(startButton);

        // ✅ QUIT 버튼
        JButton quitButton = new JButton(quitIcon);
        quitButton.setBounds(470, 480, 350, 100);
        quitButton.setBorderPainted(false);
        quitButton.setContentAreaFilled(false);
        quitButton.setFocusPainted(false);
        quitButton.addActionListener(e -> System.exit(0));
        backgroundPanel.add(quitButton);

        // ✅ 패널 추가 후 표시
        add(backgroundPanel);
        setVisible(true);
    }
    
    private void openLoginWindow() {
        // 새로운 작은 창
        JFrame loginFrame = new JFrame("닉네임 입력");
        loginFrame.setSize(400, 200);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(null);
        loginFrame.setResizable(false);

        JLabel label = new JLabel("닉네임을 입력하세요:");
        label.setBounds(50, 30, 300, 25);
        loginFrame.add(label);

        JTextField nicknameField = new JTextField();
        nicknameField.setBounds(50, 60, 280, 30);
        loginFrame.add(nicknameField);

        JButton confirmButton = new JButton("확인");
        confirmButton.setBounds(150, 110, 100, 30);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = nicknameField.getText().trim();

                if (nickname.isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "닉네임을 입력해주세요!", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 로그인 창 닫기
                loginFrame.dispose();
                // 로비 닫기
                dispose();

                // 다음 메뉴로 이동
                new PokerMenu();  // 아직 구현 전, 이후 메뉴 화면 클래스 연결
                System.out.println("닉네임: " + nickname);
            }
        });
        loginFrame.add(confirmButton);

        loginFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PokerLobby::new);
    }
}
