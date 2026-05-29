package src.ui;

import src.CmsAgent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class AgentSettingUI extends JFrame {
    // 텍스트 라벨 및 입력 필드
    private JLabel statusLabel;
    private JTextField portField;
    private JPasswordField passwordField;
    private JTextField[] shortcutNameFields;
    private JTextField[] shortcutPathFields;

    // 버튼 컴포넌트
    private JButton cancelButton;
    private JButton saveButton;

    // 수정여부 확인용 변수들
    private String originalPort = "";
    private String originalPassword = "";
    private String[] originalNames = new String[3];
    private String[] originalPaths = new String[3];

    public AgentSettingUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());                // 룩앤필 설정 (Windows 스타일)

            // 폰트 설정 (Pretendard Variable)
            Font customFont = Font.createFont(
                    Font.TRUETYPE_FONT, CmsAgent.class.getResourceAsStream("/resource/font.ttf")
            ).deriveFont(13f);

            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);

            UIManager.put("Label.font", customFont);
            UIManager.put("Button.font", customFont);
            UIManager.put("TextField.font", customFont);
            UIManager.put("PasswordField.font", customFont);
            UIManager.put("ComboBox.font", customFont);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("에이전트 설정");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        setSize(420, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        statusLabel = new JLabel("에이전트의 IP 상태를 확인하는 중입니다...");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        mainPanel.add(createInputRow("포트", portField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(createInputRow("암호", passwordField = new JPasswordField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35)));

        shortcutNameFields = new JTextField[3];
        shortcutPathFields = new JTextField[3];

        for (int i = 0; i < 3; i++) {
            JLabel shortcutLabel = new JLabel("바로가기 " + (i + 1));
            shortcutLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(shortcutLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));

            JPanel shortcutRow = new JPanel(new BorderLayout(15, 0));
            shortcutRow.setBackground(Color.WHITE);
            shortcutRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            shortcutRow.setMaximumSize(new Dimension(400, 35));

            shortcutNameFields[i] = new JTextField();
            shortcutNameFields[i].setPreferredSize(new Dimension(110, 35));

            shortcutPathFields[i] = new JTextField();

            shortcutRow.add(shortcutNameFields[i], BorderLayout.WEST);
            shortcutRow.add(shortcutPathFields[i], BorderLayout.CENTER);

            mainPanel.add(shortcutRow);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(400, 60));

        cancelButton = new JButton("취소");
        saveButton = new JButton("저장");

        Dimension buttonSize = new Dimension(100, 38);
        cancelButton.setPreferredSize(buttonSize);
        saveButton.setPreferredSize(buttonSize);
        cancelButton.setBackground(new Color(225, 225, 225));
        saveButton.setBackground(new Color(225, 225, 225));

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private JPanel createInputRow(String labelText, JTextField textField) {
        JPanel rowPanel = new JPanel(new BorderLayout(20, 0));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setMaximumSize(new Dimension(400, 35));

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(35, 35));

        rowPanel.add(label, BorderLayout.WEST);
        rowPanel.add(textField, BorderLayout.CENTER);

        return rowPanel;
    }

    public void setInitialData(String port, String password, String[] names, String[] paths) {
        this.originalPort = port;
        this.originalPassword = password;
        this.portField.setText(port);
        this.passwordField.setText(password);

        for (int i = 0; i < 3; i++) {
            this.originalNames[i] = (names[i] != null) ? names[i] : "";
            this.shortcutNameFields[i].setText(this.originalNames[i]);

            this.originalPaths[i] = (paths[i] != null) ? paths[i] : "";
            this.shortcutPathFields[i].setText(this.originalPaths[i]);
        }
    }

    public boolean isModified() {
        if (!getPort().equals(originalPort)) return true;
        if (!getPassword().equals(originalPassword)) return true;

        for (int i = 0; i < 3; i++) {
            if (!getShortcutName(i).equals(originalNames[i])) return true;
            if (!getShortcutPath(i).equals(originalPaths[i])) return true;
        }
        return false;
    }

    public void setStatusIp(String ip) {
        statusLabel.setText("에이전트가 " + ip + "에서 실행 중입니다.");
    }

    public String getPort() { return portField.getText(); }
    public String getPassword() { return new String(passwordField.getPassword()); }

    public String getShortcutName(int index) {
        if (index >= 0 && index < 3) {
            return shortcutNameFields[index].getText();
        }
        return null;
    }

    public String getShortcutPath(int index) {
        if (index >= 0 && index < 3) {
            return shortcutPathFields[index].getText();
        }
        return null;
    }

    public JButton getSaveButton() { return saveButton; }
    public JButton getCancelButton() { return cancelButton; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AgentSettingUI ui = new AgentSettingUI();
            ui.setStatusIp("192.168.0.15");
            ui.setVisible(true);
        });
    }
}