import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.*;
import java.util.*;

public class CmsManager {
    // 고정 변수 (연결할 포트 번호는 10293 고정)
    static final int PORT = 10293;
    static final int INTERVAL = 10;
    static final String AGENT_FILE = "agents.json";
    static Map<String, AgentConnection> agents = new HashMap<>();

    // 각 Agent의 정보를 처리하는 클래스
    static class AgentConnection {
        String alias;
        String ip;
        String pw;
        Socket socket;
        PrintWriter out;
        BufferedReader in;

        AgentConnection(String alias, String ip, String pw, Socket socket, PrintWriter out, BufferedReader in) {
            this.alias  = alias;
            this.ip = ip;
            this.pw = pw;
            this.socket = socket;
            this.out = out;
            this.in = in;
        }
    }

    public static void main(String[] args) {
        loadAgents();
        Scanner scanner = new Scanner(System.in);
        System.out.println("명령어 입력 (CONNECT / SHUTDOWN / REBOOT / LOCK)");

        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            // 명령어 뒤에 다른 것들이 붙기 때문에 입력값 시작을 대조하여 명령을 수행한다
            // 또 connectagent, sendCommand 각각 파싱이 다르기 때문에 명령어 전문을 메소드에 전달한다
            if      (input.startsWith("CONNECT"))   connectAgent(input);
            else if (input.startsWith("SHUTDOWN"))  sendCommand(input, "SHUTDOWN");
            else if (input.startsWith("REBOOT"))    sendCommand(input, "REBOOT");
            else if (input.startsWith("LOCK"))      sendCommand(input, "LOCK");
            else if (input.startsWith("EXEC"))      sendCommand(input, "EXEC");
            else System.out.println("알 수 없는 명령어: " + input);
        }
    }

    // 에이전트 프로그램과 연결 수립 및 시스템 정보 수신
    static void connectAgent(String input) {
        // scanner로 입력받은 명령어 전체를 공백 기준으로 파싱 (0은 CONNECT 명령어 구문이므로 제외)
        String[] parts = input.split(" ");
        String agentIP= parts[1]; // <-- 왜 0은 사용하지 않는가 -- 명령어가 [명령어] [인수] 로 구분되는데 어차피 명령어는 처리 대상이 아니니까...
        String agentPW= parts[2];
        String alias= parts[3];

        try {
            // 각 Agent마다 다른 Socket을 생성한다 (헬퍼 함수도 마찬가지, 소켓에 묶여 있으니까....
            Socket socket = new Socket(agentIP, PORT);
            socket.setSoTimeout(INTERVAL * 3000);
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 인증
            JsonObject auth = new JsonObject();
            auth.addProperty("password", agentPW);  // 원래는 PASSWORD 고정변수였지만 이제는 Parsing한 결과를 사용해야 함
            out.println(auth.toString());

            String response = in.readLine();
            JsonObject authResult = JsonParser.parseString(response).getAsJsonObject();
            if (!authResult.get("auth").getAsString().equals("AUTH_OK")) {
                System.out.println("인증 실패");
                socket.close();
                return;
            }
            System.out.println("인증 성공");

            // 인증이 성공한 경우 AgentConnection에 접속 정보를 전달 후 Map에 저장
            AgentConnection agent = new AgentConnection(alias, agentIP, agentPW, socket, out, in);
            agents.put(alias, agent);
            saveAgents();

            // 수신 스레드
            // 그냥 수신만 할 때는 수신루프로도 가능했지만 이제는 송신도 하기 때문에,
            // 스레드로 따로 만들어서 수신과정이 송신과정과 겹치지 않게 한다 (Non-blocking)
            new Thread(() -> {
                try {
                    String json;
                    while ((json = agent.in.readLine()) != null) {
                        System.out.println("[" + alias + "] " + json);
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("[" + alias + "] 접속 시간 초과 - 연결 해제");
                } catch (Exception e) {
                    System.out.println("[" + alias + "] 수신 종료: " + e.getMessage());
                } finally {
                    // 연결 종료 시 Map에서 제거
                    agents.remove(alias);
                    System.out.println("[" + alias + "] 목록에서 제거됨");
                }
            }).start();

        } catch (Exception e) {
            System.out.println("연결 실패 (" + alias + "): " + e.getMessage());
        }
    }

    // 매니저 -> 에이전트로 명령어 송신
    // Socket이 서로 연결되어 있을 때만 사용 가능 (바로 다다음줄 참조)
    static void sendCommand(String input, String cmd) {
        // scanner로 입력받은 명령어 전체를 공백 기준으로 파싱
        String[] parts = input.split(" ");
        String alias = parts[1];

        AgentConnection agent = agents.get(alias);
        if (agent == null) {
            System.out.println("존재하지 않는 Agent 별칭입니다: " + alias);
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("cmd", cmd);

        if (cmd.equals("EXEC") && parts.length >= 3) {
            json.addProperty("index", Integer.parseInt(parts[2]));
        }

        agent.out.println(json.toString());
        System.out.println("[송신 → " + alias + "] " + json);
    }

    static void saveAgents() {
        JsonObject root = new JsonObject();
        agents.forEach((alias, agent) -> {
            JsonObject entry = new JsonObject();
            entry.addProperty("ip",       agent.ip);
            entry.addProperty("password", agent.pw);  // AgentConnection에 password 필드 추가 필요
            entry.addProperty("alias",    alias);
            root.add(alias, entry);
        });
        try (FileWriter fw = new FileWriter(AGENT_FILE)) {
            fw.write(root.toString());
        } catch (Exception e) {
            System.out.println("저장 실패: " + e.getMessage());
        }
    }

    static void loadAgents() {
        File file = new File(AGENT_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            JsonObject root = JsonParser.parseReader(br).getAsJsonObject();
            root.entrySet().forEach(entry -> {
                JsonObject agent = entry.getValue().getAsJsonObject();
                String input = "CONNECT " + agent.get("ip").getAsString() + " " + agent.get("password").getAsString() + " " + agent.get("alias").getAsString();
                connectAgent(input);
            });
        } catch (Exception e) {
            System.out.println("불러오기 실패: " + e.getMessage());
        }
    }
}