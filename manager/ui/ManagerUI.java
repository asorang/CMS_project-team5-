package ui;

import javax.swing.*; // 스윙의 컴포넌트들(JFrame, JPanel, JButton 등)을 가져옴

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

public class ManagerUI {
    public static void main(String[] args) {
        // 1. JFrame 생성
        JFrame frame = new JFrame("Manager");  // 객체를 선언 및 생성하고 Manager라는 이름(제목)의 프로그램 창임

        // 2. contentPanel 생성
        JPanel contentPanel = new JPanel(new BorderLayout()); // 객체를 선언 및 생성하고 여기서 new BorderLayout()는 이 패널안에 들어가는 요소를 왼,오,위,아래,중앙으로 배치할 수 있게하는 코드임
        contentPanel.setBackground(Color.WHITE); // contentPanel 배경색을 흰색으로 설정함

        // 3. darkHeaderPanel 생성 (상단 패널)
        JPanel darkHeaderPanel = new JPanel(); // 객체를 선언 및 생성하고 상단의 어두운 회색바를 만들기 위한 패널임
        darkHeaderPanel.setBackground(new Color(100, 100, 100)); // 상단 헤더 배경색 어두운 색으로 설정함
        darkHeaderPanel.setPreferredSize(new Dimension(0, 25)); // 상단 헤더의 크기를 설정(가로 크기,세로 크기)

        // 4. bodyPanel 생성 (몸통 패널, 위,가운데로 2개의 공간을 나눔 위는 +버튼 있는 줄, 가운데는 왼쪽 목록과 오른쪽 상세정보를 담은 큰 영역)
        JPanel bodyPanel = new JPanel(new BorderLayout()); // 객체를 선언 및 생성하고 헤더 아래쪽 전체 영역을 만드는 패널임(이 안에는 위쪽:toolbarPanel,가운데:splitPanel이 들어감)
        bodyPanel.setBackground(Color.WHITE); // 이 몸통 본문 영역의 배경색을 흰색으로 설정함

        // 5. toolbarPanel 생성
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)); // +버튼이 들어가는 툴바 영역 만듬(FlowLayout.left: 왼쪽 정렬, 15:컴포넌트 사이 가로 간격, 10:위아래(세로)간격)
        toolbarPanel.setBackground(Color.WHITE); // 툴바 영역색을 흰색으로 설정함

        // 6. +버튼 생성
        JButton addButton = new JButton("+"); // +라고 적힌 버튼을 만듬
        addButton.setFont(new Font("Monospaced", Font.BOLD, 22)); // 버튼 안의 + 글자 스타일을 설정하는 코드(고정폭 글꼴, 굵게, 글자 크기)
        addButton.setPreferredSize(new Dimension(35, 35)); // 버튼의 크기 설정(가로, 세로)
        addButton.setFocusPainted(false); // 버튼 클릭 시 생기는 표시선 제거(디자인 용임)
        addButton.setBackground(new Color(224, 224, 224)); // 버튼의 배경색을 연한 회색으로 설정함
        addButton.setBorder(BorderFactory.createEmptyBorder()); // 버튼의 기본 테두리를 없앰(createEmptyBorder()는 선이 없는 빈 테두리라 보면 됨
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스를 버튼 위에 올렸을 때 커서를 손가락 모양으로 바꿈(디자인 용임)

        toolbarPanel.add(addButton); // 만든 +버튼을 toolbarPanel안에 넣음, 이 과정을 넣어야 툴바 영역에 버튼이 보임

        // 7. splitPanel 생성
        JPanel splitPanel = new JPanel(new BorderLayout(15, 0)); // 객체를 선언 및 생성하고 몸통에서 왼쪽 목록과 오른쪽 상세 패널을 나란하게 담는 패널임(15:가로 간격,0:세로 간격->왼,오 중간 간격이 15)
        splitPanel.setBackground(Color.WHITE); // 이 패널의 배경색을 흰색으로 설정함
        splitPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15)); // splitPanel 안쪽 여백을 설정하는 코드(위 여백:0,왼:15,아래:15,오:15)

        // 8. leftPanel 생성
        JPanel leftPanel = new JPanel(new BorderLayout()); // 객체를 선언 및 생성하고 왼쪽 PC목록 영역 만드는 코드임(처음엔 등록된 PC가 없어 안내문구만 있음, 왼쪽 패널은 BorderLayout()방식으로 배치가 됨)
        leftPanel.setBackground(new Color(224, 224, 224)); // 왼쪽 패널 배경색을 연한 회색으로 설정함
        leftPanel.setPreferredSize(new Dimension(250, 0)); // 왼쪽 패널의 크기를 설정함(가로,세로(세로는 레이아웃에 맡김))

        // 9. emptyText 생성
        JTextArea emptyText = new JTextArea("등록된 PC가 없습니다.\n상단의 + 버튼을 눌러 추가하세요."); // 객체를 선언 및 생성하고 왼쪽 패널에 표시할 안내 문구를 만드는 코드임(JTextArea는 여러줄 텍스트 생성)
        emptyText.setFont(new Font("맑은 고딕", Font.PLAIN, 12)); // 안내 문구 폰트 설정함(글꼴 이름, 일반 굵기, 글자 크기)
        emptyText.setBackground(new Color(224, 224, 224)); // 텍스트 영역 배경색을 연한 회색으로 설정함(왼쪽 패널과 같음 -> 자연스럽게 보기 위함)
        emptyText.setEditable(false); // 사용자가 안내 문구 수정하지 못하게 하는 코드임
        emptyText.setMargin(new Insets(20, 15, 10, 10)); // 텍스트 영역 내부 여백 설정(위 여백:20, 왼:15, 아래 10: 오:10)
        leftPanel.add(emptyText, BorderLayout.NORTH); // 안내 문구를 왼쪽 패널 위쪽에 넣는 코드임(leftPanel은 BorderLayout()을 사용 중이기 때문에 BorderLayout.NORTH에 넣으면 위쪽에 붙음)

        // 10. rightPanel 생성
        JPanel rightPanel = new JPanel(); // 객체를 선언 및 생성하고 오른쪽 상세 정보 영역을 만드는 코드임(현재는 빈 흰색 박스 상태)
        rightPanel.setBackground(Color.WHITE); // 오른쪽 패널 배경색을 흰색으로 설정함
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // 오른쪽 패널에 연한 회색 테두리를 넣는 코드임(테두리 색, 테두리 두께)(바깥 테두리 선임)

        // 11. 만든 패널들 결합
        splitPanel.add(leftPanel, BorderLayout.WEST); // 왼쪽 목록 패널을 splitPanel의 왼쪽 영역에 넣음
        splitPanel.add(rightPanel, BorderLayout.CENTER); // 오른쪽 상세 패널을 splitPanel의 중앙 영역에 넣음

        bodyPanel.add(toolbarPanel, BorderLayout.NORTH); // +버튼이 있는 툴바 영역을 bodyPanel 위쪽에 넣음
        bodyPanel.add(splitPanel, BorderLayout.CENTER); // 왼/오로 나누어진 영역을 bodyPanel 중앙에 넣음

        contentPanel.add(darkHeaderPanel, BorderLayout.NORTH); // 상단 어두운 헤더를 contentPanel 위쪽에 넣음
        contentPanel.add(bodyPanel, BorderLayout.CENTER); // 본문 전체 영역을 contentPanel 중앙에 넣음

        frame.add(contentPanel); // 완성된 전체 UI 패널을 창에 넣는 코드임

        // 12. 프레임 설정
        frame.setSize(900, 600); // 창 크기 가로:900 세로:600으로 설정함
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창에서 X버튼을 눌렀을 때 프로그램이 완전히 종료됨
        frame.setLocationRelativeTo(null); // 창이 실행될 때 화면 가운데 뜨도록 설정함(null은 특정 컴포넌트 기준x, 화면 기존 중앙이라고 보면 됨)
        frame.setVisible(true); // 창을 실제로 화면에 보이게 함 (지금까지 만든 UI 나타남)
    }
}