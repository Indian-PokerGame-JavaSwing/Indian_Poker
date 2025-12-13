package normalclass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dealer {

    private final List<Card> deck = new ArrayList<>(52); // 남은 덱(한 벌)

    public Dealer() {
        reset();
        shuffle();
    }

    public void reset() {
        deck.clear();
        // 1~13(A~K), 1~4(♣♥♦♠)
        for (int shape = 1; shape <= 4; shape++) {
            for (int num = 1; num <= 13; num++) {
                deck.add(new Card(num, shape));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    /** 한 장 뽑기 (덱 비면 자동 리셋 후 셔플) */
    public Card dealOne() {
        if (deck.isEmpty()) {
            reset();
            shuffle();
        }
        return deck.remove(0);
    }

    /** 남은 카드 수 */
    public int remaining() {
        return deck.size();
    }
}
