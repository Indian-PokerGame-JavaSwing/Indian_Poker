package ui;

import javax.swing.*;
import java.awt.*;

public class MenuButtonFactory {

    public static JButton createButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 150, 50);

        btn.setFocusPainted(false);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(85, 50, 150));
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(110, 70, 180));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(85, 50, 150));
            }
        });

        return btn;
    }
}
