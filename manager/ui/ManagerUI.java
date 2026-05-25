package ui;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class ManagerUI {

    // 로직 및 백엔드 데이터 연동을 위한 JList 컴포넌트 정의
    private JList<PcAgentData> pcJList;
    private DefaultListModel<PcAgentData> listModel;
    private CardLayout rightCardLayout;
    private JPanel rightPanel;

    // 상세 정보 화면의 가변 데이터 컴포넌트 정의
    private JLabel mainTitleLabel;
    private JLabel osValueLabel;
    private JLabel cpuValueLabel;
    private JLabel ramValueLabel;
    private JLabel diskValueLabel;
    private JProgressBar memoryProgressBar;
    private JLabel memoryValueLabel;
    private JComboBox<String> systemActionCombo;

    /**
     * 실행 진입점 메인 메서드
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManagerUI ui = new ManagerUI();
            ui.drawUI();
        });
    }

    /**
     * 메인 창(JFrame) 생성 및 조립
     */
    public void drawUI() {
        JFrame frame = new JFrame("Manager Central Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 650);
        frame.setLocationRelativeTo(null);

        // 메인 본문 패널 구성
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // 상단 툴바 레이어 추가 (+ 버튼 배치)
        mainPanel.add(createTopToolbar(frame), BorderLayout.NORTH);

        // 좌/우 분할 레이어 추가 (PC 목록 및 상세 정보 대시보드)
        mainPanel.add(createCentralSplitLayout(frame), BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);

        // [매핑 코드 추가] 프로그램 구동 직후 백엔드 API 또는 DB로부터 단말기 목록 초기화 데이터 갱신 로직 호출부
        loadBackendPcList();
    }

    /**
     * 1. 상단 툴바 레이어 생성 (+ 버튼)
     */
    private JPanel createTopToolbar(JFrame parentFrame) {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbarPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("+");
        addButton.setFont(new Font("Monospaced", Font.BOLD, 22));
        addButton.setPreferredSize(new Dimension(35, 35));
        addButton.setFocusPainted(false);
        addButton.setBackground(new Color(224, 224, 224));
        addButton.setBorder(BorderFactory.createEmptyBorder());
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // + 버튼 클릭 시 에이전트 등록 팝업창 활성화
        addButton.addActionListener(e -> showAddAgentDialog(parentFrame));

        toolbarPanel.add(addButton);
        return toolbarPanel;
    }

    /**
     * 2. 중앙 좌/우 분할 구조 레이어 생성
     */
    private JPanel createCentralSplitLayout(JFrame parentFrame) {
        JPanel splitPanel = new JPanel(new BorderLayout(15, 0));
        splitPanel.setBackground(Color.WHITE);
        splitPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        // 좌측 영역 조립
        splitPanel.add(createLeftNavigationArea(), BorderLayout.WEST);

        // 우측 영역 조립
        splitPanel.add(createRightDisplayArea(), BorderLayout.CENTER);

        return splitPanel;
    }

    /**
     * 3. 좌측 PC 목록 영역 생성 (JList 기반 설계)
     */
    private JPanel createLeftNavigationArea() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(260, 0));
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // 동적 바인딩을 위한 리스트 모델 및 JList 객체 인스턴스화
        listModel = new DefaultListModel<>();
        pcJList = new JList<>(listModel);
        pcJList.setBackground(new Color(245, 245, 245));
        pcJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 가독성을 위한 JList 셀 렌더러 적용 (온라인/오프라인 디자인화)
        pcJList.setCellRenderer(new PcListCellRenderer());

        // [매핑 코드 추가] JList 아이템 선택 변경 시 마우스 클릭 이벤트 바인딩 처리 리스너
        pcJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                PcAgentData selectedPc = pcJList.getSelectedValue();
                if (selectedPc != null) {
                    bindPcDataToRightDetailPanel(selectedPc);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(pcJList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        return leftPanel;
    }

    /**
     * 4. 우측 상세 정보 화면 전환 레이어 생성 (CardLayout)
     */
    private JPanel createRightDisplayArea() {
        rightCardLayout = new CardLayout();
        rightPanel = new JPanel(rightCardLayout);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // 3가지 가상 룸 배치
        JPanel blankView = new JPanel(new BorderLayout());
        blankView.setBackground(Color.WHITE);
        JLabel defaultMessage = new JLabel("조회할 에이전트 PC를 좌측 목록에서 선택하십시오.", SwingConstants.CENTER);
        defaultMessage.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        defaultMessage.setForeground(Color.GRAY);
        blankView.add(defaultMessage, BorderLayout.CENTER);

        JPanel onlineView = createOnlineRoomLayout();
        JPanel offlineView = createOfflineRoomLayout();

        rightPanel.add(blankView, "BLANK_VIEW");
        rightPanel.add(onlineView, "ONLINE_VIEW");
        rightPanel.add(offlineView, "OFFLINE_VIEW");

        rightCardLayout.show(rightPanel, "BLANK_VIEW");
        return rightPanel;
    }

    /**
     * 5. ONLINE_VIEW 상세 레이아웃 구조 설계
     */
    private JPanel createOnlineRoomLayout() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // 5-1. 헤더 영역 (명칭, IP, 삭제버튼)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        headerPanel.setPreferredSize(new Dimension(0, 40));

        mainTitleLabel = new JLabel("● PC-ONLINE (192.168.0.1)");
        mainTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));

        JButton deleteButton = new JButton("삭제");
        // [매핑 코드 추가] 백엔드 연동을 통한 에이전트 연결 해제 및 DB 삭제 명령어 처리부
        deleteButton.addActionListener(e -> handleAgentDelete());

        headerPanel.add(mainTitleLabel, BorderLayout.WEST);
        headerPanel.add(deleteButton, BorderLayout.EAST);

        // 5-2. 메인 컨텐츠 영역
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 5, 10, 5));

        JLabel infoTitle = new JLabel("시스템 사양 정보");
        infoTitle.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(infoTitle);
        contentPanel.add(Box.createVerticalStrut(10));

        // 하드웨어 스펙 테이블 그리드 구성
        JPanel sysGrid = new JPanel(new GridLayout(4, 2, 0, 8));
        sysGrid.setBackground(Color.WHITE);
        sysGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        sysGrid.add(new JLabel("운영체제 (OS)"));
        osValueLabel = new JLabel("-", SwingConstants.RIGHT);
        sysGrid.add(osValueLabel);

        sysGrid.add(new JLabel("프로세서 (CPU)"));
        cpuValueLabel = new JLabel("-", SwingConstants.RIGHT);
        sysGrid.add(cpuValueLabel);

        sysGrid.add(new JLabel("설치된 물리 메모리"));
        ramValueLabel = new JLabel("-", SwingConstants.RIGHT);
        sysGrid.add(ramValueLabel);

        sysGrid.add(new JLabel("디스크 저장 용량"));
        diskValueLabel = new JLabel("-", SwingConstants.RIGHT);
        sysGrid.add(diskValueLabel);

        contentPanel.add(sysGrid);
        contentPanel.add(Box.createVerticalStrut(25));

        // 리소스 게이지바 구현부
        JPanel memHeader = new JPanel(new BorderLayout());
        memHeader.setBackground(Color.WHITE);
        memHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        memHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        memHeader.add(new JLabel("실시간 메모리 사용 현황"), BorderLayout.WEST);
        // 1. 객체 생성 시에는 텍스트만 전달함
        memoryValueLabel = new JLabel("0 / 0 MB");

