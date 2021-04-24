package com.redeskyller.skyllernetworkinterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("unchecked")
public abstract class SNIPacket implements Serializable {

	private static final long serialVersionUID = -5310023689446845025L;

	public byte[] serialize()
	{
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
				objectOutputStream.writeObject(this);
			}
			return byteArrayOutputStream.toByteArray();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public static <V> V deserialize(Class<V> packetClass, byte[] data)
	{
		if (packetClass != null) {
			SNIPacket packet = deserialize(data);
			if ((packet != null) && (packetClass.equals(packet.getClass())))
				return ((V) packet);
		}
		return null;
	}

	public static SNIPacket deserialize(byte[] data)
	{
		try {
			if ((data != null) && (data.length > 0)) {
				SNIPacket packet = null;
				try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
					packet = ((SNIPacket) objectInputStream.readObject());
				}
				return packet;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

}
