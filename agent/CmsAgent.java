// OSHI 라이브러리 (운영체제 및 시스템 정보)
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;

// GSON 라이브러리 (JSON Builder/Parser)
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

// Java 내장 라이브러리
import java.net.*;      // 네트워크
import java.io.*;       // BufferReader, PrintWriter
import java.util.*;
/*  ───────────────────────────────────────────────────────────────────────────────────────────────────────────
 ## API 명세 ##
 - 기본적으로 Socket을 사용하여 Agent-Manager간 통신할 때는 JSON을 사용한다

 [ Agent → Manager 방향 (발신) ]
   <연결 수립 (인증)>
   - auth        : 인증 정보 결과                       String | 성공 시 AUTH_OK, 실패 시 AUTH_NG

   <최초 정보 전송>
   - os          : 운영체제 정보 (이름, 빌드번호)       String
   - processor   : CPU 이름                             String
   - cores       : 물리적 코어 수                       int
   - threads     : 논리적 코어(스레드) 수               int
   - totalMemory : 설치된 총 RAM 용량                   int    | GB 단위
   - totalDisk   : 설치된 총 디스크 용량                int    | GB 단위

   <주기적 정보 전송>
   - cp         : CPU 사용량                           int
   - uptime     : 시스템 업타임                        int
   - ramUsed    : 총 사용 중인 RAM                     int    | GB 단위
   - diskUsed   : 총 사용 중인 디스크                  int    | GB 단위

 [ Manager → Agent ]
   - password   : 평문 비밀번호                       String
   - cmd        : 시스템 명렁어                       String  | SHUTDOWN, REBOOT, LOCK (프로그램 실행 신호에도 나중에 활용할 거임.)
 주석으로 이거 쓰기 힘들다....
  ─────────────────────────────────────────────────────────────────────────────────────────────────────────── */

public class CmsAgent {
    // 프로그램 내부 변수
    static int PORT = 10293;                // 포트번호
    static String PASSWORD = "1234";        // 통신용 비밀번호 (평문저장)

    static final int INTERVAL = 10;         // 시스템 정보 갱신(전송) 주기
    static final String CONFIG_FILE = "config.json";
    static final List<String[]> PROGRAMS = new ArrayList<>();
    static final boolean DEBUG_MODE = true; // 개발 중 플래그
                                            // 개발 중에 동작하면 안 되는 기능이 있으면 if문 분기로 따로 빼서 사용할 것

    // OSHI Alias
    static final SystemInfo SI = new SystemInfo();                  // 시스템 정보 기본 객체
    static final HardwareAbstractionLayer HAL = SI.getHardware();   // 하드웨어 정보 레이어
    static final CentralProcessor CPU = HAL.getProcessor();         // ├ CPU 객체
    static long[] prevTicks = CPU.getSystemCpuLoadTicks();          // └ CPU 틱 정보 (사용량 계산용)
    static final OperatingSystem OS = SI.getOperatingSystem();      // 운영체제 정보

    public static void main(String[] args) throws Exception{
        loadConfig();

        try (ServerSocket server = new ServerSocket(PORT)){
            System.out.println("매니저 프로그램 연결 대기 중");
            while (true){
                Socket socket = server.accept();
                System.out.println("매니저에 연결됨: " + socket.getInetAddress());
                handleConnection(socket);
            }
        }
    }

    // 설정 파일 읽어들이기 (프로그램 시작 시 실행)
    static void loadConfig(){
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            System.out.println("설정 파일을 찾을 수 없습니다.");
            System.exit(0);
        }

        // 설정파일이 있는 경우 해당 파일을 읽어들여서 비밀번호, 포트설정, 프로그램 리스트를 받아온다
        // 여기서도 BufferedReader는 열일...
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            JsonObject root = JsonParser.parseReader(br).getAsJsonObject();

            // JSON 파일에 비밀번호가 지정되어 있다면 그걸 사용 (if문 통과), 없다면 기본값을 사용한다
            if (root.has("password")) PASSWORD = root.get("password").getAsString();

