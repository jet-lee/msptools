package de.lmu.ifi.mdsg.msp.wlan;

public class EkahauAccessPoint
{
    public String SSID;
    public byte[] MAC;
    public int channel;
    public int SignalStrength;

    public EkahauAccessPoint(String ssid, byte[] mac, int achannel, int signalstrength)
    {
        SSID = ssid;
        MAC = mac;
        channel = achannel;
        SignalStrength = signalstrength;
    }
}
