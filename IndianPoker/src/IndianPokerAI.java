public interface IndianPokerAI {
    enum Action { CALL, FOLD }

    Action decide(Card aiCard);
}
