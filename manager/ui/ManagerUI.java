package ui;

import javax.swing.*; // 스윙의 기본 컴퍼넌트들을 가져옴
import java.awt.*; // awt는 레이아웃,색상,폰트 등을 다룸
import java.awt.event.MouseAdapter; // 마우스 클릭 등 마우스 이벤트를 처리하기 위한 클래스 (기본 형태에서 이 라이브러리가 추가됨)
import java.awt.event.MouseEvent; // 마우스 클릭 시 발생하는 상세 정보를 담는 클래스 (기본 형태에서 이 라이브러리가 추가됨)

public class ManagerUI {
    public static void main(String[] args) {
        // 1. JFrame 생성
        JFrame frame = new JFrame("Manager");  // 객체를 선언 및 생성하고 Manager라는 이름(제목)의 프로그램 창임

        // 2. contentPanel 생성
        JPanel contentPanel = new JPanel(new BorderLayout()); // 객체를 선언 및 생성하고 여기서 new BorderLayout()는 이 패널안에 들어가는 요소를 왼,오,위,아래,중앙으로 배치할 수 있게하는 코드임
        contentPanel.setBackground(Color.WHITE); // contentPanel 배경색을 흰색으로 설정함

        // 3. bodyPanel 생성 (몸통 패널, 위,가운데로 2개의 공간을 나눔 위는 +버튼 있는 줄, 가운데는 왼쪽 목록 + 오른쪽 상세정보를 담은 큰 영역)
        JPanel bodyPanel = new JPanel(new BorderLayout()); // 객체를 선언 및 생성하고 전체 영역을 만드는 패널임(이 안에는 위쪽:toolbarPanel,가운데:splitPanel이 들어감)
        bodyPanel.setBackground(Color.WHITE); // 이 몸통 본문 영역의 배경색을 흰색으로 설정함

        // 4. toolbarPanel 생성
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)); // +버튼이 들어가는 툴바 영역 만듬(FlowLayout.left: 왼쪽 정렬, 15:컴포넌트 사이 가로 간격, 10:위아래(세로)간격)
        toolbarPanel.setBackground(Color.WHITE); // 툴바 영역색을 흰색으로 설정함

        // 5. +버튼 생성
        JButton addButton = new JButton("+"); // +라고 적힌 버튼을 만듬
        addButton.setFont(new Font("Monospaced", Font.BOLD, 22)); // 버튼 안의 + 글자 스타일을 설정하는 코드(고정폭 글꼴, 굵게, 글자 크기)
        addButton.setPreferredSize(new Dimension(35, 35)); // 버튼의 크기 설정(가로, 세로)
        addButton.setFocusPainted(false); // 버튼 클릭 시 생기는 표시선 제거(디자인 용임)
        addButton.setBackground(new Color(224, 224, 224)); // 버튼의 배경색을 연한 회색으로 설정함
        addButton.setBorder(BorderFactory.createEmptyBorder()); // 버튼의 기본 테두리를 없앰(createEmptyBorder()는 선이 없는 빈 테두리라 보면 됨
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스를 버튼 위에 올렸을 때 커서를 손가락 모양으로 바꿈(디자인 용임)

        // 5-2. +버튼 이벤트 연결(+ 버튼을 클릭하면 showAddAgentDialog(frame) 메서드를 실행)
        addButton.addActionListener(e -> { // 버튼 클릭 이벤트를 등록하는 코드임
            showAddAgentDialog(frame); // 아래쪽에 따로 만든 showAddAgentDialog 메서드를 실행하는 코드임
        });

        toolbarPanel.add(addButton); // 만든 +버튼을 toolbarPanel안에 넣음, 이 과정을 넣어야 툴바 영역에 버튼이 보임

        // 6. splitPanel 생성
        JPanel splitPanel = new JPanel(new BorderLayout(15, 0)); // 객체를 선언 및 생성하고 몸통에서 왼쪽 목록과 오른쪽 상세 패널을 나란하게 담는 패널임(15:가로 간격,0:세로 간격->왼,오 중간 간격이 15)
        splitPanel.setBackground(Color.WHITE); // 이 패널의 배경색을 흰색으로 설정함
        splitPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15)); // splitPanel 안쪽 여백을 설정하는 코드(위 여백:0,왼:15,아래:15,오:15)

        // 7. leftPanel 생성
        JPanel leftPanel = new JPanel(new BorderLayout()); // 객체를 선언 및 생성하고 왼쪽 PC목록 영역 만드는 코드임
        leftPanel.setPreferredSize(new Dimension(250, 0)); // 왼쪽 패널의 크기를 설정함(가로 너비, 세로 너비)
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // 왼쪽 패널 바깥쪽에 연한 회색 테두리 그림

        // 8. pcListPanel 생성 (원래 추가가 안되어있을땐 emptyText 부분이었는데, 추가를 하면 pcListPanel이 생성됨)
        JPanel pcListPanel = new JPanel(new BorderLayout()); // 왼쪽 패널 안을 채울 큰 회색 패널을 만듬
        pcListPanel.setBackground(new Color(224, 224, 224)); // 이 패널의 배경색을 회색으로 설정함

        // 8-2. cardContainer 생성 (클릭 이벤트를 주기 위해서 기본 뼈대에서 수정)
        JPanel cardContainer = new JPanel(new GridLayout(0, 1, 0, 0)); // PC의 카드들을 담는 내부 패널임(행 개수(자동증가), 열 1개, 가로 간격(0이라 x),세로 간격(0이라 x))
        cardContainer.setBackground(new Color(224, 224, 224)); // 카드 묶음 패널 배경색도 회색으로 설정(카드 뒤쪽 배경이 부모 패널과 어색하게 다르게 보이지 않게 하기 위해서임)
        JPanel onlinePcCard = createPcCard("PC-ONLINE", true, "CPU 36% RAM 25%"); //온라인 PC카드 만드는 코드, createPcCard()메서드 호출하고 카드 하나 만들고 결과를 onlinePcCard 변수에 저장
        JPanel offlinePcCard = createPcCard("PC-OFFLINE", false, "클릭하여 다시 연결"); // 오프라인 PC 카드 만드는 코드, 여기서 false이기에 카드 안 점이 빨간색으로 표시됨
        cardContainer.add(onlinePcCard); // 아까 만든 온라인 PC 카드를 cardContainer 안에 넣기
        cardContainer.add(offlinePcCard); // 오프라인 PC 카드도 cardContainer 안에 넣기

        // 8-3. pcListPanel에 cardContainer 넣기 (쉽게 설명하면 큰 패널안에 중간 패널을 넣고 작은 패널을 넣고 버튼,라벨,입력칸을 그 안에 넣는 구조라고 생각)
        pcListPanel.add(cardContainer, BorderLayout.NORTH); // 큰 회색 상자(pcListPanel)안에서, cardContainer(카드 묶음 패널)를 위쪽에 붙임
        leftPanel.add(pcListPanel, BorderLayout.CENTER); // 완성된 큰 회색 상자 pcListPanel을 leftPanel의 중앙에 넣음

        // 9. rightPanel 생성 및 CardLayout 적용 (CardLayout은 여러개 패널을 같은 위치에 겹쳐두고, 그 중에서 하나만 보여주는 레이아웃)
        CardLayout rightCardLayout = new CardLayout(); // 오른쪽 상세 화면을 바꾸기 위해서 CardLayout 객체를 만드는 코드임
        JPanel rightPanel = new JPanel(rightCardLayout); // 오른쪽 상세 정보 영역을 만드는 코드임(rightPanel의 레이아웃을 rightCardLayout으로 설정)
        rightPanel.setBackground(Color.WHITE); // 오른쪽 패널 배경색을 흰색으로 설정함
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // 오른쪽 상세 정보 패널 바깥에 연한 테두리를 그림(테두리 색,테두리 두께)

        // 9-1. 오른쪽 패널 방 3개 만들기 (서로 다른 화면3개 blankRoom(빈 화면),onlineRoom(온라인 화면),offlineRoom(오프라인 화면))
        JPanel blankRoom = new JPanel(); // 처음 실행 시 보일 빈 화면 만드는 코드임(PC선택 안했을 때, 오른쪽에 아무 내용도 안보임)
        blankRoom.setBackground(Color.WHITE); // 빈 화면 배경색을 흰색으로 설정함
        JPanel onlineRoom = createOnlineDetailCard(); // 온라인 PC 선택시 보여줄 오른쪽 상세 정보 화면 만드는 코드임,createOnlineDetailCard()메서드가 온라인 상세 정보 패널을 만든뒤 반환하고,onlineRoom에 저장
        JPanel offlineRoom = createOfflineDetailCard(); // 오프라인 PC 선택시 보여줄 오른쪽 상세 정보 화면 만드는 코드임,createOfflineDetailCard()메서드가 오프라인 안내 패널을 만든뒤 반환하고,offlineRoom에 저장

        // 9-2. 오른쪽 패널에 화면 등록
        rightPanel.add(blankRoom, "BLANK_VIEW"); // rightPanel에 빈 화면을 등록함
        rightPanel.add(onlineRoom, "ONLINE_VIEW"); // rightPanel에 온라인 상세 화면을 등록함
        rightPanel.add(offlineRoom, "OFFLINE_VIEW"); // rightPanel에 오프라인 상세 화면을 등록함

        // 9-3. 처음 화면 설정
        rightCardLayout.show(rightPanel, "BLANK_VIEW"); // 프로그램을 처음 실행했을 때 오른쪽에 빈 화면이 보여지는 코드임

        // 10. 왼쪽 카드 클릭 이벤트(마우스 이벤트)
        onlinePcCard.addMouseListener(new MouseAdapter() { // 온라인 카드에 마우스 이벤트 감지기를 붙임(온라인 카드 마우스 클릭 -> "ONLINE_VIEW" 화면 보여짐)
            @Override // 오버라이드(부모 클래스에 이미 있는 메서드를 내가 다시 정의해서 사용함)
            public void mousePressed(MouseEvent e) { // 마우스로 카드 눌렀을 때 실행되는 메서드임(MouseEvent e는 마우스 이벤트 정보 들어가있음) (온라인 카드 부분)
                rightCardLayout.show(rightPanel, "ONLINE_VIEW"); // 오른쪽 패널에서 "ONLINE_VIEW" 화면 보여짐(온라인 카드 클릭하면 오른쪽에 온라인 상세 정보가 나옴)
            }
        });

        offlinePcCard.addMouseListener(new MouseAdapter() { // 오프라인 카드에 마우스 이벤트 감지기를 붙임(오프라인 카드 마우스 클릭 -> "OFFLINE_VIEW" 화면 보여짐)
            @Override // 오버라이드(부모 클래스에 이미 있는 메서드를 내가 다시 정의해서 사용함)
            public void mousePressed(MouseEvent e) { // 오프라인 카드를 마우스로 눌렀을 때 실행되는 부분임
                rightCardLayout.show(rightPanel, "OFFLINE_VIEW"); // 오른쪽 패널에서 "OFFLINE_VIEW" 화면을 보여짐(오프라인 카드 클릭하면 오른쪽에 온라인 상세 정보가 나옴)
            }
        });

        // 11. 만든 패널들 결합
        splitPanel.add(leftPanel, BorderLayout.WEST); // 왼쪽 목록 패널을 splitPanel의 왼쪽 영역에 넣음
        splitPanel.add(rightPanel, BorderLayout.CENTER); // 오른쪽 상세 패널을 splitPanel의 중앙 영역에 넣음

        bodyPanel.add(toolbarPanel, BorderLayout.NORTH); // +버튼이 있는 툴바 영역을 bodyPanel 위쪽에 넣음
        bodyPanel.add(splitPanel, BorderLayout.CENTER); // 왼/오로 나누어진 영역을 bodyPanel 중앙에 넣음

        // 어두운 헤더 조립 라인을 제거하고, 하얀 본문 영역(bodyPanel)이 contentPanel의 중심을 꽉 채우도록 설정함
        contentPanel.add(bodyPanel, BorderLayout.CENTER); // 본문 전체 영역을 contentPanel 중앙에 넣음

        frame.add(contentPanel); // 완성된 전체 UI 패널을 창에 넣는 코드임

        // 12. 프레임 설정
        frame.setSize(900, 600); // 창 크기 가로:900 세로:600으로 설정함
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창에서 X버튼을 눌렀을 때 프로그램이 완전히 종료됨
        frame.setLocationRelativeTo(null); // 창이 실행될 때 화면 가운데 뜨도록 설정함(null은 특정 컴포넌트 기준x, 화면 기존 중앙이라고 보면 됨)
        frame.setVisible(true); // 창을 실제로 화면에 보이게 함 (지금까지 만든 UI 나타남)
    }

    // 13. showAddAgentDialog 메서드 (에이전트 연결 창을 띄우는 메서드임)
    private static void showAddAgentDialog(JFrame parentFrame) { // private를 사용하여 이 클래스 안에서만 사용(클래스 분리하면 따로 만들면 될듯함),( )안은 메인 창을 매개변수로 받는다는 뜻임
        JDialog dialog = new JDialog(parentFrame, "새로운 에이전트 연결...", true); // 팝업창 만드는 코드, JDialog는 작은 보조창임(이 팝업의 부모창,창 제목, 모달 창 -> 이 창을 처리하기 전까지 메인화면 조작 막게 한다고 생각하면됨)
        dialog.setSize(320, 200); // 팝업 창 크기를 설정함(가로,세로)
        dialog.setLayout(new BorderLayout()); // 팝업 창 안의 배치를 BorderLayout으로 설정 -> 그래서 나중에 dialog.add(formPanel, BorderLayout.CENTER); dialog.add(buttonPanel, BorderLayout.SOUTH); 이런식으로 가운데는 입력폼, 아래에는 버튼 영역 넣을 수 있음.

        // 13-2. 입력 폼 패널
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 입력 칸들을 담을 패널을 만드는 코드임(행 개수,열 개수,가로 간격,세로 간격)
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // 입력 폼 안의 안쪽 여백을 설정(위 여백,왼 여백,아래 여백, 오 여백)

        formPanel.add(new JLabel("에이전트 IP")); // 첫 번째 칸에 "에이전트 IP"라는 라벨을 넣음
        formPanel.add(new JTextField()); // 첫 번째줄 오른쪽 칸에 입력칸 넣음 -> 최종적으로는 [에이전트 IP] [ ](에이전트 IP 입력칸) 이렇게 됨

        formPanel.add(new JLabel("암호")); // 두 번째 칸에 "암호"라는 라벨을 넣음
        formPanel.add(new JPasswordField()); // 두 번째 줄 오른쪽 칸에 비밀번호 입력칸 넣음 -> 최종적으로는 [암호] [ ](암호 입력칸) 이렇게 됨

        formPanel.add(new JLabel("별칭 (선택)")); // 세 번째 칸에 "별칭(선택)"라는 라벨을 넣음
        formPanel.add(new JTextField()); // 세 번째 줄 오른쪽 칸에 별칭 입력칸 넣음 -> 최종적으로는 [별칭(선택)] [ ](별칭 입력칸) 이렇게 됨

        // 13-3. 하단 버튼 영역
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // 취소,연결 버튼 담는 패널임(FlowLayout.CENTER는 중앙 정렬, 버튼 가로 간격, 세로 간격)
        JButton cancelButton = new JButton("취소"); // 취소 버튼 생성
        JButton connectButton = new JButton("연결"); // 연결 버튼 생성

        cancelButton.addActionListener(e -> dialog.dispose()); // 취소 버튼 클릭 시 동작을 설정함(이벤트가 발생하면(e는 버튼이 눌렀다는 정보를 담은 변수임) -> 창 닫기)

        buttonPanel.add(cancelButton); // 하단 패널 버튼에 취소 버튼 추가
        buttonPanel.add(connectButton); // 하단 패널 버튼에 연결 버튼 추가 (현재는 기본 세팅값이라 연결 버튼에 대한 동작이 없음)

        // 13-4. JDialog 조립
        dialog.add(formPanel, BorderLayout.CENTER); // 입력 폼 영역을 팝업 창의 가운데에 넣음
        dialog.add(buttonPanel, BorderLayout.SOUTH); // 취소,연결 버튼 영역을 팝업 창의 아래에 넣음

        dialog.setLocationRelativeTo(parentFrame); // 팝업 창을 부모 창인 parentFrame 가운데 띄움
        dialog.setVisible(true); //팝업 창을 실제로 화면에 보이게 함(이게 실행되야 JDialog가 화면에 뜸)
    }

    // 14. createPcCard 메서드 (Pc카드 하나를 만들어 돌려주는 메서드임)
    private static JPanel createPcCard(String name, boolean isOnline, String subText) { // Pc카드 하나를 만들어 돌려주는 메서드이고 매개변수() 의미는(PC이름, 온라인 여부, 아래 설명 문구)
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 3)); // 카드 하나를 JPanel로 만들고, 내부를 2행 1열로 나눔(제목 줄 + 설명 줄, 한 열, 가로 간격, 제목,설명 사이 세로 간격)
        card.setBackground(Color.WHITE); // 카드의 배경색을 흰색으로 설정함
        card.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스를 카드 위에 올렸을 때 커서를 손가락 모양으로 바꿈 (디자인 용임)

        // 14-1. 카드 테두리 설정하는 부분임(왼쪽 리스트에서 pc목록 나열하는 부분 설정 코드)
        card.setBorder(BorderFactory.createCompoundBorder( // createCompoundBorder()는 두 개의 테두리를 합치는 역할을 함 (그래서 PC목록이 하나로 이어진 리스트로 보임)
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 224, 224)), // 카드의 특정 방향에 선을 그림(위쪽 선,왼,아래,오, 선 색깔 -> 아래쪽만 얇은 구분선으로 카드들이 붙을 때 구분)
                BorderFactory.createEmptyBorder(12, 15, 12, 15) // 카드 안쪽 여백을 설정함(위 여백,왼,아래,오 -> 카드 모서리에 붙지 않게 하는 역할)
        ));

        // 14-2. 온라인/오프라인 상태에 따른 초록색/빨간색 점 만들기(HTML 문법을 활용하여 아이콘 효과를 냄), Swing의 JLabel은 간단한 HTML을 지원해서 글자 색이나 굵기를 줄 수 있음
        String dotColor = isOnline ? "green" : "red"; // 온라인 상태에 따라서 점 색을 정함 (isOnline이 true라면 green색 false라면 red색)
        JLabel titleLabel = new JLabel("<html><font color='" + dotColor + "'>●</font> <b>" + name + "</b></html>"); // 카드 제목 줄을 만듬(화면에서 예를들면 "초록색 점 PC-ONLINE" 처럼 보임)
        titleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13)); // 제목 라벨의 글꼴 설정 (맑은 고딕, 일반 굵기, 크기 13)

        // 14-3. 하단 서브 텍스트 (CPU 사양이나 연결 안내 문구 등)
        JLabel subLabel = new JLabel(subText); // 카드 아래쪽에 설명 문구를 만듬 ( ex) CPU 36% RAM 25% 또는 클릭하여 다시 연결)
        subLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11)); // 설명 문구의 폰트를 설정함 (제목 보다는 작게 보이게 해서 크기 11로)
        subLabel.setForeground(Color.GRAY); // 글자색을 약간 흐린 회색으로 변경함

        // 14-4. 카드 패널에 차례대로 조립함
        card.add(titleLabel); // 카드 첫 번째 줄에 제목 라벨 넣음
        card.add(subLabel); // 카드 두 번째 줄에 설명 라벨 넣음(GridLayout(2, 1)이니깐 순서대로 위,아래 배치가 됨)

        return card; // 완성된 PC 카드 패널을 반환함(이러면 위에 cardContainer.add(createPcCard(...)); 처럼 바로 목록에 추가가 가능함)
    }
    // 15. createOnlineDetailCard 메서드 (이 메서드가 만드는 화면은 오른쪽 패널의 ONLINE_VIEW에 들어감) -> 지금 이렇게 만든거는 피그마 예시임
    private static JPanel createOnlineDetailCard() { // 온라인 PC 상세 화면 하나를 만들어서 반환하는 역할함
        JPanel panel = new JPanel(new BorderLayout()); // 온라인 상세 화면 전체를 담을 가장 바깥 패널을 만듬(BorderLayout을 사용해서  NORTH  → 상단 제목 영역, CENTER → 상세 정보 본문 영역 이런식으로 나눌 수 있음)
        panel.setBackground(Color.WHITE); // 온라인 상세 화면 전체 배경을 흰색으로 설정함
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // 전체 패널의 안쪽 여백을 설정함(위,왼,아래,오)

        // 15-2. 상단 타이틀 구역
        JPanel headerPanel = new JPanel(new BorderLayout()); // 오른쪽 상세 화면의 상단 제목 영역을 만듬(이 안에 왼쪽은 PC이름, 오른쪽에 삭제 버튼 들어감)
        headerPanel.setBackground(Color.WHITE); // 상단 제목 영역 배경을 흰색으로 설정함
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); // 상단 제목 영역 아래쪽에만 연한 회색 선 긋기(아래만 1로 연한 회색 선 그음)
        headerPanel.setPreferredSize(new Dimension(0, 35)); // 상단 제목 영역의 높이를 35 정도로 설정함 (가로, 세로)

        JLabel mainTitle = new JLabel("<html><font color='green'>●</font> <b>PC-ONLINE</b> <font color='gray' size='3'>(192.168.0.2)</font></html>"); // 상단 제목 라벨을 만드는 코드임(JLabel으로 HTML 태그 사용하여 색상, 굵기 넣음(위에서 설명했었음))
        mainTitle.setFont(new Font("맑은 고딕", Font.PLAIN, 15)); // 제목 라벨의 기본 폰트를 설정함
        JButton deleteButton = new JButton("삭제"); // 삭제 버튼을 만드는 코드임
        deleteButton.setMargin(new Insets(2, 10, 2, 10)); // 삭제 버튼 안쪽 여백을 조절함(위,왼,아래,오)

        headerPanel.add(mainTitle, BorderLayout.WEST); // 상단 제목 라벨을 headerPanel의 왼쪽에 넣음
        headerPanel.add(deleteButton, BorderLayout.EAST); // 삭제 버튼을 headerPanel의 오른쪽에 넣음

        // 15-3. BoxLayout(중앙 본문 구역)
        JPanel contentPanel = new JPanel(); // 상세 정보 내용 담을 본문 패널을 만듦(안에 시스템 정보, 메모리 사용량, 원격 실행, 시스템 제어가 들어감)
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // 본문 내용을 위에서 아래로 배치함
        contentPanel.setBackground(Color.WHITE); // 본문 영역 배경색을 흰색으로 설정함
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 5, 10, 5)); // 본문 패널 안쪽 여백을 설정함(위,왼,아래,오)

        // 1) 시스템 정보 섹션
        JLabel infoTitle = new JLabel("시스템 정보"); // 시스템 정보라는 제목 라벨을 만듦
        infoTitle.setFont(new Font("맑은 고딕", Font.BOLD, 13)); // 제목을 굵게 표시함
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT); // BoxLayout 안에서 이 라벨을 왼쪽 정렬함 (setAlignmentX()는 BoxLayout 안에서 가로 방향 정렬 기준을 정함)
        contentPanel.add(infoTitle); // 본문 패널에 시스템 정보 제목을 추가함
        contentPanel.add(Box.createVerticalStrut(10)); // 세로 빈 공간 10px을 추가함(제목과 아래 표 사이를 조금 띄움)

        JPanel sysGrid = new JPanel(new GridLayout(4, 2, 0, 8)); // 시스템 정보를 표처럼 보여줄 패널임(4행,2열,가로 간격x,세로 간격 8)
        sysGrid.setBackground(Color.WHITE); // 표 영역 배경을 흰색으로 설정함
        sysGrid.setAlignmentX(Component.LEFT_ALIGNMENT); // BoxLayout 안에서 표 전체를 왼쪽 정렬함

        sysGrid.add(new JLabel("OS")); // 첫 번째 줄에 OS 항목을 추가함
        sysGrid.add(new JLabel("Microsoft Windows 11 Education", SwingConstants.RIGHT));
        sysGrid.add(new JLabel("CPU")); // 두 번째 줄에 CPU 정보를 추가함
        sysGrid.add(new JLabel("AMD Ryzen 5600 with Radeon Graphics", SwingConstants.RIGHT));
        sysGrid.add(new JLabel("설치된 RAM")); // 세 번째 줄에 RAM 정보를 추가함
        sysGrid.add(new JLabel("16834 MB", SwingConstants.RIGHT));
        sysGrid.add(new JLabel("저장소 용량")); // 네 번째 줄에 저장소 정보를 추가함
        sysGrid.add(new JLabel("2개 디스크, 3049 GB", SwingConstants.RIGHT));
        contentPanel.add(sysGrid); // 완성된 시스템 정보 표를 본문 패널에 추가함
        contentPanel.add(Box.createVerticalStrut(25)); // 시스템 정보 표와 다음 메모리 사용량 영역 사이에 25px 빈 공간을 추가함

        // 2) 메모리 사용량 섹션(메모리 사용량, 4208 / 16834 MB 있는 라인)
        JPanel memHeader = new JPanel(new BorderLayout()); // 메모리 사용량 제목과 숫자 값을 담을 패널을 만듦(왼쪽은 메모리 사용량, 오른쪽은 4208 / 16384 MB가 들어감)
        memHeader.setBackground(Color.WHITE); // 배경색을 흰색으로 설정함
        memHeader.setAlignmentX(Component.LEFT_ALIGNMENT); // BoxLayout 안에서 왼쪽 정렬함
        memHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20)); // 메모리 제목 줄의 최대 높이를 20으로 제한함
        memHeader.add(new JLabel("메모리 사용량"), BorderLayout.WEST); // 왼쪽에 메모리 사용량 라벨을 넣음
        memHeader.add(new JLabel("4208 / 16834 MB"), BorderLayout.EAST); // 오른쪽에 메모리 사용량 숫자를 넣음
        contentPanel.add(memHeader); // 본문 패널에 메모리 제목 줄을 추가함
        contentPanel.add(Box.createVerticalStrut(10)); // 메모리 제목 줄과 게이지 바 사이에 10px 간격을 줌

        // 2-2) 메모리 사용량 게이지 바 디자인
        JProgressBar memBar = new JProgressBar(0, 100); // 메모리 사용량 게이지 바를 만드는 코드임(0은 최소값, 100은 최대값, 0~100% 진행률 바임)
        memBar.setValue(25); // 현재 값을 25로 설정함(25%만큼 차오른 상태로 보임)
        memBar.setStringPainted(true); // 게이지 바 안에 숫자 문자열을 표시하게 함(25%로 보임, 만약 false라면 게이지 바 안에 문자 표시 안나옴)
        memBar.setBackground(new Color(224, 224, 224)); // 게이지 바에서 비어 있는 부분 색을 회색으로 설정함
        memBar.setForeground(new Color(153, 204, 220)); // 게이지 바에서 채워진 부분 색을 연파랑색으로 설정함
        memBar.setBorderPainted(false); // 기본 입체 테두리를 제거(이러면 더 평평한 디자인처럼 보임)
        memBar.setAlignmentX(Component.LEFT_ALIGNMENT); // BoxLayout 안에서 왼쪽 정렬
        memBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20)); // 게이지 바 최대 높이를 20으로 고정함 (여기서 setMaximumSize는 최대 허용 크기를 의미함)
        contentPanel.add(memBar); // 본문 패널에 게이지 바를 추가함
        contentPanel.add(Box.createVerticalStrut(35)); // 게이지 바와 아래 원격 실행/시스템 제어 영역 사이에 35px 공간을 설정함 (createVerticalStrut()는 세로 방향 빈 공간을 만듬)

        // 3) 원격 실행 & 시스템 제어 섹션
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 20, 0)); // 아래쪽에 들어갈 원격 실행 영역과 시스템 제어 영역을 담는 패널임(1행,2열,가로 간격20,세로 간격x)
        bottomRow.setBackground(Color.WHITE); // bottomRow 배경색을 흰색으로 설정함
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT); // BoxLayout 안에서 bottomRow를 왼쪽 정렬함
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // bottomRow의 최대 높이를 60으로 고정함(Integer.MAX_VALUE는 가로는 가능한 만큼 넓게, 세로 높이는 최대 60)

        // 3-2) 원격 실행 영역
        JPanel remotePanel = new JPanel(new BorderLayout()); // 왼쪽의 원격 실행 영역을 만드는 패널임 (안에는 위쪽 제목과 가운데 체크박스가 들어감)
        remotePanel.setBackground(Color.WHITE); // 원격 실행 영역의 배경을 흰색으로 설정함
        JLabel remoteTitle = new JLabel("원격 실행"); // 원격 실행이라는 제목 라벨을 만듦
        remoteTitle.setFont(new Font("맑은 고딕", Font.BOLD, 13)); // 원격 실행 제목을 굵게 설정함 (맑은 고딕, 폰트, 글자 크기)
        remotePanel.add(remoteTitle, BorderLayout.NORTH); // 원격 실행 제목을 remotePanel의 위쪽에 넣음

        JCheckBox vncCheck = new JCheckBox("VNC Server"); // VNC Server라는 체크박스를 만듦 (여기서 체크박스는 사용자가 선택/해제할 수 있는 컴포넌트임)
        vncCheck.setBackground(Color.WHITE); // 체크박스 배경색을 흰색으로 설정함
        vncCheck.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // 체크박스 주변 위쪽 여백 추가(위,왼,아래,오)
        remotePanel.add(vncCheck, BorderLayout.CENTER); // 체크박스를 remotePanel의 가운데 영역에 넣음

        // 3-3)시스템 제어 영역
        JPanel controlPanel = new JPanel(new BorderLayout()); // 오른쪽의 시스템 제어 영역을 만드는 패널임(안에는 제목과 실행 컨트롤이 들어감)
        controlPanel.setBackground(Color.WHITE); // 시스템 제어 영역 배경색을 흰색으로 설정함
        JLabel controlTitle = new JLabel("시스템 제어"); // 시스템 제어라는 제목 라벨을 만듦
        controlTitle.setFont(new Font("맑은 고딕", Font.BOLD, 13)); // 제목 글자를 굵게 설정함 (맑은 고딕, 폰트, 글자 크기)
        controlPanel.add(controlTitle, BorderLayout.NORTH); // 시스템 제어 제목을 controlPanel의 위쪽에 넣음

        // controlAction 패널설정
        JPanel controlAction = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20)); // 콤보박스와 실행 버튼을 담을 작은 패널임(왼쪽 정렬, 컴포넌트 사이 가로 간격, 세로 간격)
        controlAction.setBackground(Color.WHITE); // 실행 컨트롤 영역 배경색을 흰색으로 설정함

        String[] actions = {"시스템 종료", "다시 시작", "컴퓨터 잠금"}; // 콤보박스에 들어갈 선택 항목들을 배열로 설정함(드롭 다운을 누르면 시스템 종료, 다시 시작, 컴퓨터 잠금 이렇게 3개가 보임)
        JComboBox<String> actionCombo = new JComboBox<>(actions); // 드롭다운 선택 박스를 만들기(괄호 안의 actions 배열이 선택 항목으로 들어감 -> [ 시스템 종료 ▼ ] 처럼 보임)
        actionCombo.setBackground(Color.WHITE); // 콤보박스 배경색을 흰색으로 설정함
        JButton runButton = new JButton("실행"); // 실행 버튼을 만듦
        runButton.setMargin(new Insets(2, 10, 2, 10)); // 실행 버튼 안쪽 여백을 설정함(위,왼,아래,오)

        controlAction.add(actionCombo); // controlAction 패널에 콤보박스를 추가함
        controlAction.add(Box.createHorizontalStrut(10)); // 콤보박스와 실행 버튼 사이에 가로 빈 공간 10px 설정
        controlAction.add(runButton); // controlAction 패널에 실행 버튼을 추가함
        controlPanel.add(controlAction, BorderLayout.CENTER); // 콤보박스와 실행 버튼이 들어간 controlAction을 controlPanel(시스템 제어)의 중앙에 넣음

        bottomRow.add(remotePanel); // 왼쪽 칸에 원격 실행 영역을 넣음
        bottomRow.add(controlPanel); // 오른쪽 칸에 시스템 제어 영역을 넣음 -> [ remotePanel ] [ controlPanel ] 이렇게 배치가 됨
        contentPanel.add(bottomRow); // 완성된 아래쪽 영역을 본문 패널에 추가함 (온라인 상세 화면의 본문 아래쪽에 이 부분이 들어감)

        // 15-4. 최종 조립
        panel.add(headerPanel, BorderLayout.NORTH); // 전체 온라인 상세 패널의 위쪽에 제목 영역을 넣음
        panel.add(contentPanel, BorderLayout.CENTER); // 전체 온라인 상세 패널의 중앙에 본문 내용을 넣음

        return panel; // 완성된 온라인 상세 정보 화면을 반환함 (PC-ONLINE 카드 클릭 → ONLINE_VIEW 표시 → 이 메서드가 만든 panel이 오른쪽에 보임의 최종 흐름이 됨)
    }
    // 16. createOfflineDetailCard 메서드 (이 메서드가 만드는 화면은 오른쪽 패널의 OFFLINE_VIEW에 들어감, 오프라인 PC를 클릭했을 때 오른쪽 패널에 보여줄 화면을 만드는 메서드임)
    private static JPanel createOfflineDetailCard() { // 오프라인 상세 화면을 만들어서 반환하는 메서드
        JPanel panel = new JPanel(new BorderLayout()); // 오프라인 화면 전체를 담을 가장 큰 패널을 만듦( BorderLayout을 사용해서 위쪽에는 제목 영역, 가운데에는 경고 문구를 넣음)
        panel.setBackground(Color.WHITE); // 전체 배경색을 흰색으로 설정함
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 전체 패널 안쪽 여백을 설정함(위,왼,아래,오)

        //16-2. 상단 타이틀 구역
        JPanel headerPanel = new JPanel(new BorderLayout()); // 상단 제목 영역을 만듦 (안에는 왼쪽에 PC 상태/이름/IP, 오른쪽에 삭제 버튼이 들어감)
        headerPanel.setBackground(Color.WHITE); // 상단 제목 영역 배경을 흰색으로 설정함
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); // 제목 영역 아래쪽에만 선을 긋기(아래에 회색 선 1px 그음)
        headerPanel.setPreferredSize(new Dimension(0, 30)); // 상단 제목 영역의 높이를 30 정도로 설정함

        JLabel mainTitle = new JLabel("<html><font color='red'>●</font> <b>PC-OFFLINE</b> <font color='gray' size='3'>(192.168.0.3)</font></html>"); // 상단 제목 라벨을 만드는 코드임(JLabel로 HTML 태그 사용)
        mainTitle.setFont(new Font("맑은 고딕", Font.PLAIN, 16)); // 제목 라벨의 기본 글꼴을 설정함(맑은 고딕, 폰트, 글자 크기)
        JButton deleteButton = new JButton("삭제"); // 삭제 버튼을 만듦

        headerPanel.add(mainTitle, BorderLayout.WEST); // 상단 제목 라벨을 headerPanel의 왼쪽에 넣음
        headerPanel.add(deleteButton, BorderLayout.EAST); // 삭제 버튼을 headerPanel의 오른쪽에 넣음

        // 16-3. 중앙 경고 문구 (지정자리용 기본 컴포넌트를 한 개 배치)
        JLabel errorLabel = new JLabel("에이전트에 연결할 수 없습니다.", SwingConstants.CENTER); // 오프라인 상태일 때 보여줄 경고 문구 라벨을 만듦 (SwingConstants.CENTER는 라벨 안에서 글자 중앙 정렬)
        errorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14)); // 경고 문구의 글꼴을 설정함(맑은 고딕, 폰트, 글자 크기)
        errorLabel.setForeground(Color.BLACK); // 경고 문구의 글자색을 검은색으로 설정함(setForeground()는 글자색 설정임)

        // 16-4. 최종 조립
        panel.add(headerPanel, BorderLayout.NORTH); // 전체 오프라인 패널의 위쪽에 상단 제목 영역을 넣음
        panel.add(errorLabel, BorderLayout.CENTER); // 전체 오프라인 패널의 중앙에 경고 문구를 넣음

        return panel; // 완성된 오프라인 상세 화면을 반환함 (PC-OFFLINE 카드 클릭 → OFFLINE_VIEW 표시 → createOfflineDetailCard()가 만든 화면이 오른쪽에 보임의 최종 흐름이 됨)
    }
}