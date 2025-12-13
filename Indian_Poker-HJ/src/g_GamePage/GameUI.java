package g_GamePage;

import javax.swing.*;
import java.awt.*;

public class GameUI {

    public JLabel lblEnemyCard, lblUserCard, lblMoney, lblInfo;
    public JButton btnCall, btnRaise, btnAllIn, btnFold;
    public JTextField txtRaise;
    public JButton btnChat;

    public JPanel rootPanel;

    private String myNickname;
    private String enemyNickname;
	public ChatWindow chatwindow;

    public GameUI(String myNickname, String enemyNickname) {
        this.myNickname = myNickname;
        this.enemyNickname = enemyNickname;
        buildUI();
    }

    private JLabel makeCardLabel() {
        JLabel l = new JLabel("", SwingConstants.CENTER);
        l.setPreferredSize(new Dimension(260, 360));
        l.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return l;
    }

    private JPanel titled(JComponent comp, String title) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Dialog", Font.BOLD, 14));
        p.add(t, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void buildUI() {

        rootPanel = new JPanel(new BorderLayout());

        // 상단
        JPanel top = new JPanel(new BorderLayout());
        lblInfo = new JLabel("상대 플레이어를 기다리는 중...", SwingConstants.LEFT);
        lblInfo.setFont(new Font("Dialog", Font.BOLD, 16));
        top.add(lblInfo, BorderLayout.WEST);
        
        // 오른쪽 상단
        btnChat = new JButton("채팅창");
        btnChat.setFocusPainted(false);
        top.add(btnChat, BorderLayout.EAST);

        // 중앙
        JPanel center = new JPanel(new GridLayout(1, 2, 20, 0));
        center.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        lblUserCard = makeCardLabel();
        lblEnemyCard = makeCardLabel();

        center.add(titled(lblUserCard, myNickname + "(나)"));
        center.add(titled(lblEnemyCard, enemyNickname + "(상대)"));

        // 하단
        JPanel bottom = new JPanel(new BorderLayout());
        lblMoney = new JLabel("나: 200원 | 상대: 200원", SwingConstants.CENTER);
        lblMoney.setFont(new Font("Dialog", Font.PLAIN, 14));

        JPanel btnPanel = new JPanel();
        btnCall = new JButton("CALL");
        btnRaise = new JButton("RAISE");
        btnAllIn = new JButton("ALL-IN");
        btnFold = new JButton("FOLD");
        txtRaise = new JTextField("20", 5);

        btnPanel.add(btnCall);
        btnPanel.add(btnRaise);
        btnPanel.add(txtRaise);
        btnPanel.add(btnAllIn);
        btnPanel.add(btnFold);

        bottom.add(lblMoney, BorderLayout.NORTH);
        bottom.add(btnPanel, BorderLayout.SOUTH);

        rootPanel.add(top, BorderLayout.NORTH);
        rootPanel.add(center, BorderLayout.CENTER);
        rootPanel.add(bottom, BorderLayout.SOUTH);
    }
}
