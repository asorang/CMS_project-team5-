// 1. 라이브러리 부분
import javax.swing.*; // Swing에서 JFrame, JPanel, JLabel, JTextField, JButton 같은 UI 컴포넌트들을 사용하기 위해 가져옴
import javax.swing.border.EmptyBorder; // 패널 안쪽에서 빈 여백을 만들기 위해서 EmptyBorder라는 클래스를 가져옴
import java.awt.*; // 이건 색상, 폰트, 레이아웃, 크기 등에 필요한 AWT 클래스들을 가져옴(컴포넌트를 쓰기위해 가져오는거 X)

// 2. 여기서 클래스 선언이랑 맴버 변수 선언
public class AgentSettingsUI extends JFrame { // AgentSettingsUI라는 클래스를 만든 다음 JFrame을 상속 받음(여기서 JFrame은  자바swing에서 하나의 창임)

    private JLabel statusLabel; // 에이전트의 현재 상태 문구를 보여줄 라벨 변수를 선언함(글자를 화면에 표시하는 컴포넌트임)
    private JTextField portField; // 포트 번호를 입력받을 텍스트 필드 변수로써 선언함(JTextField는 사용자가 한줄짜리 텍스트를 입력할 수 있는 입력칸임)
    private JPasswordField passwordField; // 암호를 입력받을 비밀번호 입력칸 변수를 선언함(JPasswordField는 앞의 JTextField와 비슷하지만, 입력한 내용이 그대로 보이지 않고 보통 ● 또는 *처럼 가려짐)

    // 바로가기 이름 입력칸 배열과 바로가기 경로 입력칸 배열을 선언함(바로가기가 3개라서 배열을 사용하였고, 바로가기 1, 2, 3의 이름과 경로 입력칸을 배열로 관리함)
    private JTextField[] shortcutNameFields; // 바로가기 이름 입력칸
    private JTextField[] shortcutPathFields; // 바로가기 경로 입력칸

    // 취소 버튼과 저장 버튼 변수를 선언함(여기서 JButton은 사용자가 클릭할 수 있는 버튼임, 버튼 객체는 뒤에서 만들고, 외부에서 이벤트 연결 변수로 준비하면 될듯)
    private JButton cancelButton; // 취소 버튼
    private JButton saveButton; // 저장 버튼

    // 백엔드에서 전달받은 초기 설정값을 저장하는 변수들 (원본 설정값 저장 변수 부분)
    private String originalPort = ""; // 처음 불러온 포트 번호를 저장함(처음 상태의 포트값을 기억해두는 역할이라고 보면됨)
    private String originalPassword = ""; // 처음 불러온 암호 값을 저장함(초기 암호 값을 따로 저장하여 변경 여부를 확인할 수 있게함)
    private String[] originalNames = new String[3]; // 처음 불러온 바로가기 이름 3개를 저장할 문자열 배열을 만듬(바로가기 1,2,3의 초기 이름 값을 기억해두는 역할임)
    private String[] originalPaths = new String[3]; // 처음 불러온 바로가기 경로 3개를 저장할 문자열 배열을 만듬(바로가기 1,2,3의 초기 실행 파일 경로를 기억해두는 역할임)

