package com.ranfa.lib;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JST（日本標準時）を <a href="https://www.nict.go.jp/">情報通信研究機構</a> から取得するユーティリティクラスです
 * <p>
 * @author hizum
 *
 * @version 4.0.0
 */
public class JST {
	
	// Logger
	private static final Logger LOG = LoggerFactory.getLogger(JST.class);
	private static final String SERVER_NAME = "ntp.nict.jp";
	
	// private constructor
	private JST() { /* do nothing */ }
	
	/**
	 * フィールド {@link #SERVER_NAME} で指定されているサーバーから時刻を取得し、そのまま {@link Date} 型に変換して返却します。
	 * <p>
	 * @return 日本標準時
	 * @throws IOException 日本標準時取得時に入出力エラーが起こった場合
	 */
	public static Date JSTNow() throws IOException {
		NTPUDPClient client = new NTPUDPClient();
		InetAddress address = InetAddress.getByName(SERVER_NAME);
		client.open();
		TimeInfo info = client.getTime(Objects.requireNonNull(address));
		info.computeDetails();
		LOG.debug("NTPClient: {}", client);
		LOG.debug("NetAddress: {}", address);
		LOG.debug("Time info: {}", info);
		return validate(info);
	}

	/**
	 * 渡された引数から独自にオフセットを計算し、取得した情報との加重平均を取って返します。
	 * <p>
	 * 基本は取得オフセットと同じ値になることが予想されます。
	 * @param info サーバーから取得した情報
	 * @param packet サーバーから取得した情報のパケット
	 * @return
	 */
	private static Date validate(TimeInfo info) {
		final NtpV3Packet packet = info.getMessage();
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
