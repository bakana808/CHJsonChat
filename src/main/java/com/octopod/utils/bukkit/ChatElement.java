package com.octopod.utils.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

import com.octopod.utils.bukkit.ChatUtils.ClickEvent;
import com.octopod.utils.bukkit.ChatUtils.Color;
import com.octopod.utils.bukkit.ChatUtils.Format;
import com.octopod.utils.bukkit.ChatUtils.HoverEvent;

/**
 * Last Updated: 2.1.2014
 * ChatElement is a representation of a Chat Object in Minecraft's new JSON chat.
 * Utitlizes "method chaining."
 * @author Octopod
 */
public class ChatElement {
	
	private ChatBuilder builder = null;
	
	public ChatElement() {this("");}
	
	public ChatElement(String text) {this(text, null);}
	
	public ChatElement(String text, ChatBuilder builder) {
		this.text = text;
		this.builder = builder;
	}
	
	private List<ChatElement> extras = new ArrayList<ChatElement>();
	
	private String text = "";
	private boolean translate = false;
	private List<String> with = new ArrayList<String>();
	
	private Color color = Color.WHITE;	
	private List<Format> formats = new ArrayList<Format>();
	
	private ClickEvent clickEvent = null;
	private String clickEvent_value = "";
	
	private HoverEvent hoverEvent = null;
	private String hoverEvent_value = "";
	
	//Variable getters
	public ClickEvent 	getClick() 		{return clickEvent;}
	public HoverEvent 	getHover() 		{return hoverEvent;}
	public String 		getClickValue() {return clickEvent_value;}
	public String 		getHoverValue() {return hoverEvent_value;}
	public String 		getText() 		{return text;}
	public Color 		getColor() 		{return color;}
	public List<Format> getFormats() 	{return formats;}
	
	/**
	 * Pushes elements to the ArrayList 'extras', which will be 
	 * incorperated in the resultant JSON object under the key 'extra'.
	 * @param elements A variable amount of ChatElements
	 */
	public ChatElement extra(ChatElement... elements) {
		for(ChatElement e: elements) {
			extras.add(e);
		}
		return this;
	}
	
	/**
	 * Gets the parent ChatBuilder, if any. 
	 * @return The parent ChatBuilder, or null, if it doesn't exist.
	 */	
	public ChatBuilder builder() {
		return builder;
	}
	
	/**
	 * Sets the text of this ChatElement.
	 * @param text The text to change to.
	 */		
	public ChatElement text(String text) {
		this.text = text;
		return this;
	}
	
	/**
	 * Sets whether the text of the ChatElement should be translated or not.
	 * Translation refers to Minecraft's localization system, where it converts nodes to the appropriate language.
	 * @param translate Whether to translate the text or not.
	 */			
	public ChatElement setTranslate(boolean translate) {
		this.translate = translate;
		return this;
	}
	
	/**
	 * Sets the color of the ChatElement.
	 * The Color enums are located in ChatUtils.
	 * @param color The Color to set this ChatElement to.
	 */				
	public ChatElement color(Color color) {
		this.color = color;
		return this;
	}

	/**
	 * Sets the active format(s) of the ChatElement.
	 * The Format enums are located in ChatUtils.
	 * @param formats The Format(s) to set this ChatElement to.
	 */
	public ChatElement format(Format... formats) {
		this.formats.clear();
		for(Format format: formats) {
			this.formats.add(format);
		}
		return this;
	}
	
	/**
	 * Sets click action of this ChatElement.
	 * The ClickEvent enums are located in ChatUtils.
	 * How the value is used depends on the ClickEvent.
	 * @param event The ClickEvent to set this ChatElement to.
	 * @param value The value.
	 */
	public ChatElement click(ClickEvent event, String value) {
		clickEvent = event;
		clickEvent_value = value;
		return this;
	}
	
	/**
	 * Sets hover action of this ChatElement.
	 * The HoverEvent enums are located in ChatUtils.
	 * How the value is used depends on the HoverEvent.
	 * @param hover The ClickEvent to set this ChatElement to.
	 * @param value The value.
	 */	
	public ChatElement hover(HoverEvent event, String value) {
		hoverEvent = event;
		hoverEvent_value = value;
		return this;
	}
	
	/**
	 * Returns the JSON Representation of this object.
	 * This representation is valid for Minecraft's JSON chat.
	 */
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
		
		json.put("extra", extras);

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

		for(Format format: formats)
			json.put(format.name().toLowerCase(), true);
		
		json.put("color", color.name().toLowerCase());

		return JSONValue.toJSONString(json);
		
	}

}
