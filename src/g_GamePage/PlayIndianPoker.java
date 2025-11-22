package g_GamePage;

import normalclass.Card;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * 네트워크 기반 인디언 포커 클라이언트
 * 
 * 역할:
 *   - 서버에 접속하여 ROUND/RESULT 메시지를 수신
 *   - CALL / FOLD 버튼 입력을 서버로 전송
 *   - 서버 메시지에 따라 카드 이미지 및 UI 갱신
 * 
 * 게임 로직은 모두 서버가 처리하고
 * 클라이언트는 UI 표시 + 입력만 담당한다.
 */
public class PlayIndianPoker extends JFrame {

    // ===============================
    //  UI 컴포넌트
    // ===============================
    private JLabel lblEnemyCard, lblUserCard, lblPot, lblMoney, lblInfo;
    private JButton btnCall, btnFold;

    // ===============================
    //  네트워크 통신 관련
    // ===============================
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;

    private final String SERVER_IP = "localhost"; // 로컬 테스트
    private final int SERVER_PORT = 50000;

    // ===============================
    //  서버가 보내주는 게임 상태
    // ===============================
    private Card myCard;
    private Card enemyCard;
    private int myMoney;
    private int enemyMoney;
    private int pot;

    // ===============================
    //  카드 이미지 경로
    // ===============================
    private final String CARD_IMG_DIR = System.getProperty("user.dir") + "/src/plus_Card/";
    private final String CARD_BACK_IMG = System.getProperty("user.dir") + "/src/plus_Card/CardBackImg.png";

    /**
     * 클라이언트(UI) 생성자
     * UI 구성 → 서버 접속 → 메시지 수신 스레드 시작
     */
    public PlayIndianPoker() {
        setTitle("Indian Poker Online");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();          // UI 구성
        connectToServer(); // 서버 연결
        startReceiver();   // 서버 메시지 수신 스레드

        setVisible(true);
    }

