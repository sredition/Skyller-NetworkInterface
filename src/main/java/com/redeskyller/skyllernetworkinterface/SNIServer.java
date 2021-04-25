package com.redeskyller.skyllernetworkinterface;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;
import com.redeskyller.skyllernetworkinterface.packets.SNIPacketSendData;

public class SNIServer extends Thread {

	public static final String PROTOCOL_NAME = "SNI";
	public static final String PROTOCOL_VERSION = "1.0";
	public static final String PROTOCOL_HEADER = (PROTOCOL_NAME.concat("/").concat(PROTOCOL_VERSION));

	private Logger log;
	private SNIServerSettings settings;
	private ServerSocket serverSocket;
	private Map<String, SNIClient> clients;

	public SNIServer(final Logger log, final SNIServerSettings settings) throws Exception
	{
		this.log = log;
		this.settings = settings;
		this.serverSocket = new ServerSocket(this.settings.getPort());
		this.clients = new HashMap<String, SNIClient>();
	}

	@Override
	public void run()
	{
		while (this.serverSocket != null) {

			try {
				Socket socket = this.serverSocket.accept();

				if (!(this.settings.isOnlyLocalhost() && !socket.getInetAddress().isLoopbackAddress())) {

					this.log.info("Recebendo conexão de '" + socket.getInetAddress().getHostAddress() + "'.");

					DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

					if (dataInputStream.readUTF().equals(PROTOCOL_HEADER)) {
						if (dataInputStream.readInt() == TimeBasedOneTimePasswordUtil
								.generateCurrentNumber(this.settings.getToken2FA())) {

							String clientName = dataInputStream.readUTF();
							if (clientName != null) {

								SNIClient client = new SNIClient(clientName, socket);

								client.setDataCallback(data -> {

									SNIPacketSendData packet = SNIPacket.deserialize(SNIPacketSendData.class, data);

									if (packet != null) {
										SNIClient targetClient = getClient(packet.getTarget());
										if (targetClient != null) {

											if (targetClient.checkConnection()) {
												targetClient.sendData(packet.getData());
											} else {

												targetClient.endConnection();
												this.clients.remove(targetClient.getName());

												this.log.info("Não foi possível conectar-se com '"
														+ targetClient.getName()
														+ "', a conexão foi removida e não está mais catalogada.");
											}
										}
									}
								});

								client.getDataListenerThread().start();

								if (this.clients.containsKey(clientName)) {
									this.clients.get(clientName).endConnection();
								}

								this.clients.put(clientName, client);

								this.log.info("A conexão foi aceita e catalogada com o nome de '" + clientName + "'.");
								continue;
							}

						} else {
							this.log.info("As credenciais de autenticação para conexão não são válidas.");
						}
					} else {
						this.log.info("A conexão não respeita os padrões do protocolo '" + PROTOCOL_HEADER + "'.");
					}
				}
				this.log.info("Conexão de '" + socket.getInetAddress().getHostAddress() + "' encerrada.");
				socket.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}

		}
	}

	public void end()
	{
		if (this.serverSocket != null) {
			try {
				this.serverSocket.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			this.serverSocket = null;
		}
	}

	public SNIClient getClient(String clientName)
	{
		return this.clients.get(clientName);
	}

}
