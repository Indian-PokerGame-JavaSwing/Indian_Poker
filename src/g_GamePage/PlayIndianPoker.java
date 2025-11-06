package g_GamePage;

import normalclass.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * 인디언 포커 (단일 JFrame)
 * - 상대 카드 1장, 내 카드 1장
 * - CALL / FOLD 로 베팅
 * - 라운드별 정산
 */
public class PlayIndianPoker extends JFrame {

    // ===== 게임 상태 =====
    private Dealer dealer;
    private User user;
    private Enemy enemy;
    private int pot = 0;
    private final int ANTE = 10;
    private final int CALL_AMOUNT = 10;

    // ===== UI 구성요소 =====
    private JLabel lblEnemyCard, lblUserCard, lblPot, lblMoney, lblInfo;
    private JButton btnCall, btnFold, btnNext;

    // 카드 이미지 경로
    private final String CARD_IMG_DIR = System.getProperty("user.dir") + "/src/plus_Card/";
    private final String CARD_BACK_IMG = System.getProperty("user.dir") + "/src/plus_Card/CardBackImg.png"; // ✅ 경로 수정

    private Card userCard;
    private Card enemyCard;

    private enum Phase { DEAL, BETTING, REVEAL, SETTLE }
    private Phase phase = Phase.DEAL;

