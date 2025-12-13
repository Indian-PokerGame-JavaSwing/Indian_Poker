package normalclass;

import java.util.ArrayList;

public class User {
    private String userName;
    private int userMoney;
    private final boolean userOrAI;
    private final ArrayList<Card> myCards = new ArrayList<>(1);

    public User() {
        this("Player", 200, true);
    }

    public User(String userName, int money, boolean userOrAI) {
        this.userName = userName;
        this.userMoney = money;
        this.userOrAI = userOrAI;
    }

    public String getUserName() { return userName; }
    public void setUserName(String name) { this.userName = name; }

    public int getMoney() { return userMoney; }
    public void setMoney(int money) { this.userMoney = money; }
    public void addMoney(int delta) { this.userMoney += delta; }

    public boolean isUser() { return userOrAI; }

    public void setMyCard(Card c) { 
        myCards.clear();
        myCards.add(c); 
    }
    public ArrayList<Card> getMycard() { return myCards; }
}
