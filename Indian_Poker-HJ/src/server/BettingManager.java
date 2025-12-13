package server;

public class BettingManager {

    public int currentBet;
    public int betP1, betP2;

    public BettingManager(int ante) {
        this.currentBet = ante;
        this.betP1 = ante;
        this.betP2 = ante;
    }

    public void applyActionP1(String[] cmd, MoneyState money, int pot[]) {

        String action = cmd[0];

        switch (action) {

            case "FOLD":
                money.p2 += pot[0];
                pot[0] = 0;
                money.foldWinner = 2;
                break;

            case "ALLIN":
                betP1 += money.p1;
                pot[0] += money.p1;
                money.p1 = 0;
                currentBet = betP1;
                break;

            case "RAISE":
                int raise1 = Integer.parseInt(cmd[1]);
                money.p1 -= raise1;
                betP1 += raise1;
                currentBet = betP1;
                pot[0] = betP1 + betP2;
                break;

            case "CALL":
                int diff1 = currentBet - betP1;
                money.p1 -= diff1;
                betP1 += diff1;
                pot[0] = betP1 + betP2;
                break;
        }
    }

    public void applyActionP2(String[] cmd, MoneyState money, int pot[]) {

        String action = cmd[0];

        switch (action) {

            case "FOLD":
                money.p1 += pot[0];
                pot[0] = 0;
                money.foldWinner = 1;
                break;

            case "ALLIN":
                betP2 += money.p2;
                pot[0] += money.p2;
                money.p2 = 0;
                currentBet = betP2;
                break;

            case "RAISE":
                int raise2 = Integer.parseInt(cmd[1]);
                money.p2 -= raise2;
                betP2 += raise2;
                currentBet = betP2;
                pot[0] = betP1 + betP2;
                break;

            case "CALL":
                int diff2 = currentBet - betP2;
                money.p2 -= diff2;
                betP2 += diff2;
                pot[0] = betP1 + betP2;
                break;
        }
    }
}
