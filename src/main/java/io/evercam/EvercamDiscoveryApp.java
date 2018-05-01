package io.evercam;

import io.evercam.network.Constants;
import io.evercam.network.DiscoveryResult;
import io.evercam.network.EvercamDiscover;
import io.evercam.network.discovery.Device;
import io.evercam.network.discovery.DiscoveredCamera;
import io.evercam.network.discovery.NetworkInfo;
import io.evercam.network.discovery.ScanRange;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class EvercamDiscoveryApp {
    private static final String ARG_IP = "-ip";
    private static final String ARG_SUBNET_MASK = "-m";

    public static void main(String[] args) {
        String ip = "";
        String subnetMask = "";
        if (args.length > 0) {
            List<String> argsArray = Arrays.asList(args);
            if (argsArray.contains("-v") || argsArray.contains("--verbose")) {
                Constants.ENABLE_LOGGING = true;
            }

            if (argsArray.contains("-ip") && argsArray.contains("-m")) {
                int ipIndex = argsArray.indexOf("-ip") + 1;
                int subnetIndex = argsArray.indexOf("-m") + 1;
                ip = (String) argsArray.get(ipIndex);
                subnetMask = (String) argsArray.get(subnetIndex);
            }
        }

        if (ip.isEmpty()) {
            ip = NetworkInfo.getLinuxRouterIp();
        }

        if (subnetMask.isEmpty()) {
            subnetMask = NetworkInfo.getLinuxSubnetMask();
        }

        EvercamDiscover.printLogMessage("Router IP address: " + ip + " subnet mask: " + subnetMask);
        EvercamDiscover.printLogMessage("Scanning...");

        try {
            ScanRange scanRange = new ScanRange(ip, subnetMask);
            DiscoveryResult discoveryResult = (new EvercamDiscover()).withDefaults(true).discoverAllLinux(scanRange);
            ArrayList<DiscoveredCamera> cameraList = discoveryResult.getCameras();
            ArrayList<Device> nonCameraList = discoveryResult.getOtherDevices();
            EvercamDiscover.printLogMessage("Scanning finished, found " + cameraList.size() + " cameras and " + nonCameraList.size() + " other devices.");
            printAsJson(cameraList, nonCameraList);
            EvercamDiscover.printLogMessage("On normal completion: 0");
            System.exit(0);
        } catch (Exception var7) {
            if (Constants.ENABLE_LOGGING) {
                var7.printStackTrace();
            }

            EvercamDiscover.printLogMessage("On error: 1");
            System.exit(1);
        }

    }

    public static void printAsJson(ArrayList<DiscoveredCamera> cameraList, ArrayList<Device> nonCameraList) {
        if (cameraList != null) {
            JSONArray cameraJsonArray = new JSONArray();
            JSONArray nonCameraJsonArray = new JSONArray();
            Iterator var5 = cameraList.iterator();

            while (var5.hasNext()) {
                DiscoveredCamera camera = (DiscoveredCamera) var5.next();
                cameraJsonArray.put(camera.toJsonObject());
            }

            var5 = nonCameraList.iterator();

            while (var5.hasNext()) {
                Device device = (Device) var5.next();
                nonCameraJsonArray.put(device.toJsonObject());
            }

            JSONObject arrayJsonObject = new JSONObject();
            arrayJsonObject.put("cameras", cameraJsonArray);
            arrayJsonObject.put("other_devices", nonCameraJsonArray);
            System.out.println(arrayJsonObject.toString(4));
        }

    }
}
