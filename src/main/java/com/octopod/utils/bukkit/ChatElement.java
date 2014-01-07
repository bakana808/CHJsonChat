package com.octopod.utils.bukkit;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.json.simple.JSONValue;

/**
 * @author Octopod
 */
public class ChatElement {
	
	public ChatElement(String text) {this.text = text;}

	public static enum ChatClickEvent {OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND}
	public static enum ChatHoverEvent {SHOW_TEXT, SHOW_ACHIEVEMENT, SHOW_ITEM}

	private String text = "";
	private boolean translate = false;
	private List<String> with = new ArrayList<String>();
	
	private List<ChatColor> styles = new ArrayList<ChatColor>();

	private ChatColor color = ChatColor.WHITE;
	
	private ChatClickEvent clickEvent = null;
	private Object clickEvent_value = "";
	
	private ChatHoverEvent hoverEvent = null;
	private Object hoverEvent_value = "";
	
	public String getText() {return text;}
	public ChatColor getColor() {return color;}
	public List<ChatColor> getStyles() {return styles;}
	
	public void color(ChatColor color) {
		if(color.isColor())
			this.color = color;
	}
	
	public void style(ChatColor style) {
		if(style.isFormat() && !styles.contains(style))
			styles.add(style);
	}
	
	public void style_remove(ChatColor style) {
		if(style.isFormat() && styles.contains(style))
			styles.remove(style);
	}
	
	public void setOnClick(ChatClickEvent event, String value) {
		clickEvent = event;
		clickEvent_value = value;
	}
	
	public void setOnHover(ChatHoverEvent event, String value) {
		hoverEvent = event;
		hoverEvent_value = value;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String toString() {

		Map<String, Object> json = new HashMap();
		
		if(translate) {
			json.put("translate", text);
		} else {
			json.put("text", text);
		}
		
		if(with.size() > 0)
			json.put("with", with);

		if(clickEvent != null) {
			Map click = new HashMap();
				click.put("action", clickEvent.name().toLowerCase());
				click.put("value", clickEvent_value);
			json.put("clickEvent", click);
		}
		
		if(hoverEvent != null) {
			Map hover = new HashMap();
				hover.put("action", hoverEvent.name().toLowerCase());
				hover.put("value", hoverEvent_value);
			json.put("hoverEvent", hover);
		}

		for(ChatColor style: styles)
			json.put(ChatBuilder.stringFromChatColor(style), true);
		
		json.put("color", color.name().toLowerCase());

		return JSONValue.toJSONString(json);
		
	}

}
