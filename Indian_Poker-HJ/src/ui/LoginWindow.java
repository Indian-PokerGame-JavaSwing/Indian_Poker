package ui;

import validator.NicknameValidator;

import javax.swing.*;

public class LoginWindow extends JFrame {

    public interface LoginListener {
        void onLoginSuccess(String nickname);
    }

    public LoginWindow(LoginListener listener) {

        setTitle("닉네임 입력");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        JLabel label = new JLabel("닉네임을 입력하세요:");
        label.setBounds(50, 30, 300, 25);
        add(label);

        JTextField nicknameField = new JTextField();
        nicknameField.setBounds(50, 60, 280, 30);
        add(nicknameField);

        JButton confirm = new JButton("확인");
        confirm.setBounds(150, 110, 100, 30);
        confirm.addActionListener(e -> {

            String nickname = nicknameField.getText().trim();

            if (!NicknameValidator.validate(nickname)) return;

            listener.onLoginSuccess(nickname);
            dispose();
        });

        add(confirm);
        setVisible(true);
    }
}
