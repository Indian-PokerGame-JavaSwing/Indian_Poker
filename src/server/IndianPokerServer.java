package server;

import java.io.*;
import java.net.*;
import normalclass.*;

/**
 * IndianPokerServer
 * -----------------------------------------
 * 역할:
 *   - 두 명의 클라이언트 접속(Player1, Player2)을 기다림
 *   - 두 플레이어가 연결되면 GameRoom 스레드를 생성하여 게임 진행
 *   - GameRoom에서는 라운드를 무한 반복하며
 *     카드 배분, 베팅 처리, 승패 판정, 돈 분배 등 모든 게임 로직을 처리한다.
 *
 * 클라이언트는 단순히 UI와 CALL/FOLD 입력만 담당하고,
 * 게임 규칙은 전부 서버에서 관리한다.
 */
public class IndianPokerServer {

    private static final int PORT = 50000;
    private static final int ANTE = 10;

    public static void main(String[] args) {
        System.out.println("IndianPokerServer: waiting on port " + PORT);

        try (ServerSocket listener = new ServerSocket(PORT)) {

            // 1) 첫 번째 플레이어 접속 대기
            Socket p1 = listener.accept();
            System.out.println("Player1 connected");

            // 2) 두 번째 플레이어 접속 대기
            Socket p2 = listener.accept();
            System.out.println("Player2 connected");

            // 3) 플레이어 두 명이 모두 연결되면 게임방 스레드 시작
            new GameRoom(p1, p2).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * GameRoom
     * -----------------------------------------
     * - Player1, Player2 각각의 소켓과 I/O 스트림을 관리
     * - Dealer를 이용해 카드 셔플/배분
     * - 라운드를 무한 반복
     * - ROUND 메시지 전송 -> CALL/FOLD 수신 -> 승패 계산 -> RESULT 전송
     */
    static class GameRoom extends Thread {

        private Socket s1, s2;
        private BufferedReader in1, in2;
        private BufferedWriter out1, out2;

        private Dealer dealer = new Dealer();

        // 플레이어 돈 (서버는 각 플레이어 돈을 반드시 따로 보관)
        private int p1Money = 200, p2Money = 200;

        private int pot; // 현재 라운드 팟 금액

        public GameRoom(Socket s1, Socket s2) {
            this.s1 = s1;
            this.s2 = s2;
            dealer.shuffle();
        }

        @Override
        public void run() {
            try {
                // 소켓 스트림 준비
                in1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
                out1 = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));

                in2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
                out2 = new BufferedWriter(new OutputStreamWriter(s2.getOutputStream()));

                // 🔥 라운드 무한 반복 실행
                while (true) {
                    playRound();      // 라운드 진행
                    Thread.sleep(1500); // UI 안정화·전환 시간 확보
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 1 라운드 진행
         * ----------------------------
         * 순서:
         *   1) 카드 두 장 배분
         *   2) 각 플레이어 앤티(ANTE) 차감 → pot 증가
         *   3) ROUND 메시지 전송 (각자 상대 카드만 보냄)
         *   4) CALL/FOLD 입력 대기
         *   5) 승패 계산 후 RESULT 메시지 전송
         */
        private void playRound() throws IOException {

            pot = 0;

            // 1) 카드 배분
            Card c1 = dealer.dealOne(); // Player1 카드
            Card c2 = dealer.dealOne(); // Player2 카드

            // 2) 앤티 차감
            p1Money -= ANTE;
            p2Money -= ANTE;
            pot += ANTE * 2;

            /**
             * ROUND 메시지 규칙
             *   ROUND myMoney enemyMoney pot visibleCardNum visibleCardShape
             *
             * Player1 → Player2 카드가 보이도록 전송
             * Player2 → Player1 카드가 보이도록 전송
             */
            send(out1, "ROUND " + p1Money + " " + p2Money + " " + pot + " " + c2.getCNum() + " " + c2.getCShape());
            send(out2, "ROUND " + p2Money + " " + p1Money + " " + pot + " " + c1.getCNum() + " " + c1.getCShape());

            // 3) CALL/FOLD 입력 받기 (둘 다 blocking read)
            String a1 = in1.readLine(); // CALL 또는 FOLD
            String a2 = in2.readLine();

            // 카드 값 계산 (A = 14 처리)
            int v1 = (c1.getCNum() == 1 ? 14 : c1.getCNum());
            int v2 = (c2.getCNum() == 1 ? 14 : c2.getCNum());

            /**
             * 승패 계산 규칙:
             *   - 한쪽이 FOLD하면 다른 쪽이 무조건 승리
             *   - 둘 다 CALL이면 카드 숫자 비교
             *   - 같으면 pot을 반씩 나눔
             */

            if (a1.equals("FOLD") && a2.equals("CALL")) {
                // Player1이 FOLD → Player2가 pot 가져감
                p2Money += pot;
                sendBothResult("LOSE", "WIN", c1, c2);

            } else if (a1.equals("CALL") && a2.equals("FOLD")) {
                // Player2가 FOLD → Player1이 pot 가져감
                p1Money += pot;
                sendBothResult("WIN", "LOSE", c1, c2);

            } else {
                // 둘 다 CALL한 경우 숫자 비교
                if (v1 > v2) {
                    p1Money += pot;
                    sendBothResult("WIN", "LOSE", c1, c2);

                } else if (v1 < v2) {
                    p2Money += pot;
                    sendBothResult("LOSE", "WIN", c1, c2);

                } else {
                    // 무승부 → pot 분할
                    p1Money += pot / 2;
                    p2Money += pot - (pot / 2);
                    sendBothResult("DRAW", "DRAW", c1, c2);
                }
            }

            // 카드가 거의 없으면 새 덱 생성
            if (dealer.remaining() < 10) {
                dealer.reset();
                dealer.shuffle();
            }
        }

        /**
         * 두 플레이어에게 각각 결과 메시지 전송
         * ---------------------------------------
         * RESULT 메시지 규칙:
         *
         *   RESULT <result> <myNum> <myShape> <enemyNum> <enemyShape> <myMoney> <enemyMoney>
         *
         * 클라이언트는 이 메시지를 받아
         *   - 자신의 카드
         *   - 상대 카드
         *   - 승/패/무승부
         *   - 최신 돈
         * 을 UI에 표시함.
         */
        private void sendBothResult(String r1, String r2, Card c1, Card c2) throws IOException {

            // Player1 기준 결과
            send(out1, "RESULT " + r1 + " "
                    + c1.getCNum() + " " + c1.getCShape() + " "
                    + c2.getCNum() + " " + c2.getCShape() + " "
                    + p1Money + " " + p2Money);

            // Player2 기준 결과 (카드 순서가 반대로 전달됨)
            send(out2, "RESULT " + r2 + " "
                    + c2.getCNum() + " " + c2.getCShape() + " "
                    + c1.getCNum() + " " + c1.getCShape() + " "
                    + p2Money + " " + p1Money);
        }

        /**
         * 클라이언트에게 메시지 전송
         * ----------------------------
         * \n 붙여서 out.flush() 해야 클라이언트가 readLine()에서 받음.
         */
        private void send(BufferedWriter out, String msg) throws IOException {
            out.write(msg + "\n");
            out.flush();
        }
    }
}
