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
	
	public ChatElement() {this("");}
	public ChatElement(String text) {this.text = text;}

	public static enum ChatClickEvent {OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND}
	public static enum ChatHoverEvent {SHOW_TEXT, SHOW_ACHIEVEMENT, SHOW_ITEM}

	private String text = "";
	private boolean translate = false;
	private List<String> with = new ArrayList<String>();
	
	private ChatColor color = ChatColor.WHITE;	
	private List<ChatColor> formats = new ArrayList<ChatColor>();
	
	private ChatClickEvent clickEvent = null;
	private String clickEvent_value = "";
	
	private ChatHoverEvent hoverEvent = null;
	private String hoverEvent_value = "";
	
	public ChatClickEvent getClick() {return clickEvent;}
	public ChatHoverEvent getHover() {return hoverEvent;}
	public String getClickValue() {return clickEvent_value;}
	public String getHoverValue() {return hoverEvent_value;}
	
	public String getText() {return text;}
	public ChatColor getColor() {return color;}
	public List<ChatColor> getFormats() {return formats;}
	
	public void translate(boolean t) {translate = t;}
	
	public void color(ChatColor color) {
		if(color.isColor())
			this.color = color;
	}
	
	public void format(ChatColor format) {
		if(format.isFormat() && !formats.contains(format))
			formats.add(format);
	}
	
	public void format_remove(ChatColor format) {
		if(format.isFormat() && formats.contains(format))
			formats.remove(format);
	}
	
	public void click(ChatClickEvent event, String value) {
		clickEvent = event;
		clickEvent_value = value;
	}
	
	public void hover(ChatHoverEvent event, String value) {
		hoverEvent = event;
		hoverEvent_value = value;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String toString() {

		Map<String, Object> json = new HashMap();
		
		if(translate) {
			json.put("translate", text);			
			if(with.size() > 0)
				json.put("with", with);
		} else {
			json.put("text", text);
		}

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

		for(ChatColor style: formats)
			json.put(ChatBuilder.stringFromChatColor(style), true);
		
		json.put("color", color.name().toLowerCase());

		return JSONValue.toJSONString(json);
		
	}

}
