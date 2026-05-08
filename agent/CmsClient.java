import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.*;
import oshi.software.os.OperatingSystem;

import java.io.*;
import java.net.*;
import java.util.*;

public class CmsClient {
    // 프로그램 내부 변수
    static final int PORT = 10293;
    static String PASSWORD = "1234";

    // 개발용 플래그 (마스터로 전송하는 정보 콘솔에 표시)
    static final boolean debugMode = true;

    // OSHI Alias
    static final SystemInfo SI = new SystemInfo();
    static final HardwareAbstractionLayer HAL = SI.getHardware();
    static final CentralProcessor CPU = HAL.getProcessor();
    static final OperatingSystem OS = SI.getOperatingSystem();
    static long[] prevTicks = CPU.getSystemCpuLoadTicks();

    public static void main(String[] args) throws Exception{
        System.out.printf("[CMS] 프로그램 준비 완료 (PORT: %d / PASS: %s)", PORT, PASSWORD);

        try (ServerSocket server = new ServerSocket(PORT)){
            while (true){
                // TODO: 소켓 열고 handleConnection 함수로 정보 수발신하도록 처리
            }
        }
    }


    static void handleConnection(Socket socket){
        // TODO: 네트워크 포트 열고 해당 포트로 JSON 데이터 전송
    }
    static void systemStatus() throws InterruptedException{
        double cpu = CPU.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = CPU.getSystemCpuLoadTicks();

        long uptime = OS.getSystemUptime();
        long ramTotal = HAL.getMemory().getTotal();
        long ramUsed = ramTotal - HAL.getMemory().getAvailable();
        long diskTotal = 0;

        // 디스크 총 용량 = getDiskStores에 있는 모든 디스크 요소의 getSize의 합
        for (HWDiskStore d: HAL.getDiskStores()) diskTotal += d.getSize();

        if (debugMode){
            System.out.printf("[%d] CPU:%d / RAM:%d/%d / DISK:%d", cpu, uptime, ramUsed, ramTotal, diskTotal);
        }

        // TODO: JSON 데이터 문자열 빌드해서 return
    }


}
