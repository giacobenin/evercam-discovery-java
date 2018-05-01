package io.evercam.network.discovery;

import io.evercam.network.Constants;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpTranslator {
    public static final String EMPTY_IP = "0.0.0.0";

    public static String getIpFromIntSigned(int ip_int) {
        String ip = "";
        for (int k = 0; k < 4; k++) {
            ip = ip + ((ip_int >> k * 8) & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }

    public static String getIpFromLongUnsigned(long ip_long) {
        String ip = "";
        for (int k = 3; k > -1; k--) {
            ip = ip + ((ip_long >> k * 8) & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }

    protected static long getUnsignedLongFromIp(String ipAddr) {

        try {
            InetAddress a = InetAddress.getByName(ipAddr);
            byte[] bytes = a.getAddress();
            return new BigInteger(1, bytes).longValueExact();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is not in use
     */
    public static boolean isValidIpv4Addr(String ip) {
        final String REGULAR_EXPRESSION_IP_V4 = "^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})){3}$";
        return ip.matches(REGULAR_EXPRESSION_IP_V4);
    }

    public static boolean isLocalIpv4(String ip) {
        final String REGULAR_EXPRESSION_LOCAL_IP = "(127.0.0.1)|(192.168.*$)|(172.1[6-9].*$)|(172.2[0-9].*$)|(172.3[0-1].*$)|(10.*$)";
        return ip.matches(REGULAR_EXPRESSION_LOCAL_IP);
    }
}
