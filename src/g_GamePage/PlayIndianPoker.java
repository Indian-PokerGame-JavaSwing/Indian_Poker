package g_GamePage;

import normalclass.Card;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class PlayIndianPoker extends JFrame {

    // ===============================
    //  UI 컴포넌트
    // ===============================
    private JLabel lblEnemyCard, lblUserCard, lblPot, lblMoney, lblInfo;
    private JButton btnCall, btnRaise, btnAllIn, btnFold;
    private JTextField txtRaise;
    private ChatPanel chatPanel;

    // ===============================
    //  네트워크 통신 관련
    // ===============================
    private Socket socket;
    private Socket chatSocket;  // 채팅 전용 소켓
    private BufferedWriter out;
    private BufferedReader in;
    private BufferedWriter chatOut;
    private BufferedReader chatIn;

    private final String SERVER_IP = "localhost"; // 로컬 테스트
    private final int SERVER_PORT = 50000;
    private final int CHAT_PORT = 50001;  // 채팅 전용 포트

    // ===============================
    //  서버가 보내주는 게임 상태
    // ===============================
    private Card myCard;
    private Card enemyCard;
    private int myMoney;
    private int enemyMoney;
    private int pot;
    private int currentBet = 0; // 현재 최소 베팅 금액

    // ===============================
    //  카드 이미지 경로
    // ===============================
    private final String CARD_IMG_DIR = System.getProperty("user.dir") + "/src/plus_Card/";
    private final String CARD_BACK_IMG = System.getProperty("user.dir") + "/src/plus_Card/CardBackImg.png";

    public PlayIndianPoker() {
        setTitle("Indian Poker Online");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);  //720으로 증가
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
        // 메인 패널 (포커 테이블 녹색)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(35, 87, 60));
        
        // 상단: 정보 표시 영역
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(20, 40, 30));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        
        lblInfo = new JLabel("🎰 상대 플레이어를 기다리는 중...", SwingConstants.CENTER);
        lblInfo.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        lblInfo.setForeground(new Color(255, 215, 0));
        
        topPanel.add(lblInfo, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 중앙: POT + 카드 영역
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(35, 87, 60));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // POT 표시
        JPanel potPanel = new JPanel();
        potPanel.setBackground(new Color(35, 87, 60));
        potPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        lblPot = new JLabel("💰 POT: 0원");
        lblPot.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        lblPot.setForeground(new Color(255, 215, 0));
        lblPot.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 3),
            BorderFactory.createEmptyBorder(18, 40, 18, 40)
        ));
        lblPot.setOpaque(true);
        lblPot.setBackground(new Color(20, 40, 30));
        potPanel.add(lblPot);
        
        centerPanel.add(potPanel, BorderLayout.NORTH);
        
        // 카드 영역 (2장 가로 배치)
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 0));
        cardsPanel.setBackground(new Color(35, 87, 60));

        lblUserCard = createCardLabel();
        lblEnemyCard = createCardLabel();

        cardsPanel.add(titled(lblUserCard, "🃏 내 카드"));
        cardsPanel.add(titled(lblEnemyCard, "🎴 상대 카드"));
        
        centerPanel.add(cardsPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 하단: 잔액 표시 + 버튼
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(20, 40, 30));
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));
        
        lblMoney = new JLabel("💵 나: 200원 | 상대: 200원", SwingConstants.CENTER);
        lblMoney.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        lblMoney.setForeground(new Color(240, 255, 240));
        lblMoney.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        btnPanel.setBackground(new Color(20, 40, 30));
        
        // 스타일된 버튼들
        btnCall = createStyledButton("CALL", new Color(46, 125, 50));
        btnRaise = createStyledButton("RAISE", new Color(230, 126, 34));
        btnAllIn = createStyledButton("ALL-IN", new Color(192, 57, 43));
        btnFold = createStyledButton("FOLD", new Color(120, 120, 120));
        
        // Raise 금액 입력창
        txtRaise = new JTextField("20", 5);
        txtRaise.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        txtRaise.setHorizontalAlignment(JTextField.CENTER);
        txtRaise.setBackground(new Color(255, 255, 255));
        txtRaise.setForeground(new Color(30, 30, 30));
        txtRaise.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        // 버튼 클릭 이벤트
        btnCall.addActionListener(e -> sendToServer("CALL"));
        btnRaise.addActionListener(e -> {
        	String text = txtRaise.getText().trim();
        	
        	if (!text.matches("\\d+")) {
        		JOptionPane.showMessageDialog(this, "숫자만 입력 가능합니다!", "입력 오류", JOptionPane.WARNING_MESSAGE);
        		return;
        	}
        	int raiseAmount = Integer.parseInt(text);
        	
        	// 최소 베팅 금액 체크
        	if (raiseAmount < currentBet) {
        		JOptionPane.showMessageDialog(this,
        				"최소 " + currentBet + "원 이상 베팅해야 합니다!",
        				"베팅 불가",
        				JOptionPane.WARNING_MESSAGE);
        		return;
        	}
        	
        	if (raiseAmount > myMoney) {
        		JOptionPane.showMessageDialog(this,
        				"보유 금액("+myMoney+"원) 초과 금액은 베팅할 수 없습니다!",
        				"베팅 불가",
        				JOptionPane.WARNING_MESSAGE);
        		return;
        	}
        	sendToServer("RAISE "+raiseAmount);
        });
        btnAllIn.addActionListener(e -> sendToServer("ALLIN"));
        btnFold.addActionListener(e -> sendToServer("FOLD"));

        btnPanel.add(btnCall);
        btnPanel.add(btnRaise);
        btnPanel.add(txtRaise);
        btnPanel.add(btnAllIn);
        btnPanel.add(btnFold);

        bottomPanel.add(lblMoney, BorderLayout.NORTH);
        bottomPanel.add(btnPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // 메인 패널을 중앙에 배치
        add(mainPanel, BorderLayout.CENTER);
        
        // 채팅 패널 추가 (오른쪽)
        chatPanel = new ChatPanel();
        add(chatPanel, BorderLayout.EAST);
    }
    
    // 스타일이 적용된 버튼 생성
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(105, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 호버 효과
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(bgColor.brighter());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }

    // 카드 표시용 기본 JLabel 생성
    private JLabel createCardLabel() {
        JLabel l = new JLabel("", SwingConstants.CENTER);
        l.setPreferredSize(new Dimension(135, 230));  // 가로 135, 세로 230
        l.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 3),
            BorderFactory.createLineBorder(Color.WHITE, 2)
        ));
        l.setOpaque(true);
        l.setBackground(Color.WHITE);
        return l;
    }

    // 제목 + 컴포넌트 세트로 묶기
    private JPanel titled(JComponent comp, String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(35, 87, 60));
        
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        t.setForeground(Color.WHITE);
        t.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        
        p.add(t, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    // ===============================
    //  서버 연결
    // ===============================
    private void connectToServer() {
        try {
            // 게임 소켓 연결
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 채팅 소켓 연결
            chatSocket = new Socket(SERVER_IP, CHAT_PORT);
            chatOut = new BufferedWriter(new OutputStreamWriter(chatSocket.getOutputStream()));
            chatIn  = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));

            setInfo("상대 플레이어 접속 대기중...");
            
            // 채팅 패널에 채팅 전용 스트림 연결
            chatPanel.setChatOutputStream(chatOut);
            chatPanel.appendSystemMessage("서버 연결 완료!");
            
            // 채팅 수신 스레드 시작
            startChatReceiver();

        } catch (Exception e) {
            e.printStackTrace();
            chatPanel.appendSystemMessage("서버 연결 실패");
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
                
                // 새 라운드 시작 - currentBet 초기화!
                currentBet = 10; // 앤티 금액

                int visibleNum   = Integer.parseInt(t[4]);
                int visibleShape = Integer.parseInt(t[5]);

                enemyCard = new Card(visibleNum, visibleShape);

                SwingUtilities.invokeLater(() -> {
                    // 내 카드는 뒷면
                    lblUserCard.setIcon(loadBackIcon());

                    // 상대 카드는 앞면(보이는 카드)
                    lblEnemyCard.setIcon(loadCardFrontIcon(enemyCard));

                    // UI에 상황 갱신
                    lblMoney.setText("💵 나: " + myMoney + "원 | 상대: " + enemyMoney + "원");
                    lblPot.setText("💰 POT: " + pot + "원");
                    setInfo("🎲 베팅 순서를 기다리는 중...");
                    
                    // 입력창도 초기화
                    txtRaise.setText("10");

                    // 버튼 비활성화 (YOUR_TURN에서 활성화)
                    btnCall.setEnabled(false);
                    btnRaise.setEnabled(false);
                    btnAllIn.setEnabled(false);
                    btnFold.setEnabled(false);
                });
                break;

            // ------------------------------------
            //  YOUR_TURN: 내 차례
            // ------------------------------------
            case "YOUR_TURN":
                SwingUtilities.invokeLater(() -> {
                    setInfo("🎲 당신의 차례입니다! CALL / RAISE / ALL-IN / FOLD 선택하세요!");
                    btnCall.setEnabled(true);
                    btnRaise.setEnabled(true);
                    btnAllIn.setEnabled(true);
                    btnFold.setEnabled(true);
                });
                break;

            // ------------------------------------
            //  WAIT_TURN: 상대방 차례
            // ------------------------------------
            case "WAIT_TURN":
                SwingUtilities.invokeLater(() -> {
                    setInfo("⏳ 상대방이 베팅 중...");
                    btnCall.setEnabled(false);
                    btnRaise.setEnabled(false);
                    btnAllIn.setEnabled(false);
                    btnFold.setEnabled(false);
                });
                break;

            // ------------------------------------
            //  POT_UPDATE: POT 및 잔액 업데이트
            // ------------------------------------
            case "POT_UPDATE":
                pot = Integer.parseInt(t[1]);
                myMoney = Integer.parseInt(t[2]);
                enemyMoney = Integer.parseInt(t[3]);
                currentBet = Integer.parseInt(t[4]); // 현재 최소 베팅 금액
                
                SwingUtilities.invokeLater(() -> {
                    lblPot.setText("💰 POT: " + pot + "원");
                    lblMoney.setText("💵 나: " + myMoney + "원 | 상대: " + enemyMoney + "원");
                    // txtRaise에 최소 베팅 금액 표시
                    txtRaise.setText(String.valueOf(currentBet));
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

                    lblMoney.setText("💵 나: " + newMyMoney + "원 | 상대: " + newEnemyMoney + "원");
                    
                    // 결과에 따라 이모지 추가
                    String resultIcon;
                    if (result.contains("WIN")) {
                        resultIcon = "🎉 승리!";
                    } else if (result.contains("LOSE")) {
                        resultIcon = "😢 패배";
                    } else {
                        resultIcon = "🤝 무승부";
                    }
                    
                    setInfo(resultIcon + " " + result + " (다음 라운드 대기중...)");
                });
                break;

                // ------------------------------------
                //  GAMEOVER : 최종 승리자
                // ------------------------------------
            case "GAMEOVER":
                String winner = t[1];

                SwingUtilities.invokeLater(() -> {
                    String message;
                    if (winner.equals("YOU")) {
                        message = "🏆 축하합니다! 최종 승리! 🏆";
                    } else {
                        message = "😢 게임 종료 - " + winner + " 승리";
                    }
                    
                    lblInfo.setText(message);

                    // 💥 최종 카드도 앞면으로 표시
                    lblUserCard.setIcon(loadCardFrontIcon(myCard));
                    lblEnemyCard.setIcon(loadCardFrontIcon(enemyCard));

                    // 모든 버튼 비활성화
                    btnCall.setEnabled(false);
                    btnRaise.setEnabled(false);
                    btnAllIn.setEnabled(false);
                    btnFold.setEnabled(false);

                    // 결과 다이얼로그
                    JOptionPane.showMessageDialog(null, message, 
                        "게임 종료", JOptionPane.INFORMATION_MESSAGE);
                    
                    // 게임 종료
                    try {
                        socket.close();
                    } catch (IOException ignored) {}
                    System.exit(0);
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
        // 이미지를 박스보다 살짝 크게 해서 여백 잘라내기
        Image img = icon.getImage().getScaledInstance(140, 240, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private Icon loadBackIcon() {
        ImageIcon icon = new ImageIcon(CARD_BACK_IMG);
        // 이미지를 박스보다 살짝 크게 해서 여백 잘라내기
        Image img = icon.getImage().getScaledInstance(140, 240, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // ===============================
    //  UI Text Set
    // ===============================
    private void setInfo(String s) {
        lblInfo.setText(s);
    }
    
    // ===============================
    //  채팅 수신 스레드
    // ===============================
    private void startChatReceiver() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = chatIn.readLine()) != null) {
                    chatPanel.appendOpponentMessage(msg);
                }
            } catch (Exception e) {
                // 연결 종료 시 정상
            }
        }).start();
    }

    // ===============================
    //  실행 시작
    // ===============================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlayIndianPoker::new);
    }
}
