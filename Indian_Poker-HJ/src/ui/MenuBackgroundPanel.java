package ui;

import javax.swing.*;
import java.awt.*;

public class MenuBackgroundPanel extends JPanel {

    private Image background;

    public MenuBackgroundPanel(String path) {
        background = new ImageIcon(getClass().getResource(path)).getImage();
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }
}
