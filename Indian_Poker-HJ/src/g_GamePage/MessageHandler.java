package g_GamePage;

import javax.swing.*;

import normalclass.Card;

public class MessageHandler {

    private GameUI ui;
    private PlayIndianPoker main;

    private String enemyNickname = "상대";

    public MessageHandler(GameUI ui, PlayIndianPoker main) {
        this.ui = ui;
        this.main = main;
    }

    public void handle(String msg) {
        String[] t = msg.split(" ");

        switch (t[0]) {

            case "ENEMYNAME":
                enemyNickname = t[1];
                SwingUtilities.invokeLater(() -> ui.lblInfo.setText(enemyNickname + "님과 게임 중"));
                break;

            case "ROUND":
                int myMoney = Integer.parseInt(t[1]);
                int enMoney = Integer.parseInt(t[2]);
                int pot = Integer.parseInt(t[3]);

                int num = Integer.parseInt(t[4]);
                int shape = Integer.parseInt(t[5]);

                SwingUtilities.invokeLater(() -> {
                    ui.lblUserCard.setIcon(CardImageLoader.loadBack());
                    ui.lblEnemyCard.setIcon(CardImageLoader.loadFront(num, shape));

                    ui.lblMoney.setText("나: " + myMoney + "원 | 상대: " + enMoney + "원");
                   
                });
                break;

            case "RESULT":
                String result = t[1];

                Card my = new Card(Integer.parseInt(t[2]), Integer.parseInt(t[3]));
                Card en = new Card(Integer.parseInt(t[4]), Integer.parseInt(t[5]));

                int newMy = Integer.parseInt(t[6]);
                int newEn = Integer.parseInt(t[7]);

                SwingUtilities.invokeLater(() -> {
                    ui.lblUserCard.setIcon(
                        CardImageLoader.loadFront(my.getCNum(), my.getCShape())
                    );
                    ui.lblEnemyCard.setIcon(
                        CardImageLoader.loadFront(en.getCNum(), en.getCShape())
                    );

                    ui.lblMoney.setText("나: " + newMy + "원 | 상대: " + newEn + "원");

                    // ⭐⭐⭐ 이 줄이 빠져 있었음 ⭐⭐⭐
                    ui.lblInfo.setText("결과: " + result + " (다음 라운드 대기중)");
                });
                break;

            
            case "CHAT":
                String sender = t[1];
                String message = msg.substring(
                    ("CHAT " + sender + " ").length()
                );

                SwingUtilities.invokeLater(() ->
                    main.deliverChat(sender, message)
                );
                break;



            
            case "GAMEOVER":
                String winner = t[1];
                SwingUtilities.invokeLater(() -> main.showGameOver(winner));
                break;
        }
    }
}
