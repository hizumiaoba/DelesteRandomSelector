package com.ranfa.lib;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JST {
	
	// Logger
	private static final Logger LOG = LoggerFactory.getLogger(JST.class);
	private static final String SERVER_NAME = "ntp.nict.jp";
	
	// private constructor
	private JST() { /* do nothing */ }
	
	public static Date JSTNow() {
		NTPUDPClient client = new NTPUDPClient();
		InetAddress address = null;
		try {
			address = InetAddress.getByName(SERVER_NAME);
		} catch (UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			client.open();
		} catch (SocketException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		TimeInfo info = null;
		try {
			info = client.getTime(Objects.requireNonNull(address));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		NtpV3Packet packet = Objects.requireNonNull(info).getMessage();
		info.computeDetails();
		LOG.debug("NTPClient: {}", client);
		LOG.debug("NetAddress: {}", address);
		LOG.debug("Time info: {}", info);
		long t1 = packet.getOriginateTimeStamp().getTime();
		long t2 = packet.getReceiveTimeStamp().getTime();
		long t3 = packet.getTransmitTimeStamp().getTime();
		long t4 = info.getReturnTime();
		long CalculatedOffset = (t2 - t1 + t3 - t4) / 2;
		long receiveOffset = info.getOffset();
		long offset = ( CalculatedOffset + receiveOffset ) / 2;
		Date date = new Date(System.currentTimeMillis() + offset);
		LOG.debug("Client originate Time: {}", t1);
		LOG.debug("Server Received request Time: {}", t2);
		LOG.debug("Server Transmit response Time: {}", t3);
		LOG.debug("Client Received response Time: {}", t4);
		LOG.info("Estimated time offset: {}", CalculatedOffset);
		LOG.info("received time offset: {}", receiveOffset);
		LOG.info("estimated JST: {}", date);
		LOG.info("Current client date: {}", new Date(System.currentTimeMillis()));
		return date;
	}
}
