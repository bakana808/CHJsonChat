package com.octopod.utils.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_7_R1.ChatSerializer;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONValue;

import com.octopod.utils.bukkit.ChatElement.ChatClickEvent;
import com.octopod.utils.bukkit.ChatElement.ChatHoverEvent;

/**
 * @author Octopod
 */
public class ChatBuilder {
	
	List<ChatElement> elements = new ArrayList<ChatElement>();
	ChatElement subject = null;
	
	public static ChatBuilder fromLegacy(String message) {
		ChatBuilder builder = new ChatBuilder();
		return builder;
	}
	
	private boolean exists() {
		if(subject == null)
			return false;
			return true;
	}
	
	public int size() {return elements.size();}
	
	public ChatBuilder select(int index) {
		if(index < 0 || index >= elements.size())
			subject = elements.get(index);
		return this;
	}
	
	public ChatBuilder push(String message) {
		elements.add(new ChatElement(message));
		subject = elements.get(elements.size() - 1);
		return this;
	}
	
	public ChatBuilder push(ChatElement element) {
		elements.add(element);
		subject = elements.get(elements.size() - 1);
		return this;
	}
	
	public ChatBuilder click(ChatClickEvent event, String value) {
		if(exists())
			subject.setOnClick(event, value);
		return this;
	}
		
		public ChatBuilder run(String command) {
			return click(ChatClickEvent.RUN_COMMAND, command);
		}
		
		public ChatBuilder suggest(String command) {
			return click(ChatClickEvent.SUGGEST_COMMAND, command);
		}
		
		public ChatBuilder link(String url) {
			return click(ChatClickEvent.OPEN_URL, url);
		}
		
		public ChatBuilder file(String path) {
			return click(ChatClickEvent.OPEN_FILE, path);
		}
	
	public ChatBuilder hover(ChatHoverEvent event, String value) {
		if(exists())
			subject.setOnHover(event, value);
		return this;
	}
	
		public ChatBuilder tooltip(String text) {
			return hover(ChatHoverEvent.SHOW_TEXT, text);
		}
		
		public ChatBuilder item(ItemStack item) {
			return hover(ChatHoverEvent.SHOW_ITEM, itemtoJSON(item));
		}
	
	public ChatBuilder color(ChatColor c) {
		if(exists())
			subject.color(c);
		return this;
	}
	
	public ChatBuilder style(ChatColor... style) {
		if(exists())
			for(ChatColor s: style) subject.style(s);
		return this;
	}
	
	public ChatBuilder bold() 			{return style(ChatColor.BOLD);}
	public ChatBuilder italic() 		{return style(ChatColor.ITALIC);}
	public ChatBuilder underline() 		{return style(ChatColor.UNDERLINE);}
	public ChatBuilder strikethrough() 	{return style(ChatColor.STRIKETHROUGH);}
	public ChatBuilder obfuscate() 		{return style(ChatColor.MAGIC);}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public String toString() {
		Map json = new HashMap();
		
		json.put("text", "");
		json.put("extra", elements);
		
		return JSONValue.toJSONString(json);
	}
	
	public void send(Player player) {
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(this.toString())));		
	}
	
	public String toLegacy() {
		
		StringBuilder sb = new StringBuilder();
		for(ChatElement e: elements) {
			for(ChatColor s: e.getStyles())
				sb.append(s);
			sb.append(e.getColor());
			sb.append(e.getText());
		}
		return sb.toString();
		
	}

	public static ChatColor stringToChatColor(String color) throws IllegalArgumentException {
		switch(color.toUpperCase()) {
			case "obfuscated":
				return ChatColor.MAGIC;
			case "underlined":
				return ChatColor.UNDERLINE;
			default:
				return ChatColor.valueOf(color.toUpperCase());
		}
	}
	
	public static String stringFromChatColor(ChatColor color) {
		switch(color) {
			case MAGIC:
				return "obfuscated";
			case UNDERLINE:
				return "underlined";
			default:
				return color.name().toLowerCase();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static String itemtoJSON(ItemStack item) {
		
		Map<String, Object> json = new HashMap<String, Object>();
		Map<String, Object> meta = new HashMap<String, Object>();
		Map<String, Object> display = new HashMap<String, Object>();
		
		json.put("id", item.getTypeId());
		json.put("Damage", (int)item.getData().getData());
		json.put("Count", item.getAmount());
		
		try{
			display.put("Name", item.getItemMeta().getDisplayName());
			meta.put("display", display);
		} catch (NullPointerException e) {}
	
		json.put("tag", meta);
		
		return JSONValue.toJSONString(json);
		
	}

}
