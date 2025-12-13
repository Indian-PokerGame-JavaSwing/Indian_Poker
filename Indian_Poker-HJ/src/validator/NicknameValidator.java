package validator;

import javax.swing.*;

public class NicknameValidator {

    public static boolean validate(String nickname) {

        nickname = nickname.trim();

        if (nickname.isEmpty()) {
            show("닉네임을 입력해주세요!");
            return false;
        }
        if (nickname.length() > 10) {
            show("닉네임은 최대 10자까지 가능합니다!");
            return false;
        }
        if (Character.isDigit(nickname.charAt(0))) {
            show("닉네임은 숫자로 시작할 수 없습니다!");
            return false;
        }
        if (nickname.matches("\\d+")) {
            show("닉네임은 숫자로만 구성될 수 없습니다!");
            return false;
        }

        return true;
    }

    private static void show(String msg) {
        JOptionPane.showMessageDialog(null, msg, "입력 오류", JOptionPane.WARNING_MESSAGE);
    }
}