// 2. 부모 패널에 넣을 때 오른쪽(EAST) 배치를 지정함
        memHeader.add(memoryValueLabel, BorderLayout.EAST);
        contentPanel.add(memHeader);
        contentPanel.add(Box.createVerticalStrut(10));

        memoryProgressBar = new JProgressBar(0, 100);
        memoryProgressBar.setStringPainted(true);
        memoryProgressBar.setBackground(new Color(235, 235, 235));
        memoryProgressBar.setForeground(new Color(130, 180, 220));
        memoryProgressBar.setBorderPainted(false);
        memoryProgressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        memoryProgressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        contentPanel.add(memoryProgressBar);
        contentPanel.add(Box.createVerticalStrut(35));

        // 제어 스크립트 실행 컨트롤부
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomRow.setBackground(Color.WHITE);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel remotePanel = new JPanel(new BorderLayout());
        remotePanel.setBackground(Color.WHITE);
        //remotePanel.add(" ", BorderLayout.NORTH);


        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Color.WHITE);
        JLabel controlTitle = new JLabel("시스템 원격 명령어 할달");
        controlTitle.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        controlPanel.add(controlTitle, BorderLayout.NORTH);

        JPanel controlAction = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        controlAction.setBackground(Color.WHITE);

        String[] actions = {"시스템 종료 (Shutdown)", "다시 시작 (Reboot)", "컴퓨터 즉시 잠금"};
        systemActionCombo = new JComboBox<>(actions);
        systemActionCombo.setBackground(Color.WHITE);
        JButton runButton = new JButton("명령 실행");

        // [매핑 코드 추가] 컴보박스에서 선택된 제어 인덱스를 추출하여 호스트 패킷 송신 모듈로 명령어 패킷을 하달하는 구현부
        runButton.addActionListener(e -> handleSystemControlCommand());

        controlAction.add(systemActionCombo);
        controlAction.add(Box.createHorizontalStrut(10));
        controlAction.add(runButton);
        controlPanel.add(controlAction, BorderLayout.CENTER);

        bottomRow.add(remotePanel);
        bottomRow.add(controlPanel);
        contentPanel.add(bottomRow);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 6. OFFLINE_VIEW 상세 레이아웃 구조 설계
     */
    private JPanel createOfflineRoomLayout() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        headerPanel.setPreferredSize(new Dimension(0, 40));

        JLabel offlineTitle = new JLabel("● PC-OFFLINE (연결 유실)");
        offlineTitle.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        offlineTitle.setForeground(Color.RED);
        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> handleAgentDelete());

        headerPanel.add(offlineTitle, BorderLayout.WEST);
        headerPanel.add(deleteButton, BorderLayout.EAST);

        JLabel errorLabel = new JLabel("해당 에이전트 단말기 네트워크와 소켓 세션 연결을 수립할 수 없습니다.", SwingConstants.CENTER);
        errorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        errorLabel.setForeground(Color.RED);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(errorLabel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 7. 새로운 에이전트 단말 PC 등록 보조 모달 창 띄우기
     */
    private void showAddAgentDialog(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "새로운 원격 에이전트 디바이스 추가", true);
        dialog.setSize(340, 220);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JTextField ipField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JTextField aliasField = new JTextField();

        formPanel.add(new JLabel("대상 에이전트 IP"));
        formPanel.add(ipField);
        formPanel.add(new JLabel("접속 보안 암호"));
        formPanel.add(passField);
        formPanel.add(new JLabel("단말기 식별 명칭"));
        formPanel.add(aliasField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton cancelButton = new JButton("취소");
        JButton connectButton = new JButton("에이전트 추가");

        cancelButton.addActionListener(e -> dialog.dispose());

        // [매핑 코드 추가] 입력창 데이터 파싱 검증 및 백엔드 데이터베이스 신규 에이전트 INSERT 쿼리 요청 로직 구현부
        connectButton.addActionListener(e -> {
            String targetIp = ipField.getText();
            String alias = aliasField.getText();
            if(!targetIp.isEmpty()) {
                // 더미 데이터 바인딩 동작 예시
                listModel.addElement(new PcAgentData(alias.isEmpty() ? "PC-AGENT" : alias, targetIp, true, "Win 11", "Intel i7", "16GB", "1TB", 35));
                dialog.dispose();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(connectButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    /**
     * =========================================================================
     * [백엔드 데이터 및 이벤트 처리 매핑 메서드 저장 공간]
     * =========================================================================
     */

    /**
     * [매핑 코드 추가] 백엔드 저장소로부터 등록된 단말 장치 데이터들을 동적 폴링하여 JList에 적재하는 영역
     */
    private void loadBackendPcList() {
        // 실제 운영 환경에서는 데이터베이스 연동 및 REST API 응답 데이터를 활용함
        // 아래 코드는 정상 매핑 검증을 위한 디버깅용 목업 데이터 세팅 모델 코드임
        listModel.addElement(new PcAgentData("서버실-메인PC", "192.168.0.2", true, "Windows 11 Education", "AMD Ryzen 5600", "16,834 MB", "3,049 GB", 25));
        listModel.addElement(new PcAgentData("개발실-테스트PC", "192.168.0.3", false, "Unknown", "Unknown", "Unknown", "Unknown", 0));
    }

    /**
     * [매핑 코드 추가] JList 아이템 선택 이벤트를 수신하여 우측 CardLayout 뷰 컴포넌트에 변수를 바인딩하는 영역
     */
    private void bindPcDataToRightDetailPanel(PcAgentData data) {
        if (data.isOnline()) {
            mainTitleLabel.setText("● " + data.getPcName() + " (" + data.getIpAddress() + ")");
            osValueLabel.setText(data.getOsInfo());
            cpuValueLabel.setText(data.getCpuInfo());
            ramValueLabel.setText(data.getRamInfo());
            diskValueLabel.setText(data.getDiskInfo());
            memoryProgressBar.setValue(data.getMemoryUsagePercent());
            memoryValueLabel.setText((data.getMemoryUsagePercent() * 160) + " / 16834 MB");

            rightCardLayout.show(rightPanel, "ONLINE_VIEW");
        } else {
            rightCardLayout.show(rightPanel, "OFFLINE_VIEW");
        }
    }

    /**
     * [매핑 코드 추가] 에이전트 단말 데이터 원격 삭제 요청 처리 핸들러 영역
     */
    private void handleAgentDelete() {
        PcAgentData selected = pcJList.getSelectedValue();
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(null, selected.getPcName() + " 단말을 관제 시스템에서 영구히 삭제하겠습니까?", "Warning", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // 백엔드 삭제 API 호출 및 JList 실시간 컴포넌트 데이터 추방
                listModel.removeElement(selected);
                rightCardLayout.show(rightPanel, "BLANK_VIEW");
            }
        }
    }

    /**
     * [매핑 코드 추가] 원격 명령어 할달 처리 핸들러 영역 (시스템 종료 / 재부팅 / 잠금 패킷 하달 실행부)
     */
    private void handleSystemControlCommand() {
        PcAgentData selected = pcJList.getSelectedValue();
        String selectedCommand = (String) systemActionCombo.getSelectedItem();

        if (selected != null) {
            JOptionPane.showMessageDialog(null,
                    selected.getPcName() + " 단말 장치로 다음의 원격 제어 명령 패킷을 전달합니다:\n" + selectedCommand,
                    "명령어 송신 완료",
                    JOptionPane.INFORMATION_MESSAGE);

            // TODO: ProcessBuilder 연동 네트워크 패킷 스트림 처리 로직 파이프라인 연계
        }
    }

    /**
     * JList 객체 관리를 구조화하기 위한 단말 에이전트 인스턴스 정보 DTO 내부 클래스
     */
    private static class PcAgentData {
        private final String pcName;
        private final String ipAddress;
        private final boolean isOnline;
        private final String osInfo;
        private final String cpuInfo;
        private final String ramInfo;
        private final String diskInfo;
        private final int memoryUsagePercent;

        public PcAgentData(String pcName, String ipAddress, boolean isOnline, String osInfo, String cpuInfo, String ramInfo, String diskInfo, int memoryUsagePercent) {
            this.pcName = pcName;
            this.ipAddress = ipAddress;
            this.isOnline = isOnline;
            this.osInfo = osInfo;
            this.cpuInfo = cpuInfo;
            this.ramInfo = ramInfo;
            this.diskInfo = diskInfo;
            this.memoryUsagePercent = memoryUsagePercent;
        }

        public String getPcName() { return pcName; }
        public String getIpAddress() { return ipAddress; }
        public boolean isOnline() { return isOnline; }
        public String getOsInfo() { return osInfo; }
        public String getCpuInfo() { return cpuInfo; }
        public String getRamInfo() { return ramInfo; }
        public String getDiskInfo() { return diskInfo; }
        public int getMemoryUsagePercent() { return memoryUsagePercent; }
    }

    /**
     * JList 가독성을 위해 동적 그래픽 변경을 적용한 GUI 커스텀 렌더러
     */
    private static class PcListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel card = new JPanel(new GridLayout(2, 1, 0, 3));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 224, 224)),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));

            if (isSelected) {
                card.setBackground(new Color(210, 230, 245));
            } else {
                card.setBackground(Color.WHITE);
            }

            if (value instanceof PcAgentData) {
                PcAgentData data = (PcAgentData) value;
                String dotColor = data.isOnline() ? "green" : "red";
                JLabel titleLabel = new JLabel("<html><font color='" + dotColor + "'>●</font> <b>" + data.getPcName() + "</b></html>");
                titleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

                JLabel subLabel = new JLabel(data.isOnline() ? "접속 IP: " + data.getIpAddress() : "네트워크 유실 - 다시 연결 시도 중");
                subLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
                subLabel.setForeground(Color.GRAY);

                card.add(titleLabel);
                card.add(subLabel);
            }
            return card;
        }
    }
}