package src;

import src.config.ConfigManager;
import src.network.SocketManager;
import src.ui.AgentSettingUI;
import src.ui.TrayManager;
import src.ui.SettingUIManager;

import javax.swing.*;


public class CmsAgent {

    public static void main(String[] args) {
        // 1. 설정 로드
        ConfigManager.loadConfig();

        // 2. UI 초기화 (EDT 스레드에서 실행)
        SwingUtilities.invokeLater(() -> {
            AgentSettingUI settingUI = SettingUIManager.createAndSetup();
            TrayManager.initTray(settingUI);
        });

        // 3. 소켓 시작
        SocketManager.startSocket();
    }
}