    // ===============================
    //  UI 구성
    // ===============================
    private void initUI() {

        // 상단: 정보 텍스트 + 팟 표시
        JPanel top = new JPanel(new BorderLayout());
        lblInfo = new JLabel("상대 플레이어를 기다리는 중...", SwingConstants.LEFT);
        lblInfo.setFont(new Font("Dialog", Font.BOLD, 16));

        lblPot = new JLabel("POT: 0", SwingConstants.RIGHT);
        lblPot.setFont(new Font("Dialog", Font.BOLD, 16));

        top.add(lblInfo, BorderLayout.WEST);
        top.add(lblPot, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // 중앙: 내 카드 / 상대 카드 표시
        JPanel center = new JPanel(new GridLayout(1, 2, 20, 0));
        center.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        lblUserCard = createCardLabel();
        lblEnemyCard = createCardLabel();

        center.add(titled(lblUserCard, "내 카드"));
        center.add(titled(lblEnemyCard, "상대 카드"));
        add(center, BorderLayout.CENTER);

        // 하단: 돈 표시 + CALL/FOLD 버튼
        JPanel bottom = new JPanel(new BorderLayout());
        lblMoney = new JLabel("나: 200원 | 상대: 200원", SwingConstants.CENTER);
        lblMoney.setFont(new Font("Dialog", Font.PLAIN, 14));

        JPanel btnPanel = new JPanel();
        btnCall = new JButton("CALL");
        btnFold = new JButton("FOLD");

        // 버튼 클릭 시 서버에 CALL/FOLD 전송
        btnCall.addActionListener(e -> sendToServer("CALL"));
        btnFold.addActionListener(e -> sendToServer("FOLD"));

        btnPanel.add(btnCall);
        btnPanel.add(btnFold);

        bottom.add(lblMoney, BorderLayout.NORTH);
        bottom.add(btnPanel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    // 카드 표시용 기본 JLabel 생성
    private JLabel createCardLabel() {
        JLabel l = new JLabel("", SwingConstants.CENTER);
        l.setPreferredSize(new Dimension(260, 360));
        l.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return l;
    }

    // 제목 + 컴포넌트 세트로 묶기
    private JPanel titled(JComponent comp, String title) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Dialog", Font.BOLD, 14));
        p.add(t, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    // ===============================
    //  서버 연결
    // ===============================
    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);

            // 소켓 스트림 준비
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            setInfo("상대 플레이어 접속 대기중...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===============================
    //  서버 메시지 수신 스레드
    // ===============================
    private void startReceiver() {
        new Thread(() -> {
            try {
                String msg;

                // 서버가 보낼 때까지 계속 수신
                while ((msg = in.readLine()) != null) {
                    handleMessage(msg); // 메시지 분류 및 처리
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ===============================
    //  서버 메시지 처리
    // ===============================
    private void handleMessage(String msg) {
        String[] t = msg.split(" ");

        switch (t[0]) {

            // ------------------------------------
            //  ROUND: 새 라운드 시작
            // ------------------------------------
            case "ROUND":
                myMoney    = Integer.parseInt(t[1]);
                enemyMoney = Integer.parseInt(t[2]);
                pot        = Integer.parseInt(t[3]);

                int visibleNum   = Integer.parseInt(t[4]);
                int visibleShape = Integer.parseInt(t[5]);

                enemyCard = new Card(visibleNum, visibleShape);

                SwingUtilities.invokeLater(() -> {
                    // 내 카드는 뒷면
                    lblUserCard.setIcon(loadBackIcon());

                    // 상대 카드는 앞면(보이는 카드)
                    lblEnemyCard.setIcon(loadCardFrontIcon(enemyCard));

                    // UI에 상황 갱신
                    lblMoney.setText("나: " + myMoney + "원 | 상대: " + enemyMoney + "원");
                    lblPot.setText("POT: " + pot);
                    setInfo("CALL 또는 FOLD를 선택하세요.");

                    // 새로운 라운드 → 버튼 다시 활성화
                    btnCall.setEnabled(true);
                    btnFold.setEnabled(true);
                });
                break;

            // ------------------------------------
            //  RESULT: 승패 결과 공개
            // ------------------------------------
            case "RESULT":

                String result = t[1];

                int myNum = Integer.parseInt(t[2]);
                int myShp = Integer.parseInt(t[3]);
                int enNum = Integer.parseInt(t[4]);
                int enShp = Integer.parseInt(t[5]);

                int newMyMoney    = Integer.parseInt(t[6]);
                int newEnemyMoney = Integer.parseInt(t[7]);

                myCard    = new Card(myNum, myShp);
                enemyCard = new Card(enNum, enShp);

                SwingUtilities.invokeLater(() -> {
                    // 결과 공개 → 내 카드도 앞면
                    lblUserCard.setIcon(loadCardFrontIcon(myCard));
                    lblEnemyCard.setIcon(loadCardFrontIcon(enemyCard));

                    lblMoney.setText("나: " + newMyMoney + "원 | 상대: " + newEnemyMoney + "원");
                    setInfo("결과: " + result + " (다음 라운드를 기다리는 중...)");
                });
                break;
        }
    }

    // ===============================
    //  CALL/FOLD 서버로 전송
    // ===============================
    private void sendToServer(String s) {
        try {
            out.write(s + "\n");
            out.flush();

            // 입력 완료 → 버튼 비활성화 (다음 라운드에서 다시 활성화됨)
            btnCall.setEnabled(false);
            btnFold.setEnabled(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===============================
    //  카드 이미지 로딩
    // ===============================
    private Icon loadCardFrontIcon(Card c) {
        int num = c.getCNum();
        if (num == 1) num = 14; // A는 파일명 규칙상 14로 저장됨

        String path = CARD_IMG_DIR + "Card" + num + c.getCShape() + ".png";

        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(240, 340, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private Icon loadBackIcon() {
        ImageIcon icon = new ImageIcon(CARD_BACK_IMG);
        Image img = icon.getImage().getScaledInstance(240, 340, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // ===============================
    //  UI Text Set
    // ===============================
    private void setInfo(String s) {
        lblInfo.setText(s);
    }

    // ===============================
    //  실행 시작
    // ===============================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlayIndianPoker::new);
    }
}
