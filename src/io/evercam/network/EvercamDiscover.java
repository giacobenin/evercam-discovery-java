package io.evercam.network;

import io.evercam.Vendor;
import io.evercam.network.discovery.DiscoveredCamera;
import io.evercam.network.discovery.IpScan;
import io.evercam.network.discovery.MacAddress;
import io.evercam.network.discovery.NatMapEntry;
import io.evercam.network.discovery.NetworkInfo;
import io.evercam.network.discovery.ScanRange;
import io.evercam.network.discovery.ScanResult;
import io.evercam.network.discovery.UpnpDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EvercamDiscover
{
	public static final int DEFAULT_FIXED_POOL = 20;

	private ArrayList<String> activeIpList = new ArrayList<String>();
	private ArrayList<UpnpDevice> deviceList = new ArrayList<UpnpDevice>();// UPnP
																			// device
																			// list
	private ArrayList<NatMapEntry> mapEntries = new ArrayList<NatMapEntry>();// NAT
																				// table
	private ArrayList<DiscoveredCamera> cameraList = new ArrayList<DiscoveredCamera>();
	private ArrayList<DiscoveredCamera> onvifDeviceList = new ArrayList<DiscoveredCamera>();
	private boolean upnpDone = false;
	private boolean natDone = false;
	private int countDone = 0;
	private int queryCountDone = 0;
	private String externalIp = "";
	private boolean withDefaults = false;
	public ExecutorService pool;
	public static long NAT_TIMEOUT = 5000; // 5 secs
	public static long IDENTIFICATION_TIMEOUT = 16000; // 16 secs
	public static long QUERY_TIMEOUT = 12000; // 12 secs

	/**
	 * Include camera defaults(username, password, paths, and thumbnail URLs) in the scanning result or
	 * not
	 * 
	 * @param withDefaults
	 *            true if include camera defaults
	 */
	public EvercamDiscover withDefaults(boolean withDefaults)
	{
		this.withDefaults = withDefaults;
		return this;
	}

	/**
	 * The wrapped method to scan for cameras in Android.
	 * 
	 * @param scanRange
	 *            the range of IP addresses to scan
	 * @param routerIp
	 *            gateway/router IP address
	 * @return a list of discovered camera devices
	 * @throws Exception
	 */
	public ArrayList<DiscoveredCamera> discoverAllLinux(ScanRange scanRange) throws Exception
	{
		pool = Executors.newFixedThreadPool(DEFAULT_FIXED_POOL);
		// Request for external IP address
		externalIp = NetworkInfo.getExternalIP();

		if (!pool.isShutdown())
		{
			// ONVIF discovery
			pool.execute(onvifRunnable);
			printLogMessage("Discovering ONVIF devices......");
			// Start UPnP discovery
			pool.execute(upnpRunnable);
			printLogMessage("Discovering UPnP devices......");
			// Start UPnP router discovery
			pool.execute(new NatRunnable(scanRange.getRouterIpString())
			{
				@Override
				public void onFinished(ArrayList<NatMapEntry> mapEntries)
				{
					printLogMessage("NAT discovery finished.");
					if (mapEntries != null)
					{
						EvercamDiscover.this.mapEntries = mapEntries;
					}
					natDone = true;
				}
			});
			printLogMessage("Discovering NAT table......");
		}

		// Scan to get a list of active IP addresses.
		IpScan ipScan = new IpScan(new ScanResult()
		{
			@Override
			public void onActiveIp(String ip)
			{
				printLogMessage("Active IP: " + ip);
				activeIpList.add(ip);
			}

			@Override
			public void onIpScanned(String ip)
			{
				// TODO Auto-generated method stub
			}
		});
		ipScan.scanAll(scanRange);

		long natWaitingTime = 0;
		while (!upnpDone || !natDone)
		{
			if (natWaitingTime < NAT_TIMEOUT)
			{
				printLogMessage("Waiting for UPnP & NAT discovery...");
				Thread.sleep(2000);
				natWaitingTime += 2000;
			}
			else
			{
				printLogMessage("UPnP & NAT discovery timeout.");
				break;
			}
		}

		printLogMessage("Identifying cameras......");
		// For each active IP, request for MAC address and vendor
		for (int index = 0; index < activeIpList.size(); index++)
		{
			if (!pool.isShutdown())
			{
				pool.execute(new IdentifyCameraRunnable(activeIpList.get(index))
				{
					@Override
					public void onCameraFound(DiscoveredCamera discoveredCamera, Vendor vendor)
					{
						discoveredCamera.setExternalIp(externalIp);

						// Add details discovered from UPnP to camera object
						discoveredCamera = mergeUpnpDevicesToCamera(discoveredCamera, deviceList);

						// Add details in discovered NAT table(mainly
						// forwarded ports)
						discoveredCamera = mergeNatTableToCamera(discoveredCamera, mapEntries);

						synchronized (cameraList)
						{
							cameraList.add(discoveredCamera);
						}
					}

					@Override
					public void onFinished()
					{
						countDone++;
					}
				});
			}
		}

		long identificationWaitingTime = 0;
		while (countDone != activeIpList.size())
		{
			if (identificationWaitingTime < IDENTIFICATION_TIMEOUT)
			{
				printLogMessage("Identifying cameras..." + countDone + '/' + activeIpList.size());
				Thread.sleep(4000);
				identificationWaitingTime += 4000;
			}
			else
			{
				printLogMessage("Camera identification timeout.");
				break;
			}
		}

		// Merge ONVIF devices to discovered camera list
		mergeOnvifDeviceListToCameraList();

		if (!pool.isShutdown())
		{
			for (DiscoveredCamera discoveredCamera : cameraList)
			{
				pool.execute(new EvercamQueryRunnable(discoveredCamera)
				{
					@Override
					public void onFinished()
					{
						queryCountDone++;
					}
				}.withDefaults(withDefaults));
			}
		}

		long queryWaitingTime = 0;
		while (queryCountDone != cameraList.size())
		{
			if (queryWaitingTime < QUERY_TIMEOUT)
			{
				printLogMessage("Retrieving camera defaults..." + queryCountDone + '/'
						+ cameraList.size());
				Thread.sleep(4000);
				queryWaitingTime += 4000;
			}
			else
			{
				printLogMessage("Evercam query timeout.");
				break;
			}
		}

		pool.shutdown();

		try
		{
			if (!pool.awaitTermination(3600, TimeUnit.SECONDS))
			{
				pool.shutdownNow();
			}
		}
		catch (InterruptedException e)
		{
			pool.shutdownNow();
			Thread.currentThread().interrupt();
		}
		
		cameraList = mergeDuplicateCameraFromList(cameraList);

		//Query ARP table again if MAC address is still empty after merging
		fillMacAddressIfNotExist(cameraList);

		return cameraList;
	}

	public static DiscoveredCamera mergeSingleUpnpDeviceToCamera(UpnpDevice upnpDevice,
			DiscoveredCamera discoveredCamera)
	{
		int port = upnpDevice.getPort();
		String model = upnpDevice.getModel();
		if (port > 0)
		{
			discoveredCamera.setHttp(port);
		}
		discoveredCamera.setName(upnpDevice.getFriendlyName());
		discoveredCamera.setModel(model);
		return discoveredCamera;
	}

	public static DiscoveredCamera mergeUpnpDevicesToCamera(DiscoveredCamera camera,
			ArrayList<UpnpDevice> upnpDeviceList)
	{
		try
		{
			if (upnpDeviceList.size() > 0)
			{
				for (UpnpDevice upnpDevice : upnpDeviceList)
				{
					// If IP address matches
					String ipFromUpnp = upnpDevice.getIp();
					if (ipFromUpnp != null && !ipFromUpnp.isEmpty())
					{
						if (camera.getIP().equals(ipFromUpnp))
						{
							mergeSingleUpnpDeviceToCamera(upnpDevice, camera);
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			printLogMessage("Exception while merging UPnP device: " + e.getStackTrace().toString());
		}
		return camera;
	}

	public static DiscoveredCamera mergeNatEntryToCamera(DiscoveredCamera camera,
			NatMapEntry mapEntry)
	{
		int natInternalPort = mapEntry.getInternalPort();
		int natExternalPort = mapEntry.getExternalPort();

		if (camera.getHttp() == natInternalPort)
		{
			camera.setExthttp(natExternalPort);
		}
		if (camera.getRtsp() == natInternalPort)
		{
			camera.setExtrtsp(natExternalPort);
		}

		return camera;
	}

	public static DiscoveredCamera mergeNatTableToCamera(DiscoveredCamera camera,
			ArrayList<NatMapEntry> mapEntries)
	{
		if (mapEntries != null && mapEntries.size() > 0)
		{
			for (NatMapEntry mapEntry : mapEntries)
			{
				String natIp = mapEntry.getIpAddress();
				if (camera.getIP().equals(natIp))
				{
					mergeNatEntryToCamera(camera, mapEntry);
				}
			}
		}
		return camera;
	}
	
	/**
	 * Review the camera list and merge cameras with the same IP address
	 * 
	 * @param the re-organized camera list
	 */
    public static ArrayList<DiscoveredCamera> mergeDuplicateCameraFromList(ArrayList<DiscoveredCamera> cameraList) 
    {
        Map<String, DiscoveredCamera> cameraHashMap = new HashMap<>();
        for (DiscoveredCamera camera: cameraList) 
        {
            String ip = camera.getIP();
            DiscoveredCamera tempCamera = cameraHashMap.get(ip);
            if (tempCamera == null) 
            {
                tempCamera = new DiscoveredCamera(ip);
                cameraHashMap.put(ip, tempCamera);
            }
            tempCamera.merge(camera);
        }

       return new ArrayList<>(cameraHashMap.values());
    }
	
	/**
	 * If MAC address doesn't exist in camera object, query ARP table again
	 */
	public static void fillMacAddressIfNotExist(ArrayList<DiscoveredCamera> cameraList)
	{
		for(DiscoveredCamera camera : cameraList)
		{
			if(!camera.hasMac())
			{
				camera.setMAC(MacAddress.getByIpLinux(camera.getIP()));
			}
		}
	}

	private OnvifRunnable onvifRunnable = new OnvifRunnable()
	{
		@Override
		public void onFinished()
		{
			printLogMessage("ONVIF discovery finished.");
		}

		@Override
		public void onDeviceFound(DiscoveredCamera discoveredCamera)
		{
			printLogMessage("Found ONVIF device: " + discoveredCamera.getIP());
			discoveredCamera.setExternalIp(externalIp);
			onvifDeviceList.add(discoveredCamera);
		}
	};

	public void mergeOnvifDeviceListToCameraList()
	{
		if (onvifDeviceList.size() > 0)
		{
			for (DiscoveredCamera onvifCamera : onvifDeviceList)
			{
				boolean matched = false;

				if (cameraList.size() > 0)
				{
					for (DiscoveredCamera discoveredCamera : cameraList)
					{
						if (discoveredCamera.getIP().equals(onvifCamera.getIP()))
						{
							matched = true;
							if (onvifCamera.hasModel())
							{
								discoveredCamera.setModel(onvifCamera.getModel());
								discoveredCamera.setHttp(onvifCamera.getHttp());
							}

							break;
						}
					}
				}

				if (!matched)
				{
					cameraList.add(onvifCamera);
				}
			}
		}
	}

	private UpnpRunnable upnpRunnable = new UpnpRunnable()
	{

		@Override
		public void onFinished(ArrayList<UpnpDevice> upnpDeviceList)
		{
			printLogMessage("UPnP discovery finished.");
			if (upnpDeviceList != null)
			{
				deviceList = upnpDeviceList;
			}
			upnpDone = true;
		}

		@Override
		public void onDeviceFound(UpnpDevice upnpDevice)
		{
			printLogMessage("Found UPnP device: " + upnpDevice.getIp());
		}
	};

	/**
	 * Only print the logging message when logging is enabled
	 * 
	 * @param message
	 *            The logging message to be printed in console
	 */
	public static void printLogMessage(String message)
	{
		if (Constants.ENABLE_LOGGING)
		{
			System.out.println(message);
		}
	}
}
