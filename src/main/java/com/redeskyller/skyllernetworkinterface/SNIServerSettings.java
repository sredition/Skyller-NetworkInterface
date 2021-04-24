package com.redeskyller.skyllernetworkinterface;

public class SNIServerSettings {

	private final int port;
	private final String token2fa;
	private boolean onlyLocalhost;

	public SNIServerSettings(final int port, final String token2fa)
	{
		this(port, token2fa, true);
	}

	public SNIServerSettings(final int port, final String token2fa, boolean onlyLocalhost)
	{
		this.port = port;
		this.token2fa = token2fa;
		this.onlyLocalhost = onlyLocalhost;
	}

	public String getToken2FA()
	{
		return this.token2fa;
	}

	public boolean isOnlyLocalhost()
	{
		return this.onlyLocalhost;
	}

	public void setOnlyLocalhost(boolean onlyLocalhost)
	{
		this.onlyLocalhost = onlyLocalhost;
	}

	public int getPort()
	{
		return this.port;
	}

}
