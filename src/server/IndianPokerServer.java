package server;

import java.io.*;
import java.net.*;
import normalclass.*;

public class IndianPokerServer {

    private static final int PORT = 50000;
    private static final int CHAT_PORT = 50001;
    private static final int ANTE = 10;

    public static void main(String[] args) {
        System.out.println("IndianPokerServer: waiting on port " + PORT);

        try (ServerSocket listener = new ServerSocket(PORT);
             ServerSocket chatListener = new ServerSocket(CHAT_PORT)) {

            Socket p1 = listener.accept();
            System.out.println("Player1 connected (game)");
            
            Socket p1Chat = chatListener.accept();
            System.out.println("Player1 connected (chat)");

            Socket p2 = listener.accept();
            System.out.println("Player2 connected (game)");
            
            Socket p2Chat = chatListener.accept();
            System.out.println("Player2 connected (chat)");

            new 
            GameRoom(p1, p2, p1Chat, p2Chat).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class GameRoom extends Thread {

        private Socket s1, s2;
        private Socket chatS1, chatS2;
        private BufferedReader in1, in2;
        private BufferedWriter out1, out2;
        private BufferedReader chatIn1, chatIn2;
        private BufferedWriter chatOut1, chatOut2;

        private Dealer dealer = new Dealer();
        private int p1Money = 200, p2Money = 200;
        private int pot;
        private volatile boolean running = true;

        public GameRoom(Socket s1, Socket s2, Socket chatS1, Socket chatS2) {
            this.s1 = s1;
            this.s2 = s2;
            this.chatS1 = chatS1;
            this.chatS2 = chatS2;
            dealer.shuffle();
        }

        @Override
        public void run() {
            try {
                in1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
                out1 = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
                in2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
                out2 = new BufferedWriter(new OutputStreamWriter(s2.getOutputStream()));

                chatIn1 = new BufferedReader(new InputStreamReader(chatS1.getInputStream()));
                chatOut1 = new BufferedWriter(new OutputStreamWriter(chatS1.getOutputStream()));
                chatIn2 = new BufferedReader(new InputStreamReader(chatS2.getInputStream()));
                chatOut2 = new BufferedWriter(new OutputStreamWriter(chatS2.getOutputStream()));
                
                startChatListeners();

                while (true) {
                    boolean continueGame = playRound();
                    if (!continueGame) {
                        System.out.println("Game finished.");
                        running = false;
                        break;
                    }
                    Thread.sleep(1500);
                }
                
                try { s1.close(); } catch (Exception ignored) {}
                try { s2.close(); } catch (Exception ignored) {}
                try { chatS1.close(); } catch (Exception ignored) {}
                try { chatS2.close(); } catch (Exception ignored) {}

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean playRound() throws IOException {
            
            pot = 0;
            int betP1 = ANTE;
            int betP2 = ANTE;

            Card c1 = dealer.dealOne();
            Card c2 = dealer.dealOne();

            p1Money -= ANTE;
            p2Money -= ANTE;
            pot = betP1 + betP2;

            send(out1, "ROUND " + p1Money + " " + p2Money + " " + pot + " " + c2.getCNum() + " " + c2.getCShape());
            send(out2, "ROUND " + p2Money + " " + p1Money + " " + pot + " " + c1.getCNum() + " " + c1.getCShape());

            boolean p1Folded = false;
            boolean p2Folded = false;
            int currentBet = ANTE;
            
            // === Player1 턴 ===
            send(out1, "YOUR_TURN");
            send(out2, "WAIT_TURN");
            
            String[] cmd1 = in1.readLine().split(" ", 2);
            String action1 = cmd1[0];
            
            if (action1.equals("FOLD")) {
                p1Folded = true;
            } else if (action1.equals("ALLIN")) {
                betP1 += p1Money;
                pot += p1Money;
                p1Money = 0;
                currentBet = betP1;
            } else if (action1.equals("RAISE")) {
                int raiseAmount = Integer.parseInt(cmd1[1]);
                if (p1Money >= raiseAmount) {
                    p1Money -= raiseAmount;
                    betP1 += raiseAmount;
                    currentBet = betP1;
                    pot = betP1 + betP2;
                }
            } else if (action1.equals("CALL")) {
                int diff = currentBet - betP1;
                if (p1Money >= diff) {
                    p1Money -= diff;
                    betP1 += diff;
                    pot = betP1 + betP2;
                }
            }
            
            send(out1, "POT_UPDATE " + pot + " " + p1Money + " " + p2Money + " " + currentBet);
            send(out2, "POT_UPDATE " + pot + " " + p2Money + " " + p1Money + " " + currentBet);
            
            if (p1Folded) {
                p2Money += pot;
                sendBothResult("LOSE", "WIN", c1, c2);
                return checkGameOver();
            }
            
            // === Player2 턴 ===
            send(out2, "YOUR_TURN");
            send(out1, "WAIT_TURN");
            
            String[] cmd2 = in2.readLine().split(" ", 2);
            String action2 = cmd2[0];
            
            if (action2.equals("FOLD")) {
                p2Folded = true;
            } else if (action2.equals("ALLIN")) {
                betP2 += p2Money;
                pot += p2Money;
                p2Money = 0;
                currentBet = Math.max(currentBet, betP2);
            } else if (action2.equals("RAISE")) {
                int raiseAmount = Integer.parseInt(cmd2[1]);
                if (p2Money >= raiseAmount) {
                    p2Money -= raiseAmount;
                    betP2 += raiseAmount;
                    currentBet = Math.max(currentBet, betP2);
                    pot = betP1 + betP2;
                }
            } else if (action2.equals("CALL")) {
                int diff = currentBet - betP2;
                if (p2Money >= diff) {
                    p2Money -= diff;
                    betP2 += diff;
                    pot = betP1 + betP2;
                }
            }
            
            send(out1, "POT_UPDATE " + pot + " " + p1Money + " " + p2Money + " " + currentBet);
            send(out2, "POT_UPDATE " + pot + " " + p2Money + " " + p1Money + " " + currentBet);
            
            if (p2Folded) {
                p1Money += pot;
                sendBothResult("WIN", "LOSE", c1, c2);
                return checkGameOver();
            }
            
            // Player2가 베팅 완료 → 즉시 결과 판정!
            // (Player1 추가 턴 없음)
            
            // 승패 판정
            int v1 = (c1.getCNum() == 1 ? 14 : c1.getCNum());
            int v2 = (c2.getCNum() == 1 ? 14 : c2.getCNum());
            
            if (v1 > v2) {
                p1Money += pot;
                sendBothResult("WIN", "LOSE", c1, c2);
            } else if (v1 < v2) {
                p2Money += pot;
                sendBothResult("LOSE", "WIN", c1, c2);
            } else {
                p1Money += pot / 2;
                p2Money += pot - (pot / 2);
                sendBothResult("DRAW", "DRAW", c1, c2);
            }
            
            try { Thread.sleep(1200); } catch (InterruptedException ignored) {}

            if (dealer.remaining() < 10) {
                dealer.reset();
                dealer.shuffle();
            }
            
            return checkGameOver();
        }
        
        private boolean checkGameOver() throws IOException {
            if (p1Money <= 0 || p2Money <= 0) {
                String winner = (p1Money > p2Money) ? "Player1" : "Player2";
                send(out1, "GAMEOVER " + winner);
                send(out2, "GAMEOVER " + winner);
                return false;
            }
            return true;
        }

        private void sendBothResult(String r1, String r2, Card c1, Card c2) throws IOException {
            send(out1, "RESULT " + r1 + " "
                    + c1.getCNum() + " " + c1.getCShape() + " "
                    + c2.getCNum() + " " + c2.getCShape() + " "
                    + p1Money + " " + p2Money);

            send(out2, "RESULT " + r2 + " "
                    + c2.getCNum() + " " + c2.getCShape() + " "
                    + c1.getCNum() + " " + c1.getCShape() + " "
                    + p2Money + " " + p1Money);
        }

        private static void send(BufferedWriter out, String msg) throws IOException {
            out.write(msg + "\n");
            out.flush();
        }
        
        private void startChatListeners() {
            new Thread(() -> {
                try {
                    String line;
                    while (running && (line = chatIn1.readLine()) != null) {
                        send(chatOut2, line);
                        System.out.println("[P1→P2] " + line);
                    }
                } catch (Exception e) {
                    if (running) e.printStackTrace();
                }
            }).start();
            
            new Thread(() -> {
                try {
                    String line;
                    while (running && (line = chatIn2.readLine()) != null) {
                        send(chatOut1, line);
                        System.out.println("[P2→P1] " + line);
                    }
                } catch (Exception e) {
                    if (running) e.printStackTrace();
                }
            }).start();
        }
    }
}
