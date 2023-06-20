/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.beans.network;

import java.io.Serial;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.nervousync.exceptions.beans.network.IPAddressException;
import org.nervousync.exceptions.beans.network.NetworkInfoException;
import org.nervousync.utils.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * System interface network information
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jul 24, 2015 11:53:10 AM $
 */
public final class NetworkInfo implements Serializable {

	/**
	 * 
	 */
    @Serial
	private static final long serialVersionUID = -8060054814830700945L;

	/**
	 * Is virtual adapter
	 */
	private final boolean virtual;
	/**
	 * Interface display name in the system
	 */
	private final String displayName;
	/**
	 * Interface adapter physical address
	 */
	private String macAddress = "";
	/**
	 * IP address list of interface configured
	 */
	private final List<IPAddressInfo> ipAddressList = new ArrayList<>();

	/**
	 * Constructor for NetworkInfo
	 *
	 * @param networkInterface NetworkInterface value
	 * @throws NetworkInfoException If the value of NetworkInterface is null or catch other SocketException
	 */
	public NetworkInfo(final NetworkInterface networkInterface) throws NetworkInfoException {
		if (networkInterface == null) {
			throw new NetworkInfoException("NetworkInterface is null");
		}
		Logger logger = LoggerFactory.getLogger(this.getClass());
		try {
			if (networkInterface.isUp() && !networkInterface.isVirtual()) {
				byte[] macAddress = networkInterface.getHardwareAddress();
				if (macAddress != null && macAddress.length > 0) {
					StringBuilder stringBuilder = new StringBuilder();
					for (byte mac : macAddress) {
						stringBuilder.append(":");
						String address = Integer.toHexString(mac & 0xFF);
						if (address.length() == 1) {
							address = "0" + address;
						}
						stringBuilder.append(address.toUpperCase());
					}
					this.macAddress = stringBuilder.substring(1);
				}
			}
		} catch (SocketException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Retrieve network info error! ", e);
			}
			throw new NetworkInfoException("Retrieve network info error! ", e);
		}

		this.virtual = networkInterface.isVirtual();
		
		this.displayName = networkInterface.getDisplayName();
		Enumeration<InetAddress> enumeration = networkInterface.getInetAddresses();
		
		while (enumeration.hasMoreElements()) {
			try {
				IPAddressInfo ipAddressInfo = new IPAddressInfo(enumeration.nextElement());
				this.ipAddressList.add(ipAddressInfo);
			} catch (IPAddressException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Read IP Address Info Error! ", e);
				}
			}
		}
	}

	/**
	 * Is virtual boolean.
	 *
	 * @return the virtual
	 */
	public boolean isVirtual() {
		return virtual;
	}

	/**
	 * Gets display name.
	 *
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Gets mac address.
	 *
	 * @return the macAddress
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * Gets the ip address list.
	 *
	 * @return the getIpAddressList
	 */
	public List<IPAddressInfo> getIpAddressList() {
		return ipAddressList;
	}

	/**
	 * Gets i pv 4 address list.
	 *
	 * @return the IPv4 address list
	 */
	public List<IPAddressInfo> getIPv4AddressList() {
		List<IPAddressInfo> addressList = new ArrayList<>();
		for (IPAddressInfo ipAddressInfo : this.ipAddressList) {
			if (IPUtils.isIPv4Address(ipAddressInfo.getIpAddress())) {
				addressList.add(ipAddressInfo);
			}
		}
		return addressList;
	}

	/**
	 * Gets i pv 6 address list.
	 *
	 * @return the IPv6 address list
	 */
	public List<IPAddressInfo> getIPv6AddressList() {
		List<IPAddressInfo> addressList = new ArrayList<>();
		for (IPAddressInfo ipAddressInfo : this.ipAddressList) {
			if (IPUtils.isIPv6Address(ipAddressInfo.getIpAddress())) {
				addressList.add(ipAddressInfo);
			}
		}
		return addressList;
	}

	/**
	 * Gets serial version uid.
	 *
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * Configured ip address information
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
	 * @version $Revision : 1.0 $ $Date: Jul 2, 2018 $
	 */
	public static final class IPAddressInfo implements Serializable {

		/**
		 * 
		 */
		@Serial
		private static final long serialVersionUID = -2882813548945783456L;
		
		/**
		 * IP address, supported IPv4 and IPv6
		 */
		private final String ipAddress;
		/**
		 * Is site local address
		 */
		private final boolean local;
		/**
		 * Is loop back address
		 */
		private final boolean loop;
		/**
		 * Is link local status
		 */
		private final boolean linkLocal;

		/**
		 * Constructor
		 *
		 * @param inetAddress InetAddress object read from interface
		 * @throws IPAddressException Given inetAddress is null
		 */
		public IPAddressInfo(final InetAddress inetAddress) throws IPAddressException {
			if (inetAddress == null) {
				throw new IPAddressException("InetAddress is null");
			}

			String ipAddress = inetAddress.getHostAddress();
			if (ipAddress.indexOf("%") > 0) {
				this.ipAddress = ipAddress.substring(0, ipAddress.indexOf("%"));
			} else {
				this.ipAddress = ipAddress;
			}
			this.local = inetAddress.isSiteLocalAddress();
			this.loop = inetAddress.isLoopbackAddress();
			this.linkLocal = inetAddress.isLinkLocalAddress();
		}

		/**
		 * Gets ip address.
		 *
		 * @return the ipAddress
		 */
		public String getIpAddress() {
			return ipAddress;
		}

		/**
		 * Is local boolean.
		 *
		 * @return the local
		 */
		public boolean isLocal() {
			return local;
		}

		/**
		 * Is loop boolean.
		 *
		 * @return the loop
		 */
		public boolean isLoop() {
			return loop;
		}

		/**
		 * Is link local boolean.
		 *
		 * @return the linkLocal
		 */
		public boolean isLinkLocal() {
			return linkLocal;
		}
	}
}
