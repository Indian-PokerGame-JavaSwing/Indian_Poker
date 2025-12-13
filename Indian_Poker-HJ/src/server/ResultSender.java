package server;

import normalclass.Card;

public class ResultSender {

    public void sendBoth(PlayerConnection p1, PlayerConnection p2,
                         String r1, String r2,
                         Card c1, Card c2,
                         MoneyState money) throws Exception {

        p1.send("RESULT " + r1 + " "
                + c1.getCNum() + " " + c1.getCShape() + " "
                + c2.getCNum() + " " + c2.getCShape() + " "
                + money.p1 + " " + money.p2);

        p2.send("RESULT " + r2 + " "
                + c2.getCNum() + " " + c2.getCShape() + " "
                + c1.getCNum() + " " + c1.getCShape() + " "
                + money.p2 + " " + money.p1);
    }

    public void sendGameOver(PlayerConnection p1, PlayerConnection p2, String winner) throws Exception {
        p1.send("GAMEOVER " + winner);
        p2.send("GAMEOVER " + winner);
    }
}
