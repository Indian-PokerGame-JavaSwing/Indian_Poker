package g_GamePage;

import javax.swing.*;
import java.awt.*;

public class CardImageLoader {

    private static final String CARD_IMG_DIR = System.getProperty("user.dir") + "/src/plus_Card/";
    private static final String CARD_BACK_IMG = CARD_IMG_DIR + "CardBackImg.png";

    public static Icon loadFront(int num, int shape) {
        if (num == 1) num = 14;
        String path = CARD_IMG_DIR + "Card" + num + shape + ".png";

        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(240, 340, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static Icon loadBack() {
        ImageIcon icon = new ImageIcon(CARD_BACK_IMG);
        Image img = icon.getImage().getScaledInstance(240, 340, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
