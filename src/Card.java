public class Card {
    private final CardRank rank;
    private final CardSuit suit;

    public Card(CardRank rank, CardSuit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public CardRank getRank() {
        return rank;
    }

    public CardSuit getSuit() {
        return suit;
    }

    public int getValue() {
        return rank.getValue(); // A=14, 2~K 그대로
    }

    public String toShortString() {
        return suit.getSymbol() + rank.getLabel();
    }
}
