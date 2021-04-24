package com.redeskyller.skyllernetworkinterface.bukkit;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.redeskyller.skyllernetworkinterface.SNIClient;
import com.redeskyller.skyllernetworkinterface.SNIPacket;
import com.redeskyller.skyllernetworkinterface.SNITokenFile;
import com.redeskyller.skyllernetworkinterface.bukkit.events.SNIDataReceivedEvent;
import com.redeskyller.skyllernetworkinterface.packets.SNIPacketSendData;

public class SkyllerNetworkInterface extends JavaPlugin implements Listener {

	private static SkyllerNetworkInterface instance;

	private YamlConfiguration config;
	private String serverName;
	private SNITokenFile tokenFile;
	private SNIClient client;

	@Override
	public void onEnable()
	{
		instance = this;

		getDataFolder().mkdirs();

		File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			saveResource("config.yml", false);
		}
		this.config = YamlConfiguration.loadConfiguration(configFile);
		this.serverName = this.config.getString("serverName");

		this.tokenFile = new SNITokenFile(new File(getDataFolder(), "secret.token"));

		this.client = new SNIClient(getServerName(), this.config.getString("proxyHost"),
				this.config.getInt("proxyPort"));

		this.client.setDataCallback(data -> {
			Bukkit.getPluginManager().callEvent(new SNIDataReceivedEvent(data));
		});

		this.client.connect(this.tokenFile.getToken());
	}

	@Override
	public void onDisable()
	{
		if (this.client != null) {
			this.client.end();
		}
	}

	public void sendPacket(String server, SNIPacket packet)
	{
		getClient().sendPacket(new SNIPacketSendData(server, packet.serialize()));
	}

	public static SkyllerNetworkInterface getInstance()
	{
		return instance;
	}

	public YamlConfiguration getConfig()
	{
		return config;
	}

	public String getServerName()
	{
		return serverName;
	}

	public SNITokenFile getTokenFile()
	{
		return tokenFile;
	}

	public SNIClient getClient()
	{
		return client;
	}

}
