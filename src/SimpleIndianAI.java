// 간단한 전략: 9 이상이면 콜, 아니면 폴드
public class SimpleIndianAI implements IndianPokerAI {

    @Override
    public Action decide(Card aiCard) {
        int v = aiCard.getValue();
        if (v >= 9) return Action.CALL;
        if (v <= 5) return Action.FOLD;
        // 애매한 카드(6~8)는 약간 랜덤
        return Math.random() < 0.5 ? Action.CALL : Action.FOLD;
    }
}
