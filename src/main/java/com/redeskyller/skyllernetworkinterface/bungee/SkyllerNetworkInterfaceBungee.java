package com.redeskyller.skyllernetworkinterface.bungee;

import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import com.redeskyller.skyllernetworkinterface.SNIServer;
import com.redeskyller.skyllernetworkinterface.SNIServerSettings;
import com.redeskyller.skyllernetworkinterface.SNITokenFile;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class SkyllerNetworkInterfaceBungee extends Plugin {

	private static SkyllerNetworkInterfaceBungee instance;

	private Configuration serverConfig;
	private SNITokenFile tokenFile;

	private SNIServer server;

	@Override
	public void onEnable()
	{
		try {
			getDataFolder().mkdirs();

			File serverConfigFile = new File(getDataFolder(), "server.yml");
			if (!serverConfigFile.exists()) {
				Files.copy(getResourceAsStream(serverConfigFile.getName()), serverConfigFile.toPath(),
						new CopyOption[0]);
			}
			this.serverConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(serverConfigFile);
			this.tokenFile = new SNITokenFile(new File(getDataFolder(), "secret.token"));

			this.server = new SNIServer(getLogger(), new SNIServerSettings(this.serverConfig.getInt("serverPort"),
					this.tokenFile.getToken(), this.serverConfig.getBoolean("onlyLocalConnections")));

			this.server.start();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	@Override
	public void onDisable()
	{
		if (this.server != null) {
			this.server.end();
		}
	}

	public static SkyllerNetworkInterfaceBungee getInstance()
	{
		return instance;
	}

	public Configuration getServerConfig()
	{
		return this.serverConfig;
	}

	public SNITokenFile getTokenFile()
	{
		return this.tokenFile;
	}

	public SNIServer getServer()
	{
		return this.server;
	}
}
