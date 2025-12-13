package ui;

import g_GamePage.PlayIndianPoker;
import javax.swing.*;

public class MenuActionHandler {

    private final JFrame frame;
    private final String nickname;

    public MenuActionHandler(JFrame frame, String nickname) {
        this.frame = frame;
        this.nickname = nickname;
    }

    // 게임 시작 버튼 클릭 처리
    public void handleStart() {
        frame.dispose();
        new PlayIndianPoker(nickname);
    }

    // 설정 버튼 클릭 처리
    public void handleSettings() {
        JOptionPane.showMessageDialog(
            frame,
            "설정 기능은 곧 추가됩니다!",
            "설정",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // 종료 버튼 클릭 처리
    public void handleExit() {
        System.exit(0);
    }
}
