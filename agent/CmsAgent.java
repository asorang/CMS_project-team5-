// OSHI 라이브러리 (운영체제 및 시스템 정보)
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;

// GSON 라이브러리 (JSON Builder/Parser)
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

// Java 내장 라이브러리
import java.net.*;      // 네트워크
import java.io.*;       // BufferReader, PrintWriter

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
   - cp          : CPU 사용량 %                         int
   - uptime      : 시스템 업타임                        int
   - ramUsed     : 총 사용 중인 RAM                     int    | GB 단위
   - diskUsed    : 총 사용 중인 디스크                  int    | GB 단위

 [ Manager → Agent ]
   - password : 평문 비밀번호                           String

 주석으로 이거 쓰기 힘들다....
  ─────────────────────────────────────────────────────────────────────────────────────────────────────────── */

public class CmsAgent {
    // 프로그램 내부 변수
    static final int PORT = 10293;          // 포트번호
    static String PASSWORD = "1234";        // 통신용 비밀번호 (평문저장)
    static final int INTERVAL = 10;         // 시스템 정보 갱신(전송) 주기

    // OSHI Alias
    static final SystemInfo SI = new SystemInfo();                  // 시스템 정보 기본 객체
    static final HardwareAbstractionLayer HAL = SI.getHardware();   // 하드웨어 정보 레이어
    static final CentralProcessor CPU = HAL.getProcessor();         // ├ CPU 객체
    static long[] prevTicks = CPU.getSystemCpuLoadTicks();          // └ CPU 틱 정보 (사용량 계산용)
    static final OperatingSystem OS = SI.getOperatingSystem();      // 운영체제 정보

    public static void main(String[] args) throws Exception{
        try (ServerSocket server = new ServerSocket(PORT)){
            while (true){
                Socket socket = server.accept();
                System.out.println("매니저에 연결됨: " + socket.getInetAddress());
                handleConnection(socket);
            }
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
            // out.println(programList());

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

        System.out.println("[송신] " + json);
        return json.toString();
    }

    // 시스템 정보(운영체제, 프로세서, 코어 수, 스레드 수, 총 메모리, 디스크 수)를 JSON 형태로 반환
    static String systemInfo(){
        long diskTotal = 0;
        for (OSFileStore fs : OS.getFileSystem().getFileStores()) {
            diskTotal += fs.getTotalSpace();
        }

        JsonObject json = new JsonObject();
        json.addProperty("os", OS.toString());
        json.addProperty("processor", CPU.getProcessorIdentifier().getName().toString());
        json.addProperty("cores", CPU.getPhysicalProcessorCount());
        json.addProperty("threads", CPU.getLogicalProcessorCount());
        json.addProperty("totalMemory", toGB(HAL.getMemory().getTotal()));
        json.addProperty("diskTotal", toGB(diskTotal));

        System.out.println("[송신] " + json);
        return json.toString();
    }

    private static long toMB(long bytes){
        return bytes / (1024 * 1024);
    }

    private static long toGB(long bytes){
        return bytes / (1024 * 1024 * 1024);
    }
}
