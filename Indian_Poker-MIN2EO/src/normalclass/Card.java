package normalclass;

public class Card {
    private final int cNum;    // 1~13 (A=1, J=11, Q=12, K=13)
    private final int cShape;  // 1=♣, 2=♥, 3=♦, 4=♠

    public Card(int cardNum, int cardShape) {
        this.cNum = cardNum;
        this.cShape = cardShape;
    }

    // 기존 코드 호환용(있으면 사용)
    public int getCNum()   { return cNum; }
    public int getCShape() { return cShape; }

    // 인디언 포커용 별칭
    public int getNumber() { return cNum; }

    public String getPattern() {
        switch (cShape) {
            case 1: return "♣";
            case 2: return "♥";
            case 3: return "♦";
            case 4: return "♠";
            default: return "?";
        }
    }
}
