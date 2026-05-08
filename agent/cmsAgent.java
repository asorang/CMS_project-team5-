import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;
import gson.Gson;

import java.net.*;

public class cmsAgent {
    // 프로그램 내부 변수
    static final int PORT = 10293;
    static String PASSWORD = "1234";
    static final int INTERVAL = 10;
    
    // OSHI Alias
    static final SystemInfo SI = new SystemInfo();
    static final HardwareAbstractionLayer HAL = SI.getHardware();
    static final CentralProcessor CPU = HAL.getProcessor();
    static final OperatingSystem OS = SI.getOperatingSystem();
    static long[] prevTicks = CPU.getSystemCpuLoadTicks();

    public static void main(String[] args) throws Exception{
        System.out.printf("[Agent] READY (PORT: %d / PASS: %s)", PORT, PASSWORD);

        try (ServerSocket server = new ServerSocket(PORT)){
            while (true){
                Socket socket = server.accept();
                System.out.println("[Agent] Connection Established: " + socket.getInetAddress());
                handleConnection(socket);
            }
        }
    }

    // Manager와의 연결 처리 & 시스템 상태 전송
    static void handleConnection(Socket socket){
        try{
            // 소켓에서 입력 스트림과 출력 스트림을 생성
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        }{
            String authRaw = in.readLine();
            JsonObject authJson = JsonParser.parseString(authRaw).getAsJsonObject();
        
            // Password Key가 일치하지 않으면 AUTH_FAIL 메시지 전송
            if (authRaw == null || !authJson.has("password") || !authJson.get("password").getAsString().equals(PASSWORD)){
                out.println("{\"cmd\":\"AUTH_FAIL\"}");
                socket.close();

                System.out.println("[Agent] Manager Authentication Failed");
                return;
            }
            // 인증 성공 시 AUTH_OK 메시지 전송
            out.println("{\"cmd\":\"AUTH_OK\"}");
            System.out.println("[Agent] Manager Authentication Successful");

            // 시스템 정보와 프로그램 리스트            
            out.println(systemInfo());
            // out.println(programList()); 
            
            // 시스템 상태 정보를 n초마다 전송
            while(!out.checkError()){ // <- 연결이 끊어질 때 while문 종료
                out.println(systemStatus());
                Thread.sleep(INTERVAL * 1000);
            }
        } catch (Exception e){
            System.out.println("[Agent] Manager Disconnected: " + e.getMessage());
        }
    }

    // 시스템 상태 정보를 JSON 형태로 반환
    static String systemStatus() throws InterruptedException{
        double cpu = CPU.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = CPU.getSystemCpuLoadTicks();

        long uptime = OS.getSystemUptime();
        long ramTotal = HAL.getMemory().getTotal();
        long ramUsed = ramTotal - HAL.getMemory().getAvailable();
        long diskTotal = 0;

        // 디스크 총 용량 = getDiskStores에 있는 모든 디스크 요소의 getSize의 합
        for (HWDiskStore d: HAL.getDiskStores()) diskTotal += d.getSize();
        
        // GSON 라이브러리를 사용하여 JSON 객체 생성 및 데이터 추가
        JsonObject json = new JsonObject();
        json.addProperty("cpu", (int) cpu);
        json.addProperty("uptime", uptime);
        json.addProperty("ramUsed", ramUsed);
        json.addProperty("ramTotal", ramTotal);
        json.addProperty("diskTotal", diskTotal);

        // JSON 객체를 문자열로 변환하여 반환
        return json.toString();
    }

    // 시스템 정보(운영체제, 프로세서, 코어 수, 스레드 수, 총 메모리, 디스크 수)를 JSON 형태로 반환
    static String systemInfo(){
        JsonObject json = new JsonObject();
        json.addProperty("os", OS.toString());
        json.addProperty("processor", CPU.toString());
        json.addProperty("cores", CPU.getPhysicalProcessorCount());
        json.addProperty("threads", CPU.getLogicalProcessorCount());
        json.addProperty("totalMemory", toGB(HAL.getMemory().getTotal()) + " GB");
        json.addProperty("diskCount", HAL.getDiskStores().length);

        return json.toString();
    }

    private static long toGB(long bytes){
        return bytes / (1024 * 1024 * 1024);
    }
}
