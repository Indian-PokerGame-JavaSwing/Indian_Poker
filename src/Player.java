public class Player {
    private final String name;
    private int chips;
    private Card card;
    private int win;
    private int lose;

    public Player(String name, int chips) {
        this.name = name;
        this.chips = chips;
    }

    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public void addChips(int amount) {
        chips += amount;
    }

    public void removeChips(int amount) {
        chips -= amount;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public void addWin() { win++; }
    public void addLose() { lose++; }

    public int getWin() { return win; }
    public int getLose() { return lose; }
}
