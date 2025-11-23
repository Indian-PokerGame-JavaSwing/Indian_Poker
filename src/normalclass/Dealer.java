package normalclass;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Dealer 클래스
 *
 * 역할:
 *   - 서버에서 사용되는 "카드 덱 관리" 전용 클래스
 *   - 52장의 카드 생성 / 셔플 / 카드 한 장씩 배분 기능 제공
 *
 * 클라이언트(PlayIndianPoker)는 게임 로직을 가지지 않기 때문에
 * Dealer는 오직 서버(IndianPokerServer)에서만 사용된다.
 */
public class Dealer {

    // 52장의 카드를 저장할 리스트
    private ArrayList<Card> deck;

    /**
     * 생성자
     * 객체 생성 시 자동으로 덱을 초기화(reset)하고
     * 카드를 섞기(shuffle) 위해 reset에서 전체 카드 생성
     */
    public Dealer() {
        reset();
    }

    /**
     * 덱 초기화 (새로운 52장 세트 생성)
     *
     * 카드 규칙:
     *   - 숫자: 1(A) ~ 13(K)
     *   - 문양(shape): 1~4
     *   → 총 52장
     *
     * 서버가 여러 라운드를 반복하다 보면
     * 카드가 부족해지므로 IndianPokerServer에서 자동으로 reset() 호출됨.
     */
    public void reset() {
        deck = new ArrayList<>();

        // shape: 1~4, number: 1~13
        for (int shape = 1; shape <= 4; shape++) {
            for (int num = 1; num <= 13; num++) {
                deck.add(new Card(num, shape));
            }
        }
    }

    /**
     * 카드를 랜덤하게 섞기
     *
     * Collections.shuffle() 사용
     * 서버 시작 시 1번 실행
     * 카드가 거의 다 떨어졌을 때 서버에서 다시 shuffle 함
     */
    public void shuffle() {
        Collections.shuffle(deck);
    }

    /**
     * 카드 한 장 배분
     *
     * deck에서 맨 앞 카드(0번)를 꺼내고 제거한다.
     * deck이 비었을 경우 null을 반환 (서버가 감지해 reset 후 사용)
     *
     * @return Card 한 장 또는 null
     */
    public Card dealOne() {
        if (deck.isEmpty()) return null;
        return deck.remove(0);
    }

    /**
     * 덱에 몇 장 남았는지 확인
     *
     * 서버에서 라운드가 반복되다가 카드가 10장 이하가 되면
     * reset() + shuffle()을 자동 호출해 새 덱을 다시 준비한다.
     *
     * @return 남은 카드 수
     */
    public int remaining() {
        return deck.size();
    }
}
