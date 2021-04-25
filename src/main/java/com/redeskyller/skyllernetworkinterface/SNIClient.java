package com.redeskyller.skyllernetworkinterface;

import static com.redeskyller.skyllernetworkinterface.SNIServer.PROTOCOL_HEADER;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;

public class SNIClient implements Runnable {

	private Socket socket;

	private String name;
	private String hostname;
	private int port;

	private Thread dataListenerThread;

	private SNIDataCallback dataCallback;

	public SNIClient(final String name, final Socket socket)
	{
		this(name, socket.getInetAddress().getHostAddress(), socket.getPort());
		this.socket = socket;
	}

	public SNIClient(final String name, final String hostname, final int port)
	{
		this.name = name;
		this.hostname = hostname;
		this.port = port;

		this.dataListenerThread = new Thread(this);
	}

	public void connect(String token2fa)
	{
		this.connect(token2fa, false);
	}

	public void connect(String token2fa, boolean silent)
	{
		try {
			endConnection();

			this.socket = new Socket(this.hostname, this.port);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
				dataOutputStream.writeUTF(PROTOCOL_HEADER);
				dataOutputStream.writeInt(TimeBasedOneTimePasswordUtil.generateCurrentNumber(token2fa));
				dataOutputStream.writeUTF(this.name);
			}

			this.socket.getOutputStream().write(byteArrayOutputStream.toByteArray());
			this.socket.getOutputStream().flush();

			this.dataListenerThread = new Thread(this);
			this.dataListenerThread.start();
		} catch (Exception exception) {
			if (!silent) {
				exception.printStackTrace();
			}
		}
	}

	public boolean checkConnection()
	{
		try {
			DataOutputStream dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
			dataOutputStream.writeInt(0);
			dataOutputStream.flush();
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	public void sendPacket(SNIPacket packet)
	{
		if (packet != null) {
			sendData(packet.serialize());
		}
	}

	public void sendData(byte[] data)
	{
		if ((data != null) && (data.length > 0)) {

			try {
				DataOutputStream dataOutputStream = new DataOutputStream(this.socket.getOutputStream());

				dataOutputStream.writeInt(data.length);
				dataOutputStream.write(data);
				dataOutputStream.flush();

			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void endConnection()
	{
		try {
			if (this.socket != null) {
				this.socket.close();
				this.socket = null;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		while ((this.socket != null) && !this.socket.isClosed()) {
			try {
				DataInputStream dataInputStream = new DataInputStream(this.socket.getInputStream());
				if (dataInputStream.available() > 0) {
					byte[] data = new byte[dataInputStream.readInt()];
					for (int i = 0; i < data.length; i++) {
						data[i] = dataInputStream.readByte();
					}

					if ((data.length > 0) && (this.dataCallback != null)) {
						this.dataCallback.call(data);
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public Socket getSocket()
	{
		return this.socket;
	}

	public Thread getDataListenerThread()
	{
		return this.dataListenerThread;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getHostname()
	{
		return this.hostname;
	}

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}

	public int getPort()
	{
		return this.port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public SNIDataCallback getDataCallback()
	{
		return this.dataCallback;
	}

	public void setDataCallback(SNIDataCallback dataCallback)
	{
		this.dataCallback = dataCallback;
	}

}