    // ===== 생성자 =====
    public PlayIndianPoker() {
        setTitle("Indian Poker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initModel();
        initUI();
        startNewRound();

        setVisible(true);
    }

    // ===== 데이터 모델 초기화 =====
    private void initModel() {
        dealer = new Dealer();
        dealer.shuffle();

        user = new User("You", 200, true);
        enemy = new Enemy("AI");
    }

    // ===== UI 초기화 =====
    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        lblInfo = new JLabel("환영합니다. 인디언 포커를 시작합니다.", SwingConstants.LEFT);
        lblInfo.setFont(lblInfo.getFont().deriveFont(Font.BOLD, 16f));
        top.add(lblInfo, BorderLayout.WEST);

        lblPot = new JLabel("POT: 0", SwingConstants.RIGHT);
        lblPot.setFont(lblPot.getFont().deriveFont(Font.BOLD, 16f));
        top.add(lblPot, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // 중앙 카드
        JPanel center = new JPanel(new GridLayout(1, 2, 20, 0));
        center.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        lblEnemyCard = createCardLabel();
        lblUserCard = createCardLabel();
        center.add(titled(lblUserCard, "내 카드"));
        center.add(titled(lblEnemyCard, "상대 카드"));


        add(center, BorderLayout.CENTER);

        // 하단 버튼
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        lblMoney = new JLabel(statusMoneyText(), SwingConstants.CENTER);
        lblMoney.setFont(lblMoney.getFont().deriveFont(Font.PLAIN, 14f));
        bottom.add(lblMoney, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        btnCall = new JButton("CALL (" + CALL_AMOUNT + ")");
        btnFold = new JButton("FOLD");
        btnNext = new JButton("NEXT ROUND");
        btnNext.setEnabled(false);

        btnCall.addActionListener(e -> onPlayerCall());
        btnFold.addActionListener(e -> onPlayerFold());
        btnNext.addActionListener(e -> startNewRound());

        buttons.add(btnCall);
        buttons.add(btnFold);
        buttons.add(btnNext);
        bottom.add(buttons, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    private JLabel createCardLabel() {
        JLabel l = new JLabel("", SwingConstants.CENTER);
        l.setPreferredSize(new Dimension(260, 360));
        l.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return l;
    }

    private JPanel titled(JComponent comp, String title) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 14f));
        p.add(t, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private String statusMoneyText() {
        return String.format("나: %d원   |   AI: %d원", user.getMoney(), enemy.getMoney());
    }

    // ===== 게임 라운드 진행 =====
    private void startNewRound() {
        if (dealer.remaining() < 10) {
            dealer.reset();
            dealer.shuffle();
        }

        phase = Phase.DEAL;
        pot = 0;

        userCard = dealer.dealOne();
        enemyCard = dealer.dealOne();

        pot += payAnte(user, ANTE) + payAnte(enemy, ANTE);

        // 🔹 라운드 시작 시 — 상대 카드 뒷면 / 내 카드 앞면
        setEnemyCardFaceDown();
        setUserCardFaceUp(userCard);

        updatePotText();
        setInfoText("상대 카드가 가려져 있습니다. CALL / FOLD 중 선택하세요.");
        lblMoney.setText(statusMoneyText());

        btnCall.setEnabled(true);
        btnFold.setEnabled(true);
        btnNext.setEnabled(false);

        phase = Phase.BETTING;
    }

    private void onPlayerCall() {
        if (phase != Phase.BETTING) return;

        pot += payCall(user, CALL_AMOUNT);

        UserAI ai = new UserAI();
        UserAI.IPAction aiAction = ai.decideIndianPokerAction(userCard);

        if (aiAction == UserAI.IPAction.FOLD) {
            setInfoText("AI가 FOLD 했습니다. 당신이 팟을 가져갑니다!");
            user.setMoney(user.getMoney() + pot);
            endRoundUIReveal(false);
        } else {
            pot += payCall(enemy, CALL_AMOUNT);
            revealAndSettle();
        }
    }

    private void onPlayerFold() {
        if (phase != Phase.BETTING) return;
        setInfoText("당신이 FOLD 했습니다. AI가 팟을 가져갑니다.");
        enemy.setMoney(enemy.getMoney() + pot);
        endRoundUIReveal(false);
    }

    private void revealAndSettle() {
        phase = Phase.REVEAL;

        // 🔹 결과 공개 시 — 상대 카드 앞면으로 변경
        setEnemyCardFaceUp(enemyCard);
        setUserCardFaceUp(userCard);

        int my = valueOf(userCard);
        int ai = valueOf(enemyCard);
        String msg;

        if (my > ai) {
            msg = String.format("당신 승! (%s > %s)", labelFor(userCard), labelFor(enemyCard));
            user.setMoney(user.getMoney() + pot);
        } else if (my < ai) {
            msg = String.format("AI 승! (%s < %s)", labelFor(userCard), labelFor(enemyCard));
            enemy.setMoney(enemy.getMoney() + pot);
        } else {
            msg = String.format("무승부! (%s = %s) 팟 분할", labelFor(userCard), labelFor(enemyCard));
            user.setMoney(user.getMoney() + pot / 2);
            enemy.setMoney(enemy.getMoney() + pot - (pot / 2));
        }

        setInfoText(msg);
        endRoundUIReveal(true);
    }

    private void endRoundUIReveal(boolean alreadyRevealed) {
        phase = Phase.SETTLE;
        if (!alreadyRevealed) {
            setUserCardFaceUp(userCard);
            setEnemyCardFaceUp(enemyCard);
        }

        updatePotText();
        lblMoney.setText(statusMoneyText());

        btnCall.setEnabled(false);
        btnFold.setEnabled(false);
        btnNext.setEnabled(true);
    }

    // ===== 유틸 =====
    private int payAnte(Object player, int amount) {
        if (player instanceof User) {
            User u = (User) player;
            u.setMoney(u.getMoney() - amount);
            return amount;
        } else {
            Enemy e = (Enemy) player;
            e.setMoney(e.getMoney() - amount);
            return amount;
        }
    }

    private int payCall(Object player, int amount) {
        return payAnte(player, amount);
    }

    private int valueOf(Card c) {
        int v = c.getCNum();
        if (v == 1) return 14;
        return v;
    }

    private String labelFor(Card c) {
        String face;
        int n = c.getCNum();
        switch (n) {
            case 1: face = "A"; break;
            case 11: face = "J"; break;
            case 12: face = "Q"; break;
            case 13: face = "K"; break;
            default: face = String.valueOf(n);
        }
        return face + "(" + c.getCShape() + ")";
    }

    private void setInfoText(String s) { lblInfo.setText(s); }
    private void updatePotText() { lblPot.setText("POT: " + pot); }

    // 🔹 카드 표시 함수들
    private void setEnemyCardFaceUp(Card c) { lblEnemyCard.setIcon(loadCardFrontIcon(c)); }
    private void setEnemyCardFaceDown() { lblEnemyCard.setIcon(loadBackIcon()); }
    private void setUserCardFaceUp(Card c) { lblUserCard.setIcon(loadCardFrontIcon(c)); }

    // ===== 이미지 로드 =====
    private Icon loadCardFrontIcon(Card c) {
        int shape = c.getCShape();
        int num = c.getCNum();
        if (num == 1) num = 14;

        String path = CARD_IMG_DIR + "Card" + num + shape + ".png";

        File f = new File(path);
        if (!f.exists()) {
            System.err.println("❌ 이미지 없음: " + path);
            return loadBackIcon();
        }

        ImageIcon icon = new ImageIcon(path);
        Image scaled = icon.getImage().getScaledInstance(240, 340, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private Icon loadBackIcon() {
        File f = new File(CARD_BACK_IMG);
        if (!f.exists()) {
            System.err.println("❌ 백이미지 없음: " + CARD_BACK_IMG);
            return null;
        }
        ImageIcon icon = new ImageIcon(CARD_BACK_IMG);
        Image scaled = icon.getImage().getScaledInstance(240, 340, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlayIndianPoker::new);
    }
}
