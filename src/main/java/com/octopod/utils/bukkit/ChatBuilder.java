package com.octopod.utils.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONValue;

import com.octopod.utils.bukkit.ChatUtils.ClickEvent;
import com.octopod.utils.bukkit.ChatUtils.Color;
import com.octopod.utils.bukkit.ChatUtils.Format;
import com.octopod.utils.bukkit.ChatUtils.HoverEvent;
import com.octopod.utils.bukkit.ChatUtils.Alignment;

/**
 * Last Updated: 2.1.2014
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
	 * Gets the ChatElement at the specified index. Returns null if out of bounds.
	 * @return The ChatElement from the index, or null if not found.
	 */		
	public ChatElement getElementAt(int i) {
		try {
			return allElements.get(i);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
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
	public ChatBuilder append(String message) {
		allElements.add(new ChatElement(message));
		return select(size() - 1);
	}
	
	/**
	 * Pushes a new ChatElement to the end of the ChatBuilder.
	 * It will also select the last element.
	 * @param element The ChatElement to push.
	 */		
	public ChatBuilder append(ChatElement element) {
		allElements.add(element);
		return select(size() - 1);
	}
	
	/**
	 * Pushes filler to the end of the ChatBuilder, as a new ChatElement.
	 * Fillers fit text to a pixel width (according to Minecraft's default font)
	 * Fillers will contain filler characters if the width is too abnormal.
	 * If you want to avoid filler characters, make sure the width is divisible by 4. (the width of a space)
	 * Unexpected behavior might occur if used with the translate feature of MC's new chat system.
	 * It will also select the last element.
	 * @param width The width of the filler.
	 */		
	public ChatBuilder appendFiller(int width) { 
		allElements.add(ChatUtils.filler(width));
		return select(size() - 1);
	}
	
	/**
	 * Pushes a block (text + filler) to the end of the ChatBuilder, as a new ChatElement.
	 * Fillers fit text to a pixel width (according to Minecraft's default font)
	 * Fillers will contain filler characters if the width is too abnormal.
	 * If you want to avoid filler characters, make sure the width is divisible by 4. (the width of a space)
	 * Unexpected behavior might occur if used with the translate feature of MC's new chat system.
	 * It will also select the last element.
	 * @param element The element to use as base text.
	 * @param width The width of the block of text.
	 * @param alignment The alignment to use (Left, Right, Center)
	 */	
	public ChatBuilder appendBlock(ChatElement element, int width, Alignment alignment) {
		element.text(ChatUtils.cut(element.getText(), width));
		return append(element).fit(width);
	}	
	
	public ChatBuilder appendBlock(ChatElement element, int width) {
		return appendBlock(element, width, Alignment.LEFT);
	}	
	
	/**
	 * Fits the current element to be size 'width'. 
	 * If the width of the text from the current element is longer than 'width', some trunctation might occur.
	 * @param width the width to fit to
	 */
	public ChatBuilder fit(int width) {
		currentElement.text(ChatUtils.cut(currentElement.getText(), width));
		int fillerWidth = width - ChatUtils.width(currentElement.getText());
		currentElement.extra(ChatUtils.filler(fillerWidth));
		return this;
	}
	
	/**
	 * Returns the width of the current element in pixels, according to Minecraft's default font.
	 * @return the width of the current element, in pixels.
	 */
	public int width() {
		return ChatUtils.width(currentElement.getText());
	}
	
	/**
	 * Sets the hover event of the currently selected ChatElement.
	 * @param event The ChatHoverEvent to use.
	 * @param value The value, as a string.
	 */
	public ChatBuilder click(ClickEvent event, String value) {
		if(exists())
			currentElement.click(event, value);
		return this;
	}
		
		public ChatBuilder run(String command) {
			return click(ClickEvent.RUN_COMMAND, command);
		}
		
		public ChatBuilder suggest(String command) {
			return click(ClickEvent.SUGGEST_COMMAND, command);
		}
		
		public ChatBuilder link(String url) {
			return click(ClickEvent.OPEN_URL, url);
		}
		
		public ChatBuilder file(String path) {
			return click(ClickEvent.OPEN_FILE, path);
		}
		
	/**
	 * Sets the hover event of the currently selected ChatElement.
	 * @param event The ChatHoverEvent to use.
	 * @param value The value, as a string.
	 */
	public ChatBuilder hover(HoverEvent event, String value) {
		if(exists())
			currentElement.hover(event, value);
		return this;
	}
	
		public ChatBuilder tooltip(String text) {
			return hover(HoverEvent.SHOW_TEXT, text);
		}
		
		public ChatBuilder achievement(String name) {
			return hover(HoverEvent.SHOW_ACHIEVEMENT, name);
		}
		
		public ChatBuilder item(ItemStack item) {
			return hover(HoverEvent.SHOW_ITEM, ChatUtils.itemtoJSON(item));
		}
		
	/**
	 * Change the color of the currently selected ChatElement. Non-color ChatColors will be ignored.
	 * @param ChatColor The new color of the current element.
	 */	
	public ChatBuilder color(Color c) {
		if(exists())
			currentElement.color(c);
		return this;
	}
	
	/**
	 * Apply formats to the currently selected ChatElement. Non-format ChatColors will not apply.
	 * @param ChatColor... The formats to apply to the current element.
	 */
	public ChatBuilder format(Format... formats) {
		if(exists())
			currentElement.format(formats);
		return this;
	}
	
	//Shortcuts for format()
	
	public ChatBuilder bold() 			{return format(Format.BOLD);}
	public ChatBuilder italic() 		{return format(Format.ITALIC);}
	public ChatBuilder underline() 		{return format(Format.UNDERLINED);}
	public ChatBuilder strikethrough() 	{return format(Format.STRIKETHROUGH);}
	public ChatBuilder obfuscate() 		{return format(Format.OBFUSCATED);}
	
	/**
	 * Sends the player this object represented as a chat message.
	 * @param player The player that the message will be sent to.
	 */
	public void send(Player player) {
		ChatUtils.send(player, this);
	}
	
	/**
	 * Returns this object as a legacy chat string. Actually just a shortcut to the static toLegacy method.
	 * @return Legacy chat string
	 */
	public String toLegacy() {
		return ChatUtils.toLegacy(this);
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
