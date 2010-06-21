package de.lmu.ifi.mdsg.msp.wlan;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class EkahauPositionUpdater {
	private String server;
	private int ELPPort;
	private int EMPPort;
	private DatagramSocket udpClientELP;
	private DatagramSocket udpClientEMP;

	public EkahauPositionUpdater(String server, int elpPort, int empPort) {
		this.server = server;
		ELPPort = elpPort;
		EMPPort = empPort;
	}

	/**
	 * Connect to the Ekahau Positioning Engine via the Ekahau Location
	 * Protocol.
	 * 
	 * @return
	 */
	public boolean connectELP() {
		try {
			udpClientELP = new DatagramSocket();
			InetSocketAddress remoteAdress = new InetSocketAddress(server,
					ELPPort);
			udpClientELP.connect(remoteAdress);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void closeELP() {
		if (udpClientELP != null) {
			udpClientELP.close();
		}
	}

	public boolean connectEMP() {
		try {
			udpClientEMP = new DatagramSocket();
			InetSocketAddress remoteAddress = new InetSocketAddress(server,
					EMPPort);
			udpClientEMP.connect(remoteAddress);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void closeEMP() {
		udpClientEMP.close();
	}

	public boolean sendELP(List<EkahauAccessPoint> APList, byte[] myMAC)
			throws Exception {
		// Hier wird das Protokoll für den Ekahau Position definiert

		ArrayList<Byte> dataList = new ArrayList<Byte>();

		// Hier der Kopf des Protokolls

		dataList.add((byte) 0x45);
		dataList.add((byte) 0x4c);
		dataList.add((byte) 0x50);
		dataList.add((byte) 0x00);
		dataList.add((byte) 0x00);
		dataList.add((byte) 0x01);
		dataList.add((byte) 0x00);
		dataList.add((byte) 0x01);
		dataList.add((byte) 0x00);
		dataList.add((byte) 0x00);
		dataList.add((byte) 0x00);
		dataList.add((byte) 0x00);

		// Hier wird die Anzahl der AccessPoints übergeben
		/*
		 * Nach folgender Tabelle: Hexcode für 2 Byte | Anzahl APs
		 * 
		 * 00 10 0 00 20 1 00 30 2 00 40 3 00 50 4 00 60 5 00 70 6 00 80 7 00 90
		 * 8 00 a0 9 00 b0 10 00 c0 11 00 d0 12 00 e0 13 00 f0 14 01 00 15 01 10
		 * 16 01 20 17 01 30 18 01 40 19 01 50 20 01 60 21 01 70 22 01 80 23 01
		 * 90 24 01 a0 25 01 b0 26 01 c0 27 01 d0 28 01 e0 29 01 f0 30
		 */
		// Berechnung nach folgender Formel (Anzahl APs + 1) * 16 -> Hex

		int first, last;
		int counterfirst = 2;
		int counterlast = 2;
		first = 0;
		last = 16;

		for (int i = 0; i < APList.size(); i++) {
			counterfirst++;
			if (counterfirst == 17) {
				first++;
				counterfirst = 0;
			}
			counterlast++;
			last += 16;
			if (counterlast == 17) {
				last = 0;
				counterlast = 0;
			}
		}

		dataList.add((byte) 0x00); // Die ersten
		dataList.add((byte) 0x00); // zwei werden nicht gebraucht, da 400 000
									// APs reichen sollten
		dataList.add((byte) first);
		dataList.add((byte) last);

		// MAC Addresse des Gerätes
		for (byte b : myMAC) {
			dataList.add(b);
		}

		dataList.add((byte) 0x2e);
		dataList.add((byte) 0xe1);
		dataList.add((byte) 0x00);

		// int BatteryLife =
		// (int)Microsoft.WindowsMobile.Status.SystemState.PowerBatteryStrength;

		// FIXME insert current battery level here
		dataList.add((byte) 0); // Das ist die Batterieladung in %

		dataList.add((byte) 0x00);
		dataList.add((byte) 0x00);
		dataList.add((byte) 0x00);
		dataList.add((byte) 0x00);
		dataList.add((byte) 0x3f);
		dataList.add((byte) 0xff);

		// Hier kommen die Accesspoints
		// Zuerst MAC Addresse, dann ff dann Signalstärk dann 00 dann channel
		// dann 6 * 00

		// Alle Accesspoints
		for (EkahauAccessPoint AP : APList) {
			// MAC Addresse
			for (byte bt : AP.MAC) {
				dataList.add(bt);
			}

			// ff

			dataList.add((byte) 0xff);

			// Signalstärke

			// Umrechnung für Ekahau läuft folgendermaßen: Signalstärke (mit
			// Vorzeichen) - 1 -> Hex

			int val = AP.SignalStrength - 1;

			dataList.add((byte) val);

			// 00 Trennbyte

			dataList.add((byte) 0);

			// Channel

			dataList.add((byte) AP.channel);

			// 6 * 00 Trennbyte
			dataList.add((byte) 0);
			dataList.add((byte) 0);
			dataList.add((byte) 0);
			dataList.add((byte) 0);
			dataList.add((byte) 0);
			dataList.add((byte) 0);
		}

		Object[] dataArray = dataList.toArray();
		int length = dataList.size();
		byte[] data = new byte[length];

		for (int i = 0; i < length; i++) {
			data[i] = (Byte) dataArray[i];
		}

		try {
			DatagramPacket packet = new DatagramPacket(data, length,
					udpClientELP.getRemoteSocketAddress());
			udpClientELP.send(packet);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
}