package com.redeskyller.skyllernetworkinterface.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SNIDataReceivedEvent extends Event {

	private static final HandlerList handlerList = new HandlerList();

	private final byte[] data;

	public SNIDataReceivedEvent(final byte[] data)
	{
		this.data = data;
	}

	public byte[] getData()
	{
		return data;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlerList;
	}

	public static HandlerList getHandlerList()
	{
		return handlerList;
	}

}
