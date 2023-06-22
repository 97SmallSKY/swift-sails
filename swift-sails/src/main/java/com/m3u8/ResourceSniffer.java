//import org.jnetpcap.Pcap;
//import org.jnetpcap.PcapIf;
//import org.jnetpcap.packet.PcapPacket;
//import org.jnetpcap.packet.PcapPacketHandler;
//
//public class PacketSniffer {
//    public static void main(String[] args) {
//        // 获取系统上的网络设备列表
//        StringBuilder errbuf = new StringBuilder();
//        PcapIf device = new PcapIf();
//        int r = Pcap.findAllDevs(device, errbuf);
//        if (r != Pcap.OK) {
//            System.err.println("Error: " + errbuf.toString());
//            return;
//        }
//
//        // 选择要嗅探的网络设备
//        PcapIf selectedDevice = device;
//
//        // 打开选定的网络设备
//        int snaplen = 64 * 1024; // 捕获的数据包的最大长度
//        int flags = Pcap.MODE_PROMISCUOUS; // 混杂模式
//        int timeout = 10 * 1000; // 超时时间，单位为毫秒
//        Pcap pcap = Pcap.openLive(selectedDevice.getName(), snaplen, flags, timeout, errbuf);
//        if (pcap == null) {
//            System.err.println("Error while opening device: " + errbuf.toString());
//            return;
//        }
//
//        // 创建数据包处理器
//        PcapPacketHandler<String> packetHandler = new PcapPacketHandler<String>() {
//            public void nextPacket(PcapPacket packet, String user) {
//                System.out.println("Received packet: " + packet);
//            }
//        };
//
//        // 开始捕获数据包
//        pcap.loop(Pcap.LOOP_INFINITE, packetHandler, "user");
//
//        // 关闭网络设备
//        pcap.close();
//    }
//}