    // 3. 생성자 부분
    public AgentSettingsUI() { // AgentSettingsUI 객체가 생성될 때 자동으로 실행되는 생성자임(에이전트 설정 창에서 기본 설정을 시작하는 부분임)
        // 이쪽 try,catch 부분 쓴 것은 설명이 길지만 한마디로 그냥 디자인쪽 코드라 생각하면 됨
        // 현재 OS(윈도우 등)의 고유 디자인 테마를 적용(try,catch의 사용 이유는 LookAndFeel 적용 과정에서 예외가 발생할 수 있기 때문에 실패 시 프로그램이 튕기는 것을 막기 위한 안전장치라 보면됨)
        try {
            // UIManager.setLookAndFeel()은 Swing 컴포넌트의 기본 모양을 설정하고,()안의 UIManager.getSystemLookAndFeelClassName()은 현재 운영체제의 기본 스타일 이름을 가져옴(예를들면 Windows에서 실행하면 Windows 느낌의 버튼과 입력칸에 가까워짐)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { // try문에서 오류 발생 시 처리하는 부분임(그 오류를 e라는 이름으로 받아서 처리한다는 뜻임, Exception은 자바에서 예외,오류의 의미임)
            e.printStackTrace(); // 발생한 오류의 자세한 내용을 콘솔에 출력함(어디서, 어떤 이유로 발생했는지 보여줌)
        }

        setTitle("에이전트 설정"); // 창 제목을 "에이전트 설정" 으로 설정함(여기서 setTitle()은 JFrame의 제목 표시줄에 들어갈 문구를 지정하는 메서드임)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창의 X버튼을 누르면 프로그램이 종료되도록 설정함(즉 JFrame.EXIT_ON_CLOSE는 창을 닫으면 자바 프로그램 자체를 종료한다는 의미)

        setSize(420, 560); // 창 크기를 가로 420, 세로 560으로 설정함
        setLocationRelativeTo(null); // 창을 화면 중앙에 띄우는 역할을 함(여기서 null은 특정 컴포넌트를 기준x, 전체 화면 기준으로 중앙 배치 한다는 뜻임)
        setResizable(false); // 사용자가 혹시나 창 크기를 조절하지 못하게 함, 창 크기 고정(창 안에 여러 입력칸, 버튼칸 등이 정해져있기에 건들면 UI가 이상해져서 이렇게 설정함)
        getContentPane().setBackground(Color.WHITE); // JFrame 기본 영역 배경색을 흰색으로 설정함

        initUI(); // 실제 UI 구성 요소를 만드는 initUI() 메서드를 호출(여기서 상태 라벨, 포트 입력칸, 암호 입력칸 등 실제로 생성하고 배치함)
    }

