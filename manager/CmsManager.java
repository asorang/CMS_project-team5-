import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CmsManager {
    // 고정 변수 (에이전트랑 거의 동일, 다만 얘는 에이전트 프로그램 IP정보를 가지고 있겠지.....)
    static final String AGENT_IP = "127.0.0.1";
    static final int PORT = 10293;
    static final String PASSWORD = "1234";
    static final int INTERVAL = 10;

    // 소켓 및 기타 네트워크 관련 헬퍼 객체/함수
    // 이걸 왜 밖에 빼놨냐고? -> 최초 연결할 때 정의되고, 명령어 송수신 과정에서 재활용하게 됨
    static Socket socket;
    static PrintWriter out;
    static BufferedReader in;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("명령어 입력 (CONNECT / SHUTDOWN / REBOOT / LOCK)");

        while (true) {
            String cmd = scanner.nextLine().trim();
            if (cmd.isEmpty()) continue;

            switch (cmd) {
                case "CONNECT"  -> connectAgent();
                case "SHUTDOWN" -> sendCommand("SHUTDOWN");
                case "REBOOT"   -> sendCommand("REBOOT");
                case "LOCK"     -> sendCommand("LOCK");
                default         -> System.out.println("알 수 없는 명령어입니다.");
            }
        }
    }

    // 에이전트 프로그램과 연결 수립 및 시스템 정보 수신
    static void connectAgent() {
        try {
            socket = new Socket(AGENT_IP, PORT);
            socket.setSoTimeout(INTERVAL * 3000);

            // 소켓에 InputStream, OutputStream을 묶어서 추후 명령어 전송 시에도 재활용할 수 있게 함
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 인증
            JsonObject auth = new JsonObject();
            auth.addProperty("password", PASSWORD);
            out.println(auth.toString());

            String response = in.readLine();
            JsonObject authResult = JsonParser.parseString(response).getAsJsonObject();
            if (!authResult.get("auth").getAsString().equals("AUTH_OK")) {
                System.out.println("인증 실패");
                socket.close();
                return;
            }
            System.out.println("인증 성공");

            // 수신 스레드
            // 그냥 수신만 할 때는 수신루프로도 가능했지만 이제는 송신도 하기 때문에,
            // 스레드로 따로 만들어서 수신과정이 송신과정과 겹치지 않게 한다 (Non-blocking)
            new Thread(() -> {
                try {
                    String json;
                    while ((json = in.readLine()) != null) {
                        System.out.println("[수신] " + json);
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("접속 시간 초과 (연결 해제됨)");
                } catch (Exception e) {
                    System.out.println("수신 종료: " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            System.out.println("연결 실패: " + e.getMessage());
        }
    }

    // 매니저 -> 에이전트로 명령어 송신
    // Socket이 서로 연결되어 있을 때만 사용 가능 (바로 다다음줄 참조)
    static void sendCommand(String cmd) {
        if (out == null) {  // 소켓이 연결되어 있지 않으면 out에 아직 아무것도 들어가 있지 않으니까 NULL을 반환
            System.out.println("먼저 Agent와 연결해야 합니다. (CONNECT)");
            return;
        }
        JsonObject json = new JsonObject();

        // 인자로 받은 명령어를 cmd의 Payload로 삽입하여 out을 통해 소켓으로 전송하게 됨
        json.addProperty("cmd", cmd);
        out.println(json.toString());
        System.out.println("[송신] " + json);
    }
}