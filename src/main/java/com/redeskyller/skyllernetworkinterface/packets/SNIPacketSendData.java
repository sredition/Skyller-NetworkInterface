package com.redeskyller.skyllernetworkinterface.packets;

import com.redeskyller.skyllernetworkinterface.SNIPacket;

public class SNIPacketSendData extends SNIPacket {

	private static final long serialVersionUID = 515770105883119768L;

	private String target;
	private byte[] data;

	public SNIPacketSendData(final String target, final byte[] data)
	{
		this.target = target;
		this.data = data;
	}

	public String getTarget()
	{
		return this.target;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	public byte[] getData()
	{
		return this.data;
	}

	public void setData(byte[] data)
	{
		this.data = data;
	}

}