            root.get("programs").getAsJsonArray().forEach(el -> {
                JsonObject p = el.getAsJsonObject();
                PROGRAMS.add(new String[]{
                        p.get("name").getAsString(),
                        p.get("path").getAsString()
                });
            });
            System.out.println("설정 로드 완료. 프로그램 " + PROGRAMS.size() + "개");
        } catch (Exception e) {
            System.out.println("설정 로드 실패: " + e.getMessage());
        }
    }

    // Manager와의 연결 처리 & 시스템 상태 전송
    static void handleConnection(Socket socket){
        try{
            // 소켓에서 입력 스트림과 출력 스트림을 생성
            // 기본적으로 Socket에서 들어오는 정보들은 Byte Stream이기 때문에 Helper가 있어야 편하게 사용할 수 있음
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String authString = in.readLine();                                                // 인증정보 RAW 텍스트
            JsonObject authJson = JsonParser.parseString(authString).getAsJsonObject();       // ...를 JSON으로 파싱

            // Password Key가 일치하지 않으면 AUTH_FAIL 메시지 전송
            if (!authJson.has("password") || !authJson.get("password").getAsString().equals(PASSWORD)){
                out.println("{\"auth\":\"AUTH_NG\"}");
                socket.close();

                System.out.println("매니저 인증 실패");
                return;
            }
            // 인증 성공 시 AUTH_OK 메시지 전송
            out.println("{\"auth\":\"AUTH_OK\"}");
            System.out.println("매니저 인증 성공");

            // 시스템 정보와 프로그램 리스트
            out.println(systemInfo());

            // 매니저에서 명령어 수신은 별개의 스레드로 처리한다 (Non-Blocking)
            new Thread(() -> {
                try{
                    String systemCmd;
                    while ((systemCmd = in.readLine()) != null) { systemCommand(systemCmd, out); }
                }catch (Exception e){
                    System.out.println("명령 수신 종료: " + e.getMessage());
                }
            }).start();

            // 시스템 상태 정보를 n초마다 전송
            while(!out.checkError()){ // <- 연결이 끊어질 때 while문 종료
                out.println(systemStatus());
                Thread.sleep(INTERVAL * 1000);
            }
        } catch (Exception e){
            System.out.println("매니저 프로그램 연결 종료: " + e.getMessage());
        }
    }

    // 시스템 상태 정보를 JSON 형태로 반환
    // OSHI 라이브러리에서 가져온 데이터를 JSON으로 Build하여 Raw String으로 소켓에 반환한다
    static String systemStatus(){
        double cpu = CPU.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = CPU.getSystemCpuLoadTicks();

        long uptime = OS.getSystemUptime();
        long ramTotal = HAL.getMemory().getTotal();
        long ramUsed = ramTotal - HAL.getMemory().getAvailable();
        long diskTotal = 0; long diskUsed = 0;

        // 디스크 총 사용량 = 모든 파티션의 용량 총합 - 각 파티션의 사용 가능한 공간 총합
        // Claude 참조함
        for (OSFileStore fs : OS.getFileSystem().getFileStores()) {
            diskTotal += fs.getTotalSpace();                        // 파티션 용량 총합
            diskUsed  += fs.getTotalSpace() - fs.getUsableSpace();  // 용량 총합 - 사용 가능한 공간 총합
        }

        // GSON 라이브러리를 사용하여 JSON 객체 생성 및 데이터 추가
        JsonObject json = new JsonObject();
        json.addProperty("cpu", (int) cpu);
        json.addProperty("uptime", uptime);
        json.addProperty("ramUsed", toGB(ramUsed));
        json.addProperty("diskUsed", toGB(diskUsed));

        if (DEBUG_MODE){ System.out.println("[송신] " + json); }
        return json.toString();
    }

    // 시스템 정보 및 프로그램 바로가기 정보를 JSON 형태로 반환
    // 원래는 별도의 함수로 나누려고 했는데 어차피 JSON인 거 한번에 뭉쳐서 보내는 게 낫겠더라....
    static String systemInfo(){
        long diskTotal = 0;
        for (OSFileStore fs : OS.getFileSystem().getFileStores()) {
            diskTotal += fs.getTotalSpace();
        }

        // 컴퓨터 정보
        JsonObject json = new JsonObject();
        json.addProperty("os", OS.toString());
        json.addProperty("processor", CPU.getProcessorIdentifier().getName().toString());
        json.addProperty("cores", CPU.getPhysicalProcessorCount());
        json.addProperty("threads", CPU.getLogicalProcessorCount());
        json.addProperty("totalMemory", toGB(HAL.getMemory().getTotal()));
        json.addProperty("diskTotal", toGB(diskTotal));

        // 프로그램 리스트
        JsonArray programs = new JsonArray();
        for (String[] p : PROGRAMS) {
            JsonObject entry = new JsonObject();
            entry.addProperty("name", p[0]);
            entry.addProperty("path", p[1]);
            programs.add(entry);
        }
        json.add("programs", programs);

        if (DEBUG_MODE){ System.out.println("[송신] " + json); }
        return json.toString();
    }

    // 시스템 조작 명령어 (컴퓨터 종료, 다시 시작 등) 를 처리하는 메서드
    static void systemCommand(String json, PrintWriter out){
        JsonObject ack = new JsonObject();
        ack.addProperty("type", "ACK");
        try{
            JsonObject cmdJson = JsonParser.parseString(json).getAsJsonObject();
            String cmd = cmdJson.get("cmd").getAsString();
            ack.addProperty("cmd", cmd); // 파싱 성공한 명령어 기록

            System.out.println("[수신] " + json);

            if (DEBUG_MODE) {
                switch (cmd) {
                    // 개발 중에는 단순 문장 출력으로 대체
                    case "SHUTDOWN" -> System.out.println("[CMD] Shutdowm 명령 수신됨");
                    case "REBOOT" -> System.out.println("[CMD] Reboot 명령 수신됨");
                    case "LOCK" -> System.out.println("[CMD] PC 잠금 명령 수신됨");
                    case "EXEC" -> System.out.println("[CMD] EXEC 명령 수신됨, index: " + cmdJson.get("index").getAsInt());

                }
                // 디버그 모드 성공 응답 세팅
                ack.addProperty("status", "SUCCESS");
                ack.addProperty("message", "디버그 모드: " + cmd + " 가상 실행 완료");
            }else {
                switch (cmd) {
                    // 실제 작동 시에는 컴퓨터 조작: 근데 개발중에는 컴퓨터가 꺼지면 굉장히 불미스럽겠죠? 그러니까 DEBUG_MODE를 True로 둡시다.
                    case "SHUTDOWN" -> new ProcessBuilder("shutdown", "/s", "/t", "0").start();
                    case "REBOOT"   -> new ProcessBuilder("shutdown", "/r", "/t", "0").start();
                    case "LOCK"     -> new ProcessBuilder("rundll32.exe", "user32.dll,LockWorkStation").start();
                    case "EXEC"     -> {
                        int index = cmdJson.get("index").getAsInt();
                        if (index >= 0 && index < PROGRAMS.size()) new ProcessBuilder(PROGRAMS.get(index)[1]).start();
                    }
                }
                // 실제 모드 성공 응답 세팅
                ack.addProperty("status", "SUCCESS");
                ack.addProperty("message", "명령어가 정상적으로 시스템에 전달됨");
            }
        }catch (Exception e) {
            System.out.println("명령 처리 오류: " + e.getMessage());
        }
        // 완성된 ACK 패킷을 매니저로 송신
        if (out != null) {
            out.println(ack.toString());
            System.out.println("[송신] " + ack.toString());
        }
    }

    private static long toMB(long bytes){
        return bytes / (1024 * 1024);
    }

    private static long toGB(long bytes){
        return bytes / (1024 * 1024 * 1024);
    }
}
