package cn.moyada.screw.enums;

/**
 * 协议
 * @author xueyikang
 * @create 2018-04-21 03:42
 */
public enum ProtocolType {
    HTTP("http://"),
    HTTPS("https://"),
    FILE("file://"),
    TCP("tcp://"),
    UDP("udp://"),
    ZOOKEEPER("zookeeper://");

    /**
     * 协议头
     */
    private String head;

    ProtocolType(String head) {
        this.head = head;
    }

    /**
     * 检查协议
     * @param url
     * @return
     */
    public static boolean checkProtocol(String url) {
        return checkProtocol(url, ProtocolType.values());
    }

    /**
     * 检查协议是否正确
     * @param url
     * @param protocolTypes
     * @return
     */
    public static boolean checkProtocol(String url, ProtocolType... protocolTypes) {
        int minLen = 0;
        int maxLen = 0;
        for (ProtocolType protocolType : protocolTypes) {
            minLen = Math.min(minLen, protocolType.head.length());
            maxLen = Math.max(maxLen, protocolType.head.length());
        }
        if(url.length() < minLen + 1) {
            return false;
        }

        for (ProtocolType protocolType : protocolTypes) {
            if(url.startsWith(protocolType.head)) {
                return true;
            }
        }
        return false;
    }
}
