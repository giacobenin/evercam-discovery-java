package io.evercam.network.discovery;

import org.apache.commons.net.util.SubnetUtils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static io.evercam.network.discovery.IpTranslator.getUnsignedLongFromIp;

public class ScanRange {
    private final long routerIp;
    private final String routerIpString;
    private final SubnetUtils utils;

    public ScanRange(String routerIp, String subnetMask) throws Exception {
        this.routerIp = getUnsignedLongFromIp(routerIp);
        this.routerIpString = routerIp;
        this.utils = new SubnetUtils(routerIp, subnetMask);
    }

    /**
     * @return true if the given IP is in this scan range
     */
    public boolean containIp(String ip) throws Exception {
        return utils.getInfo().isInRange(ip);
    }

    public long size() {
        return utils.getInfo().getAddressCountLong();
    }

    protected long getRouterIp() {
        return routerIp;
    }

    public String getRouterIpString() {
        return routerIpString;
    }

    public String toString() {
        return new StringBuilder("[")
                .append(utils.getInfo().getLowAddress())
                .append(", ")
                .append(utils.getInfo().getHighAddress())
                .append("]")
                .toString();
    }

    protected long getScanStart() {
        return getUnsignedLongFromIp(utils.getInfo().getLowAddress());
    }

    protected long getScanEnd() {
        return getUnsignedLongFromIp(utils.getInfo().getHighAddress());
    }
}
