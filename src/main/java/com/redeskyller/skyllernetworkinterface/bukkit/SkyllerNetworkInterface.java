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
import com.redeskyller.skyllernetworkinterface.bukkit.util.SNIClientConnector;
import com.redeskyller.skyllernetworkinterface.packets.SNIPacketSendData;

public class SkyllerNetworkInterface extends JavaPlugin implements Listener {

	private static SkyllerNetworkInterface instance;

	private YamlConfiguration config;
	private String serverName;
	private SNITokenFile tokenFile;
	private SNIClientConnector connector;

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

		SNIClient client = new SNIClient(getServerName(), this.config.getString("proxyHost"),
				this.config.getInt("proxyPort"));

		client.setDataCallback(data -> {
			Bukkit.getPluginManager().callEvent(new SNIDataReceivedEvent(data));
		});

		this.connector = new SNIClientConnector(client, this.tokenFile.getToken());
		this.connector.start();
	}

	@Override
	public void onDisable()
	{
		if (this.connector != null) {
			this.connector.finish();
		}
	}

	public void sendPacket(String server, SNIPacket packet)
	{
		getConnector().getClient().sendPacket(new SNIPacketSendData(server, packet.serialize()));
	}

	public static SkyllerNetworkInterface getInstance()
	{
		return instance;
	}

	@Override
	public YamlConfiguration getConfig()
	{
		return this.config;
	}

	public String getServerName()
	{
		return this.serverName;
	}

	public SNITokenFile getTokenFile()
	{
		return this.tokenFile;
	}

	public SNIClientConnector getConnector()
	{
		return this.connector;
	}

}
