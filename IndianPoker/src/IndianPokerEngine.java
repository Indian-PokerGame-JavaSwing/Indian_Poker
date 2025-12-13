public class IndianPokerEngine {

    public enum Phase { DEAL, BETTING, REVEAL, SETTLE }

    private final Deck deck = new Deck();
    private final Player human;
    private final Player ai;
    private final IndianPokerAI aiLogic;

    private final int ANTE = 10;
    private final int CALL_AMOUNT = 10;

    private int pot;
    private Phase phase = Phase.DEAL;
    private String message = "게임을 시작합니다.";

    public IndianPokerEngine(String humanName, int startChips) {
        this.human = new Player(humanName, startChips);
        this.ai = new Player("AI", startChips);
        this.aiLogic = new SimpleIndianAI();
    }

    // --- getter들 ---

    public Player getHuman() { return human; }
    public Player getAI() { return ai; }

    public int getPot() { return pot; }
    public Phase getPhase() { return phase; }
    public String getMessage() { return message; }

    public Card getHumanCard() { return human.getCard(); }
    public Card getAICard() { return ai.getCard(); }

    public int getAnteAmount() { return ANTE; }
    public int getCallAmount() { return CALL_AMOUNT; }

    // --- 게임 흐름 ---

    public void startNewRound() {
        if (deck.remaining() < 10) {
            deck.reset();
        }

        pot = 0;
        phase = Phase.DEAL;
        message = "새 라운드를 시작합니다.";

        human.setCard(deck.draw());
        ai.setCard(deck.draw());

        ante(human);
        ante(ai);

        phase = Phase.BETTING;
        message = "상대 카드가 가려져 있습니다. 배팅 또는 포기를 선택하세요.";
    }

    private void ante(Player p) {
        p.removeChips(ANTE);
        pot += ANTE;
    }

    public void playerFold() {
        if (phase != Phase.BETTING) return;

        ai.addChips(pot);
        ai.addWin();
        human.addLose();
        message = "당신이 포기했습니다. AI가 팟(" + pot + ")을 가져갑니다.";
        phase = Phase.SETTLE;
    }

    public void playerCall() {
        if (phase != Phase.BETTING) return;

        // 사람 콜
        human.removeChips(CALL_AMOUNT);
        pot += CALL_AMOUNT;

        // AI 결정
        IndianPokerAI.Action aiAction = aiLogic.decide(ai.getCard());

        if (aiAction == IndianPokerAI.Action.FOLD) {
            human.addChips(pot);
            human.addWin();
            ai.addLose();
            message = "AI가 포기했습니다. 당신이 팟(" + pot + ")을 가져갑니다.";
            phase = Phase.SETTLE;
            return;
        }

        // AI도 콜 → 공개 후 승패 판정
        ai.removeChips(CALL_AMOUNT);
        pot += CALL_AMOUNT;

        revealAndSettle();
    }

    private void revealAndSettle() {
        phase = Phase.REVEAL;

        int humanVal = human.getCard().getValue();
        int aiVal = ai.getCard().getValue();

        if (humanVal > aiVal) {
            human.addChips(pot);
            human.addWin();
            ai.addLose();
            message = "당신 승! " + cardCompareText();
        } else if (humanVal < aiVal) {
            ai.addChips(pot);
            ai.addWin();
            human.addLose();
            message = "AI 승! " + cardCompareText();
        } else {
            // 무승부 → 팟 분할
            int half = pot / 2;
            human.addChips(half);
            ai.addChips(pot - half);
            message = "무승부! " + cardCompareText();
        }

        pot = 0; // 정산 후 0
        phase = Phase.SETTLE;
    }

    private String cardCompareText() {
        return String.format("(%s vs %s)",
                human.getCard().toShortString(),
                ai.getCard().toShortString());
    }
}
