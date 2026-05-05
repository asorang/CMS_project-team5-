import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.util.List;
import static java.lang.Thread.sleep;

public class SystemInfoGet {
    public static void main() throws InterruptedException{
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        OperatingSystem os = si.getOperatingSystem();
        CentralProcessor cpu = hal.getProcessor();
        GlobalMemory ram = hal.getMemory();

        long ramTotal = ram.getTotal();
        long ramAvailable = ram.getAvailable();

        // CPU 사용률을 측정하기 위해서는 CPU 틱 속도를 측정하여 해당 값을 기반으로 구한다
        // 1초 대기해야 하므로 Thread.sleep(1000)을 사용, 오류가 나는 경우에는 main() 뒤에 throws InterruptedException 이 추가되었는지 꼭 확인하자!
        long[] prevTicks = cpu.getSystemCpuLoadTicks();
        Thread.sleep(1000);
        double cpuUsage = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100.0;


        System.out.printf("[OS] " + os.toString() + "%n");
        System.out.printf("[CPU] %s (%dC/%dT/%.1f%%)%n", cpu.getProcessorIdentifier().getName(), cpu.getPhysicalProcessorCount(), cpu.getLogicalProcessorCount(), cpuUsage);
        System.out.printf("[RAM] %dMB / %dMB%n", toMB(ramTotal - ramAvailable), toMB(ramTotal));

        // 디스크 정보를 가져올 때는 좀 복잡하다!
        // 현재 PC에 장착된 디스크 정보를 하나씩 가져오는데 이건 List를 사용해야 함.
        List<HWDiskStore> disks = hal.getDiskStores();
        if (disks.isEmpty()) {
            System.out.println("[DISK] 디스크를 찾을 수 없음");
        }else{
            for (int i=0; i<disks.size(); i++){
                HWDiskStore disk = disks.get(i);
                System.out.printf("[DISK-%d] %s (%d GB)%n", i, disk.getModel(), toGB(disk.getSize()));
                // disk.getModel() - 디스크의 모델 출력 (하드디스크/SSD 모델)
                // disk.getName() - 디스크의 이름 (Windows의 경우 물리적 주소)을 출력
            }
        }
    }

    // 기본적으로 OSHI는 바이트 단위로 데이터량을 출력한다 (RAM, 하드디스크....)
    // 용량 단위를 바꿔주는 메소드가 있는 게 좋음.
    private static long toMB(long bytes) {
        long mbBytes = bytes / (1024*1024);
        return mbBytes;
    }

    private static long toGB(long bytes){
        long gbBytes = bytes / (1024*1024*1024);
        return gbBytes;
    }
}
