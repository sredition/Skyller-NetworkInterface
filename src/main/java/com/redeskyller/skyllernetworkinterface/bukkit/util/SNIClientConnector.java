package com.redeskyller.skyllernetworkinterface.bukkit.util;

import com.redeskyller.skyllernetworkinterface.SNIClient;

public class SNIClientConnector extends Thread {

	private SNIClient client;
	private String token2fa;

	public SNIClientConnector(final SNIClient client, final String token2fa)
	{
		this.client = client;
		this.token2fa = token2fa;
	}

	@Override
	public void run()
	{
		while (this.client != null) {
			try {
				if (!this.client.checkConnection()) {
					this.client.connect(this.token2fa, true);
				}
				Thread.sleep(1000L);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void finish()
	{
		if (this.client != null) {
			this.client.endConnection();
			this.client = null;
		}
	}

	public SNIClient getClient()
	{
		return this.client;
	}

}
