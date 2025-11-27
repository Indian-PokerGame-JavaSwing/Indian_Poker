package normalclass;

public class UserAI {
    public enum IPAction { CALL, FOLD }

    /** 상대 오픈 카드(=내 카드 추정 근거)를 보고 단순 결정 */
    public IPAction decideIndianPokerAction(Card opponentVisibleCard) {
        int v = opponentVisibleCard.getNumber(); // A=1
        if (v == 1) v = 14; // A 최상(선택 규칙)
        if (v >= 9) return IPAction.CALL;
        // 약간의 랜덤 콜 섞기
        return Math.random() < 0.2 ? IPAction.CALL : IPAction.FOLD;
    }
}
