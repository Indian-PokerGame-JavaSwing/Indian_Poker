package server;

import normalclass.Card;
import normalclass.Dealer;

public class RoundManager {

    private Dealer dealer;

    public RoundManager() {
        dealer = new Dealer();
        dealer.shuffle();
    }

    public Card[] dealCards() {

        if (dealer.remaining() < 10) {
            dealer.reset();
            dealer.shuffle();
        }

        return new Card[]{dealer.dealOne(), dealer.dealOne()};
    }

    public void sendRoundStart(PlayerConnection p1, PlayerConnection p2,
                               MoneyState money, int pot,
                               Card c1, Card c2) throws Exception {

        p1.send("ROUND " + money.p1 + " " + money.p2 + " " + pot + " " +
                c2.getCNum() + " " + c2.getCShape());

        p2.send("ROUND " + money.p2 + " " + money.p1 + " " + pot + " " +
                c1.getCNum() + " " + c1.getCShape());
    }
}