    // 4. initUI 메서드 시작
    private void initUI() { // 설정 창 내부 UI를 구성하는 메서드임(private이기에 이 클래스 안에서만 호출 가능함, 컴포넌트들을 만들고, 배치하는 작업하는 곳임)
        JPanel mainPanel = new JPanel(); // 설정 창 내용을 담을 메인 패널을 생성함(즉, JPanel을 여러 UI 요소를 담을 수 있는 컨테이너라 보면됨)
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // 메인 패널의 요소들을 위에서 아래 방향으로 배치하도록 설정함(BoxLayout.Y_AXIS는 세로 방향 배치를 의미함)
        mainPanel.setBackground(Color.WHITE); // 메인 패널의 배경색을 흰색으로 설정함(JFame의 배경색인 흰색을 똑같이 맞춰줌)
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30)); // 메인 패널 안쪽에 위25, 왼30, 아래25, 오른 30의 여백을 줌

        // 4-1. 상태 표시 라벨
        statusLabel = new JLabel("에이전트의 IP 상태를 확인하는 중입니다..."); // 상태 표시 라벵을 생성하고 초기 문구를 "에이전트의 IP 상태를 확인하는 중입니다..."로 설정함
        statusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15)); // 상태 라벨의 폰트 설정함(맑은 고딕, 굵게, 크기 15로 설정함)
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // 상태 라벨을 왼쪽 정렬함(상태 문구가 입력 칸들과 같은 왼쪽 기준선에 맞춰 보이게 함)
        mainPanel.add(statusLabel); // 상태 라벨을 메인 패널에 추가함(설정 창 맨 위에 상태 라벨이 표시되게 함)
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25))); // 상태 레벨 창 아래에 세로 25만큼의 빈 공간을 추가함(Box.createRigidArea()는 고정된 크기의 빈 공간을 만듬, 상태 라벨과 포트 입력 영역 사이에 적당한 간격을 만든다 생각하면 됨)

        // 4-2. 포트 및 암호 입력 영역
        mainPanel.add(createInputRow("포트", portField = new JTextField())); // 포트 입력 행을 만들어 메인 패널에 추가함(createInputRow()는 왼쪽 라벨과 오른쪽 입력칸 한 줄로 만들어주는 역할을 함)
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15))); // 포트 입력칸과 암호 입력칸 사이에 세로 15만큼의 간격을 줌
        mainPanel.add(createInputRow("암호", passwordField = new JPasswordField())); // 암호 입력 행을 만들어 메인 패널에 추가함(JPasswordField는 비밀번호 입력 전용 필드라 입력 내용이 화면에 그대로 노출 x)
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35))); // 암호 입력 영역 아래에 세로 35만큼 빈 공간을 추가함

        // 4-3. 바로가기 설정 영역
        // 바로가기 이름 입력칸 배열과 바로가기 경로 입력칸 배열을 각각 3칸짜리로 생성함(바로가기 설정이 3개 필요하기 때문에 이렇게 설정함)
        shortcutNameFields = new JTextField[3]; // 바로가기 이름 입력칸 배열
        shortcutPathFields = new JTextField[3]; // 바로가기 경로 입력칸 배열

        // 5. 바로가기 입력 영역 반복문
        for (int i = 0; i < 3; i++) { // 바로가기 입력 영역을 3번 반복해서 만듬(i는 0부터 시작해 2까지 증가하고 총 3번 반복되고 바로가기 1, 2, 3 입력 영역을 같은 구조로 자동 생성함)
            JLabel shortcutLabel = new JLabel("바로가기 " + (i + 1)); // 바로가기 1, 바로가기 2, 바로가기 3 라벨을 만듬(각 바로가기 입력칸 위에 제목 라벨을 표시함, 사용자에게 1번부터 보여주는게 자연스럽기에 (i + 1)사용)
            shortcutLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14)); // 바로가기 라벨의 폰트를 설정함(맑은 고딕, 굵게, 크기14)
            shortcutLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // 바로가기 라벨을 왼쪽 정렬함(바로가기 제목들이 왼쪽 기준선에 맞춰 정렬함)
            mainPanel.add(shortcutLabel); // 바로가기 라벨을 메인 패널에 추가함(화면에 바로가기 1 같은 제목이 표시됨)
            mainPanel.add(Box.createRigidArea(new Dimension(0, 8))); // 바로가기 라벨과 입력칸 사이에 세로 8만큼 간격을 줌(디자인 역할)

            JPanel shortcutRow = new JPanel(new BorderLayout(15, 0)); // 바로가기 이름 입력칸과 경로 입력칸을 담을 가로 방향 패널을 만듬(왼,오 입력칸 사이 15만큼 간격이 생김)
            shortcutRow.setBackground(Color.WHITE); // 바로가기 입력 행 패널의 배경색을 흰색으로 설정함
            shortcutRow.setAlignmentX(Component.LEFT_ALIGNMENT); // 바로가기 입력 행 전체를 왼쪽 정렬을 함(이러면 바로가기 입력칸들이 다른 입력 영역과 같이 왼쪽 기준에 맞춰짐)
            shortcutRow.setMaximumSize(new Dimension(400, 35)); // 바로가기 입력 행의 최대 크기를 가로 400, 세로 35로 제한함(즉 바로가기 입력칸 높이가 일정하게 보이게 함)

            shortcutNameFields[i] = new JTextField(); // 바로가기 이름을 입력할 텍스트 필드를 만들고 배열의 i번째 위치에 저장함(바로가기 이름을 입력할 왼쪽 입력칸을 만듬)
            shortcutNameFields[i].setPreferredSize(new Dimension(110, 35)); // 바로가기 이름 입력칸의 기본 크기를 가로 110, 세로 35로 설정함

            shortcutPathFields[i] = new JTextField(); // 바로가기 경로를 입력할 텍스트 필드를 만들고 배열의 i번째 위치에 저장함(바로가기 경로를 입력할 오른쪽 입력칸을 만듬)

            shortcutRow.add(shortcutNameFields[i], BorderLayout.WEST); // 바로가기 이름 입력칸을 입력 행의 왼쪽 영역에 배치함(BorderLayout.WEST는 왼쪽에 이름 입력칸이 보이게 함)
            shortcutRow.add(shortcutPathFields[i], BorderLayout.CENTER); // 바로가기 경로 입력칸을 입력 행의 가운데 영역에 배치함(BorderLayout.CENTER는 오른쪽 영역에 경로 입력칸이 보이게 함)

            mainPanel.add(shortcutRow); // 완성된 바로가기 입력 행을 메인 패널에 추가함
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // 각 바로가기 입력 행 아래에 세로 20만큼 간격을 줌(각 항목들 띄우는 디자인 역할)
        }

        // 6. 하단 버튼 영역
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // 취소/저장 버튼을 담을 패널을 만들고, 버튼들을 가운데 정렬함(FlowLayout.CENTER는 버튼들을 가운데 배치,20은 버튼 사이 가로 간격, 10은 위아래 세로 여백임)
        buttonPanel.setBackground(Color.WHITE); // 버튼 패널 배경색을 흰색으로 설정함
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 버튼 패널 자체를 왼쪽 정렬 기준으로 맞춤
        buttonPanel.setMaximumSize(new Dimension(400, 60)); // 버튼 패널 최대 크기를 가로 400, 세로 60으로 설정함

        cancelButton = new JButton("취소"); // 취소 버튼 생성(각 버튼에 들어갈 글자)
        saveButton = new JButton("저장"); // 저장 버튼 생성(각 버튼에 들어갈 글자)

        // 버튼 스타일링
        Dimension buttonSize = new Dimension(100, 38); // 버튼 크기를 가로100, 세로 38로 지정하기 위한 Dimension 객체 생성
        cancelButton.setPreferredSize(buttonSize); // 취소 버튼 기본 크기를 가로 100, 세로 38로 설정함
        saveButton.setPreferredSize(buttonSize); // 저장 버튼 기본 크기를 가로 100, 세로 38로 설정함
        cancelButton.setBackground(new Color(225, 225, 225)); // 취소 버튼 배경색을 연한 회색으로 설정함
        saveButton.setBackground(new Color(225, 225, 225)); // 저장 버튼 배경색을 연한 회색으로 설정함
        cancelButton.setFont(new Font("맑은 고딕", Font.BOLD, 14)); // 취소 버튼 글꼴을 맑은 고딕, 굵게, 크기 14로 설정함
        saveButton.setFont(new Font("맑은 고딕", Font.BOLD, 14)); // 저장 버튼 글꼴을 맑은 고딕, 굵게, 크기 14로 설정함

        buttonPanel.add(cancelButton); // 버튼 패널에 취소 버튼을 추가함
        buttonPanel.add(saveButton); // 버튼 패널에 저장 버튼을 추가함 -> 이렇게 되면 추가한 순서대로 화면에 취소, 저장 순서로 표시가 됨(또 위에서 FlowLayout.CENTER로 설정해서 두 버튼이 가운데 모여 배치됨)

        mainPanel.add(Box.createVerticalGlue()); // 중간의 남는 공간들을 밀어내기 위해서 유연하게 빈 공간을 추가함(VerticalGlue는 남는 세로 공간을 차지하는 역할을 함, 버튼 영역이 화면 아래쪽에 자연스럽게 배치되도록 밀어주는 디자인적인 역할로 보면 됨)
        mainPanel.add(buttonPanel); // 버튼 패널을 메인 패널에 추가함

        add(mainPanel); // 완성된 메인 패널을 JFrame 창에 추가함 -> 전체 패널 구성은 끝난 상태임
    }

    // 7. createInputRow 메서드 ( 포트 [입력칸], 암호 [입력칸] 같은 공통 입력 행을 만드는 작업이라고 보면됨)
    private JPanel createInputRow(String labelText, JTextField textField) { // 라벨과 입력칸이 한 줄로 배치된 입력 행 패널을 만들어서 반환하는 메서드임
        JPanel rowPanel = new JPanel(new BorderLayout(20, 0)); // 입력 행 전체를 담을 패널을 만들고, BorderLayout으로 배치함(왼쪽이 라벨 사이 가로 간격(20, 0)이 20 오른쪽이 입력칸이 들어가는 한줄 패널임)
        rowPanel.setBackground(Color.WHITE); // 입력 행 패널의 배경을 흰색으로 설정함
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 입력 행 패널을 왼쪽으로 정렬함 (포트, 암호 입력 행이 왼쪽 기준선에 맞춰 정렬됨)
        rowPanel.setMaximumSize(new Dimension(400, 35)); // 입력행의 최대 크기를 가로 400, 세로 35로 설정함

        JLabel label = new JLabel(labelText); // 입력 행 왼쪽에 표시할 라벨을 만듬(labelText는 값에 따라 라벨 글자가 달라지는데 "포트"를 넘기면 포트 라벨, "암호"를 넘기면 암호 라벨이 됨)
        label.setFont(new Font("맑은 고딕", Font.BOLD, 14)); // 이 라벨의 폰트를 맑은 고딕, 굵게, 크기 14로 설정을 함
        label.setPreferredSize(new Dimension(35, 35)); // 라벨 영역의 기본 크기를 가로 35, 세로 35로 설정함

        rowPanel.add(label, BorderLayout.WEST); // 라벨을 입력 행 패널의 왼쪽 영역에 추가함(포트, 암호 글자가 입력 행 왼쪽에 배치가 됨)
        rowPanel.add(textField, BorderLayout.CENTER); // 입력칸을 입력 행 패널의 가운데 영역에 추가함(입력칸이 라벨 오른쪽의 넓은 영역을 채움)

        return rowPanel; // 완성된 입력 행 패널을 반환함(이 반환값은 mainPanel.add(createInputRow(...))에서 사용되고 만들어진 행을 메인 화면에서 바로 추가 가능하게 됨)
    }

    // 8. setInitialData 메서드(설정 창이 처음 열릴 때 기존 설정값을 화면에 표시하려고 사용되고, 예를들면 설정 파일이나 백엔드에서 포트, 암호, 바로가기 정보를 가져와 이 메서드에 넘겨주면, 그 값들이 화면 입력칸에 들어간다고 보면됨)
    public void setInitialData(String port, String password, String[] names, String[] paths) { // 초기 포트, 암호, 바로가기 이름, 바로가기 경로 값을 UI입력칸에 넣어주는 메서드임(외부에서 받은 초기 설정 데이터를 설정 창에 적용하는 작업을 시작함)
        this.originalPort = port; // 전달받은 포트 값을 originalPort에 저장함(여기서 port는 메서드로 전달받은 값, originalPort는 클래스 안에 있는 원본 저장용 변수임) 처음 불러온 포트 값을 원본 데이터로 기억해둠
        this.originalPassword = password; // 전달받은 암호 값을  originalPassword에 저장함(나중에 암호를 바꿨는지 비교하려면 처음 암호 값을 따로 저장해야함 그래서 password 값을 originalPassword에 넣음) 처음 불러온 암호 값을 원본 데이터로 기억해둠
        this.portField.setText(port); // 포트 입력칸에 전달받은 포트 값을 표시함(여기서 portField는 화면에 보이는 포트 입력칸,setText(port)를 사용하면 입력칸 안에 port 값이 들어감, 기존 포트 설정값을 UI입력칸에 보여줌)
        this.passwordField.setText(password); // 암호 입력칸에 전달받은 암호 값을 표시함(여기서 passwordField는 JPasswordField이므로 암호 입력 전용 칸, setText(password)를 사용 시 암호 값이 입력칸에 들어가지만, 화면에서는 보통 그대로 보이지 않고 숨김 문자처럼 표시됨, 기존 암호 설정값을 실제 UI 암호 입력칸에 넣어주는 역할을 함)

        for (int i = 0; i < 3; i++) { // 바로가기 1번~3번까지 반복해 초기값을 넣음(바로가기 3개의 이름과 경로를 차례대로 처리함)
            // i번째 바로가기 이름이 null이 아니면 그 값을 저장하고, null이면 빈 문자열을 저장함(i번째 바로가기 이름의 원본 값을 저장하는 역할을 함)
            this.originalNames[i] = (names[i] != null) ? names[i] : ""; // 삼항 연산자를 사용함(의미는 names[i]가 null이 아니면 names[i]를 사용하고, names[i]가 null이면 ""를 사용함)
            this.shortcutNameFields[i].setText(this.originalNames[i]); // i번째 바로가기 이름 입력칸에 원본 이름 값을 표시함(바로 위의 originalNames[i]에 저장한 값을 실제 화면 입력칸에 넣고, 원본 이름이 있으면 그 이름 표시, 값 없으면 빈칸 표시)

            // i번째 바로가기 경로가 null이 아니면 그 값을 저장하고, null이면 빈 문자열을 저장함(i번째 바로가기 경로의 원본 값을 저장하는 역할을 함)
            this.originalPaths[i] = (paths[i] != null) ? paths[i] : ""; // 여기도 삼항 연산자 사용함(의미는 paths[i]에 실제 값이 있으면 그 값을 사용하고,값이 null이면 빈 문자열로 처리함)
            this.shortcutPathFields[i].setText(this.originalPaths[i]); // i번째 바로가기 경로 입력칸에 원본 경로 값을 표시함(바로 위의 originalPaths[i]에 저장된 값을 실제 경로 입력칸에 넣고, 경로 값이 있으면 그 경로가 표시, 값 없으면 빈칸 표시)
        }
    }

    // 9. isModified 메서드(이 메서드는 저장 버튼이나 취소 버튼을 눌렀을 때 "사용자가 설정을 바꿨는지" 확인하는데 사용함 그래서 boolean 자료형 사용함)
    public boolean isModified() { // 현재 입력값이 처음 불러온 원본 값과 달라졌는지 확인하는 메서드임(즉 설정값 변경 여부 검사하는 작업을 여기서 함)
        if (!getPort().equals(originalPort)) return true; // 현재 포트 값이 원본 포트 값과 다르면 true를 반환함(getPort()는 현재 포트 입력칸 값, originalPort는 처음 불러온 포트 값, 그리고 equals는 비교하는 메서드임) 즉, 포트 번호 변경을 확인하고 바뀌면 바로 변경된걸 알려줌
        if (!getPassword().equals(originalPassword)) return true; // 현재 암호 값이 원본 암호 값과 다르면 true를 반환함(getPassword() 현재 암호 입력칸 값, originalPassword는 처음 저장된 암호 값임) 두 값이 다르면 암호를 수정한 것 이기에 다른 값 볼 필요x -> true 반환함 (암호가 변경됐는지 확인함)

        for (int i = 0; i < 3; i++) { // 바로가기 1번~3번까지 반복해 변경 여부를 확인함(바로가기 이름,경로 3개를 차례대로 검사함)
            if (!getShortcutName(i).equals(originalNames[i])) return true; // i번째 현재 바로가기 이름이 원본 이름과 다르면 true 반환함(바로가기 이름이 수정되었는지 확인함)
            if (!getShortcutPath(i).equals(originalPaths[i])) return true; // i번째 현재 바로가기 경로가 원본 경로와 다르면 true 반환함(바로가기 실행 파일 경로가 변경 되었는지 확인함)
        }
        return false; // 모든 값을 비교해 바뀐 내용이 없다면 false 반환함(즉, 설정값이 변경되지 않았다는 결과를 반환함)
    }

    // 10. 백엔드 연동을 위한 데이터 추출 툴(Getter / Setter)
    public void setStatusIp(String ip) { // 전달받은 IP 주소를 상태 라벨에 표시하는 메서드임
        statusLabel.setText("에이전트가 " + ip + "에서 실행 중입니다."); // 처음은 "에이전트의 IP 상태를 확인하는 중입니다..."라고 표시되지만 이후 setStatusIp("192.168.0.15")를 호출하면 상태 문구가 바뀜
    }

    public String getPort() { return portField.getText(); } // 포트 입력칸에 입력된 값을 문자열로 반환함(여기서 portField.getText()는 사용자가 포트 입력칸에 적은 값을 가져옴 예를들면 사용자가 8080을 입력했다면 "8080"이라는 문자열이 반환됨) 즉, 백엔드 코드가 사용자가 입력한 포트 번호를 가져갈 수 있게함
    public String getPassword() { return new String(passwordField.getPassword()); } // 암호 입력칸에 입력된 값을 문자열로 반환함(여기서 JPasswordField는 보안상 getText()보다는 getPassword()를 사용하였고, getPassword()는 문자 배열을 반환하기에 여기서는 new String(...)으로 문자열로 변환함)

    public String getShortcutName(int index) { // index 번호에 해당하는 바로가기 이름 입력값을 반환하는 역할을 함
        if (index >= 0 && index < 3) { // if (index >= 0 && index < 3)은 배열 범위를 벗어나지 않게 검사하는 코드임(index = 0 → 바로가기 1, index = 1 → 바로가기 2... 이런식임)
            return shortcutNameFields[index].getText(); // index번째 바로가기 이름 입력칸에 들어 있는 텍스트를 그대로 반환함(즉 사용자가 입력한 바로가기 이름을 백엔드나 다른 코드에서 사용할 수 있게 반환함)
        }
        return null; // 만약 잘못된 index가 들어오면 null을 반환함
    }

    public String getShortcutPath(int index) { // index 번호에 해당하는 바로가기 경로 입력값을 반환하는 역할을 함
        if (index >= 0 && index < 3) { // 구조는 앞의 getShortcutName()과 같음
            return shortcutPathFields[index].getText(); // index번째 바로가기 경로 입력칸에 들어 있는 텍스트를 그대로 반환함(즉 사용자가 입력한 바로가기 실행 파일 경로를 반환함)
        }
        return null; // index가 잘못된 경우 null을 반환함(배열 범위 벗어난 index일 경우)
    }

    public JButton getSaveButton() { return saveButton; } // 저장 버튼 객체를 반환함(이 메서드가 있으면 다른 클래스에서 버튼 이벤트 연결 가능하게 됨)
    public JButton getCancelButton() { return cancelButton; } // 취소 버튼 객체를 반환함(이 메서드가 있으면 다른 클래스에서 버튼 이벤트 연결 가능하게 됨)

    // 11. main 실행 테스트 부분
    public static void main(String[] args) { // 이 프로그램이 실행되는 main 메서드임
        SwingUtilities.invokeLater(() -> { // Swing UI 생성 코드를 이벤트 처리 스레드에서 실행하도록 함
            AgentSettingsUI ui = new AgentSettingsUI(); // AgentSettingsUI 객체를 생성함
            ui.setStatusIp("192.168.0.15"); // 상태 라벨에 표시할 IP 주소를 192.168.0.15로 설정함(테스트 실행 시 상단 상태 문구에 실제 IP처럼 보이는 값을 표시함)
            ui.setVisible(true); // 생성한 설정 창을 화면에 보이게 함(JFrame은 객체를 생성했다고 바로 화면에 뜨지않고 반드시 setVisible(true)를 호출해야 실제로 창이 표시가 됨)
        });
    }
}