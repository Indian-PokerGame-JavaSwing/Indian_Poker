package server;

import normalclass.Card;
import java.net.Socket;

public class GameRoom extends Thread {

    private PlayerConnection p1, p2;

    private NicknameManager nickManager = new NicknameManager();
    private BettingManager betManager;
    private RoundManager roundManager = new RoundManager();
    private ResultSender resultSender = new ResultSender();

    private final int ANTE = 10;

    public GameRoom(Socket s1, Socket s2) {
        try {
            p1 = new PlayerConnection(s1);
            p2 = new PlayerConnection(s2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            // 1️⃣ 닉네임 교환
            nickManager.receiveNicknames(p1, p2);

            MoneyState money = new MoneyState();

            // 2️⃣ 라운드 반복
            while (true) {

                betManager = new BettingManager(ANTE);
                int[] pot = {0};

                // 카드 배분
                Card[] cards = roundManager.dealCards();
                Card c1 = cards[0];
                Card c2 = cards[1];

                // 앤티 차감
                money.p1 -= ANTE;
                money.p2 -= ANTE;
                pot[0] = ANTE * 2;

                // 라운드 시작 정보 전송
                roundManager.sendRoundStart(p1, p2, money, pot[0], c1, c2);

                // ============================
                // 🔥 입력 처리 (CHAT 완전 포함)
                //    - take()로 큐에서 꺼내기
                // ============================
                String[] cmd1 = readCommandSkippingChat(p1);
                String[] cmd2 = readCommandSkippingChat(p2);

                // 베팅 처리
                betManager.applyActionP1(cmd1, money, pot);
                betManager.applyActionP2(cmd2, money, pot);

                // 폴드 승자 처리
                if (money.foldWinner != 0) {
                    if (money.foldWinner == 1)
                        resultSender.sendBoth(p1, p2, "WIN", "LOSE", c1, c2, money);
                    else
                        resultSender.sendBoth(p1, p2, "LOSE", "WIN", c1, c2, money);
                } else {
                    // 카드 비교
                    int v1 = (c1.getCNum() == 1 ? 14 : c1.getCNum());
                    int v2 = (c2.getCNum() == 1 ? 14 : c2.getCNum());

                    if (v1 > v2) {
                        money.p1 += pot[0];
                        resultSender.sendBoth(p1, p2, "WIN", "LOSE", c1, c2, money);
                    } else if (v2 > v1) {
                        money.p2 += pot[0];
                        resultSender.sendBoth(p1, p2, "LOSE", "WIN", c1, c2, money);
                    } else {
                        money.p1 += pot[0] / 2;
                        money.p2 += pot[0] - pot[0] / 2;
                        resultSender.sendBoth(p1, p2, "DRAW", "DRAW", c1, c2, money);
                    }
                }

                // 게임 종료 조건
                if (money.p1 <= 0 || money.p2 <= 0) {
                    String winner = (money.p1 > money.p2)
                            ? nickManager.p1Name
                            : nickManager.p2Name;
                    resultSender.sendGameOver(p1, p2, winner);
                    break;
                }

                Thread.sleep(1500);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // 🔹 큐에서 명령을 꺼내되 CHAT이면 즉시 중계하고 계속 대기
    //    - 클라가 보내는 포맷: "CHAT 닉네임 메시지..."
    // =====================================================
    private String[] readCommandSkippingChat(PlayerConnection player) throws Exception {

        while (true) {
            // ✅ 이제 read()가 아니라 take()로 "큐"에서 받음
            String raw = player.take();  // BlockingQueue.take()

            if (raw == null) {
                // 이론상 take()는 null 거의 안 오지만, 안전 처리
                return new String[]{"FOLD"};
            }

            // "CHAT ..." 처리
            if (raw.startsWith("CHAT ")) {
                // CHAT <sender> <message...>
                String[] parts = raw.split(" ", 3);
                String sender = (parts.length >= 2) ? parts[1] : "Unknown";
                String msg    = (parts.length >= 3) ? parts[2] : "";

                broadcastChat(sender, msg);
                continue;
            }

            // 그 외는 게임 명령: CALL / FOLD / RAISE n / ALLIN
            return raw.split(" ");
        }
    }

    // =====================================================
    // 🔥 채팅 브로드캐스트
    // =====================================================
    private void broadcastChat(String sender, String message) {
        try {
            String line = "CHAT " + sender + " " + message;
            p1.send(line);
            p2.send(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
