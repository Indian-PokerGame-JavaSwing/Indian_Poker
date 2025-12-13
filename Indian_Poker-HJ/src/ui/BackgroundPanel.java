package ui;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {

    private Image background;

    public BackgroundPanel(String path) {
        background = new ImageIcon(getClass().getResource(path)).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }
}
