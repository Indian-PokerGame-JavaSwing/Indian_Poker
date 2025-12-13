package server;

public class NicknameManager {

    public String p1Name = "Player1";
    public String p2Name = "Player2";

    public void receiveNicknames(PlayerConnection p1, PlayerConnection p2) throws Exception {

        boolean p1Received = false;
        boolean p2Received = false;

        System.out.println("닉네임 수신 대기 중...");

        while (!(p1Received && p2Received)) {

            // p1 닉네임 수신
            if (!p1Received) {
                String msg = p1.take();   // ✅ 큐에서 받기

                if (msg != null && msg.startsWith("NICK ")) {
                    p1Name = msg.substring(5).trim();
                    p1Received = true;
                    System.out.println("Player1 닉네임: " + p1Name);
                }
                // CHAT 등 다른 메시지는 그냥 무시
            }

            // p2 닉네임 수신
            if (!p2Received) {
                String msg = p2.take();   // ✅ 큐에서 받기

                if (msg != null && msg.startsWith("NICK ")) {
                    p2Name = msg.substring(5).trim();
                    p2Received = true;
                    System.out.println("Player2 닉네임: " + p2Name);
                }
            }
        }

        // 상대 닉네임 전송
        p1.send("ENEMYNAME " + p2Name);
        p2.send("ENEMYNAME " + p1Name);

        System.out.println("닉네임 교환 완료: " + p1Name + " vs " + p2Name);
    }
}
