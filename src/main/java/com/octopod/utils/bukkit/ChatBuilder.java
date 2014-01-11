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
 * Last Updated: 1.11.2014
 * ChatBuilder to build messages for Minecraft's new JSON chat.
 * Utitlizes "method chaining."
 * @author Octopod
 */
public class ChatBuilder {

	private List<ChatElement> allElements = new ArrayList<ChatElement>();
	private ChatElement currentElement = null;

	private boolean inRange(int i) {
		return (i >= 0 && i < allElements.size()) ? true : false;
	}
	
	private boolean exists() {
		return (currentElement == null) ? false : true;
	}
	
	/**
	 * The total amount of elements.
	 * @return size of elements.
	 */					

	public int size() {return allElements.size();}
	
	/**
	 * Gets the list of all ChatElements.
	 * @return ArrayList of ChatElements.
	 */					
	
	public List<ChatElement> getChatElements() {return allElements;}
	
	/**
	 * Gets the last ChatElement
	 * @return The last ChatElement.
	 */			
	
	public ChatElement getLastElement() {
		return allElements.get(allElements.size() - 1);
	}
		
	/**
	 * Gets the currently selected ChatElement.
	 * @return The currently selected ChatElement.
	 */		
	
	public ChatElement getCurrentElement() {
		return currentElement;
	}
	
	/**
	 * Manually selects the current ChatElement.
	 * @param index The index to select.
	 */		
	
	public ChatBuilder select(int index) {
		if(inRange(index)) currentElement = allElements.get(index);
		return this;
	}
	
	/**
	 * Pushes a new message to the end of the ChatBuilder, as a new ChatElement.
	 * It will also select the last element.
	 * @param message The message to push.
	 */		
	
	public ChatBuilder push(String message) {
		allElements.add(new ChatElement(message));
		return select(size() - 1);
	}
	
	/**
	 * Pushes a new ChatElement to the end of the ChatBuilder.
	 * It will also select the last element.
	 * @param element The ChatElement to push.
	 */		
	
	public ChatBuilder push(ChatElement element) {
		allElements.add(element);
		return select(size() - 1);
	}
	
	/**
	 * Sets the hover event of the currently selected ChatElement.
	 * @param event The ChatHoverEvent to use.
	 * @param value The value, as a string.
	 */	
	
	public ChatBuilder click(ChatClickEvent event, String value) {
		if(exists())
			currentElement.click(event, value);
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
		
	/**
	 * Sets the hover event of the currently selected ChatElement.
	 * @param event The ChatHoverEvent to use.
	 * @param value The value, as a string.
	 */
		
	public ChatBuilder hover(ChatHoverEvent event, String value) {
		if(exists())
			currentElement.hover(event, value);
		return this;
	}
	
		public ChatBuilder tooltip(String text) {
			return hover(ChatHoverEvent.SHOW_TEXT, text);
		}
		
		public ChatBuilder achievement(String name) {
			return hover(ChatHoverEvent.SHOW_ACHIEVEMENT, name);
		}
		
		public ChatBuilder item(ItemStack item) {
			return hover(ChatHoverEvent.SHOW_ITEM, itemtoJSON(item));
		}
		
	/**
	 * Change the color of the currently selected ChatElement. Non-color ChatColors will be ignored.
	 * @param ChatColor The new color of the current element.
	 */	
		
	public ChatBuilder color(ChatColor c) {
		if(exists())
			currentElement.color(c);
		return this;
	}
	
	/**
	 * Apply formats to the currently selected ChatElement. Non-format ChatColors will not apply.
	 * @param ChatColor... The formats to apply to the current element.
	 */
	
	public ChatBuilder format(ChatColor... formats) {
		if(exists())
			for(ChatColor format: formats) currentElement.format(format);
		return this;
	}
	
	//Shortcuts for style()
	
	public ChatBuilder bold() 			{return format(ChatColor.BOLD);}
	public ChatBuilder italic() 		{return format(ChatColor.ITALIC);}
	public ChatBuilder underline() 		{return format(ChatColor.UNDERLINE);}
	public ChatBuilder strikethrough() 	{return format(ChatColor.STRIKETHROUGH);}
	public ChatBuilder obfuscate() 		{return format(ChatColor.MAGIC);}
	
	/**
	 * Sends the player this object represented as a chat message.
	 * @param player The player that the message will be sent to.
	 */
	
	public void send(Player player) {
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(this.toString()));
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);		
	}
	
	/**
	 * Returns this object as a legacy chat string. Actually just a shortcut to the static toLegacy method.
	 * @return Legacy chat string
	 */
	
	public String toLegacy() {
		return ChatBuilder.toLegacy(this);
	}
	
	//=======================================================================================
	// Static Methods Below
	//=======================================================================================

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

	/**
	 * Converts a ChatBuilder object to Minecraft legacy chat string. 
	 * Obviously, hover and click events won't carry over.
	 * @param builder The ChatBuilder object to convert
	 * @return The legacy chat string.
	 */
	
	public static String toLegacy(ChatBuilder builder, char colorSymbol) {
		
		StringBuilder sb = new StringBuilder();
		
		for(ChatElement e: builder.getChatElements()) {
			sb.append(colorSymbol + e.getColor().getChar());
			for(ChatColor style: e.getFormats())
				sb.append(colorSymbol + style.getChar());
			sb.append(e.getText());
		}
		
		return sb.toString();
		
	}
	
	public static String toLegacy(ChatBuilder builder) {return toLegacy(builder, '&');}
	
	/**
	 * Converts Minecraft legacy chat to a ChatBuilder object.
	 * @param message The legacy chat string to convert
	 * @return A new ChatBuilder object.
	 */
	
	public static ChatBuilder fromLegacy(String message, char colorSymbol) {
		ChatBuilder builder = new ChatBuilder();

		StringBuilder text = new StringBuilder();
		boolean nextIsColorCode = false;
		ChatColor lastColor = ChatColor.WHITE;
		List<ChatColor> styles = new ArrayList<ChatColor>();

		for(char c: message.toCharArray()) {
			
			if(c == colorSymbol) {
				nextIsColorCode = true;
				continue;
			}
			
			if(nextIsColorCode) {
				nextIsColorCode = false;
				ChatColor color = ChatColor.getByChar(c);
				if(color != null) {
					if(color.isColor()) {
						//Push new element
						if(!text.toString().equals("")) builder.push(text.toString()).color(lastColor).format(styles.toArray(new ChatColor[styles.size()]));
						//Reset variables
						text = new StringBuilder();
						lastColor = color;
						styles = new ArrayList<ChatColor>();
					}
					if(color.isFormat())
						styles.add(color);
				}
				continue;
			}
			
			text.append(c);
			
		}
		
		builder.push(text.toString()).color(lastColor).format(styles.toArray(new ChatColor[styles.size()]));
		
		return builder;
	}
	
	public static ChatBuilder fromLegacy(String message) {return fromLegacy(message, '&');}
	
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
	
	//=======================================================================================
	// toString() Method
	//=======================================================================================
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public String toString() {
		Map json = new HashMap();
		
		json.put("text", "");
		json.put("extra", allElements);
		
		return JSONValue.toJSONString(json);
	}
		
}
