package com.octopod.chjsonchat;

import com.octopod.octal.minecraft.ChatBuilder;
import com.octopod.octal.minecraft.ChatElement;
import com.octopod.octal.minecraft.ChatUtils;
import com.octopod.octal.minecraft.ChatUtils.ChatColor;
import com.octopod.octal.minecraft.ChatUtils.ChatFormat;
import com.octopod.octal.minecraft.ChatUtils.ClickEvent;
import com.octopod.octal.minecraft.ChatUtils.HoverEvent;
import com.octopod.octal.minecraft.bukkit.BukkitPlayer;
import org.bukkit.entity.Player;

import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.Exceptions.ExceptionType;


public class Functions extends CHJsonChat{
	
	@api
	public static class chjc_raw_msg extends Function {
		
		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			
			MCCommandSender sender = environment.getEnv(CommandHelperEnvironment.class).GetCommandSender();
			MCPlayer target;
			if(args.length == 2) {
				target = Static.GetPlayer(args[1], t);
			} else {
				if(!(sender instanceof MCPlayer)) {
					throw new ConfigRuntimeException("You cannot send JSON chat messages to the console!", ExceptionType.FormatException, t);
				}
				target = (MCPlayer)sender;
			}
			
			ChatUtils.send(new BukkitPlayer(target.getHandle()), args[0].val());
			
			return CVoid.VOID;

		}

		public String getName() {
			return "chjc_raw_msg";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "void {json, [player]} " +
				"Attempts to send a raw JSON to a player." +
				"Minecraft will not attempt to check if the JSON is valid before sending it, " +
				"and will simply kick the player out of the server if it isn't.";
		}
		
	}
	
	@api
	public static class chjc_convert extends Function {

		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			
			char colorSymbol = '&';
			
			if(args.length == 2) {
				if(!(args[1] instanceof CString))
					throw new ConfigRuntimeException("If color symbol is provided, must be a character.", ExceptionType.CastException, t);
				try{
					colorSymbol = args[1].val().charAt(0);
				} catch (Exception e) {
					throw new ConfigRuntimeException("If color symbol is provided, cannot be empty.", ExceptionType.CastException, t);
				}
			}
			
			if(args[0] instanceof CString) {
				ChatBuilder element = ChatUtils.fromLegacy(args[0].val(), colorSymbol);
				return toArray(new ChatBuilder().append(element), t);
			}

			throw new ConfigRuntimeException("Expected a string for the first argument.", ExceptionType.CastException, t);

		}

		public String getName() {
			return "chjc_convert";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "array {message}" +
					"Converts a legacy chat message into a formatArray.";
		}

	}		
	
	@api
	public static class chjc_legacy extends Function {

		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {

			if(args[0] instanceof CArray) {
				ChatBuilder cb = fromArray((CArray)args[0], t);
				return new CString(ChatUtils.toLegacy(cb), t);
			}

			throw new ConfigRuntimeException("Expected an array for the first argument.", ExceptionType.CastException, t);

		}

		public String getName() {
			return "chjc_convert";
		}

		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		public String docs() {
			return "string {formatArray}" +
					"Converts a formatArray into a legacy chat message. " +
					"Obviously, hover and click events won't carry over.";
		}

	}	
	
	@api
	public static class chjc_msg extends Function {

		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			
			MCCommandSender sender = environment.getEnv(CommandHelperEnvironment.class).GetCommandSender();
			MCPlayer target;
			CArray format;
			
			if(args.length == 1) {
				if(!(sender instanceof MCPlayer))
					throw new ConfigRuntimeException("You cannot send JSON chat messages to the console!", ExceptionType.FormatException, t);
				target = (MCPlayer)sender;
				format = Static.getArray(args[0], t);
			} else {
				target = Static.GetPlayer(args[0], t);
				format = Static.getArray(args[1], t);
			}

			ChatBuilder cb = fromArray(format, t);
			Static.getServer().getConsole().sendMessage(cb.toLegacy());
			cb.send(new BukkitPlayer(target.getHandle()));

			return CVoid.VOID;

		}

		public String getName() {
			return "chjc_msg";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "void {[player], formatArray}";
		}

	}
	
	@api
	public static class chjc_broadcast extends Function {

		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {

			String permission = null;
			CArray format = Static.getArray(args[0], t);

			if(args.length == 2) {
				permission = args[1].val();
			}

			ChatBuilder cb = fromArray(format, t);
			Static.getServer().getConsole().sendMessage(cb.toLegacy());
			
			for(MCPlayer target: Static.getServer().getOnlinePlayers())
				if(permission == null || ((Player)target.getHandle()).hasPermission(permission))
					cb.send(new BukkitPlayer(target.getHandle()));
			
			return CVoid.VOID;

		}

		public String getName() {
			return "chjc_broadcast";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "void {formatArray, [permission]}";
		}

	}
	
	private static CArray toArray(ChatBuilder builder, Target t) throws ConfigRuntimeException {
		
		CArray array = new CArray(t);
		
		for(ChatElement e: builder.toElementList()) {
			CArray formatArray = new CArray(t);
			formatArray.set("text", e.getText());
			formatArray.set("color", e.getColor().name().toLowerCase());
			for(ChatFormat format: e.getFormats())
				formatArray.set(format.name().toLowerCase(), CBoolean.TRUE, t);
			if(e.getClick() != null) {
				CArray clickEvent = new CArray(t);
				clickEvent.set("event", e.getClick().name().toLowerCase());
				clickEvent.set("value", e.getClickValue());
				formatArray.set("onClick", clickEvent, t);
			}
			if(e.getHover() != null) {
				CArray hoverEvent = new CArray(t);
				hoverEvent.set("event", e.getHover().name().toLowerCase());
				hoverEvent.set("value", e.getHoverValue());
				formatArray.set("onHover", hoverEvent, t);
			}
			array.push(formatArray);
		}
		
		return array;
		
	}

	private static ChatBuilder fromArray(CArray format, Target t) throws ConfigRuntimeException{
		
		ChatBuilder builder = new ChatBuilder();
		
		for(Construct c: format.asList()) {
			
			if(c instanceof CString) {
				builder.append(c.val());
				continue;
			} 
			
			if(c instanceof CArray) {
				
				CArray element = (CArray)c;
				ChatColor color = ChatColor.WHITE;
				ClickEvent click;
				HoverEvent hover;
				String text = element.get("text", t).getValue();
				
				builder.append(text);
				
				//Color Checks		
				
				try{
					color = ChatColor.valueOf(element.get("color", t).val().toUpperCase());
				} catch (IllegalArgumentException e) {
					throw new ConfigRuntimeException("\"" + element.get("color", t) + "\" is not a valid color", ExceptionType.FormatException, t);
				} catch (ConfigRuntimeException e) {}
				
				builder.color(color);
				
				//Format Checks
				
				if(element.containsKey("bold") && Static.getBoolean(element.get("bold", t)))
					builder.bold();
				
				if(element.containsKey("italic") && Static.getBoolean(element.get("italic", t)))
					builder.italic();
				
				if(element.containsKey("underlined") && Static.getBoolean(element.get("underlined", t)))
					builder.underline();
				
				if(element.containsKey("strikethrough") && Static.getBoolean(element.get("strikethrough", t)))
					builder.strikethrough();
				
				if(element.containsKey("obfuscated") && Static.getBoolean(element.get("obfuscated", t)))
					builder.obfuscate();
				
				//Click Event Checks
				
				if(element.containsKey("onClick")) {
					CArray clickArray = Static.getArray(element.get("onClick", t), t);
					try{
						click = ClickEvent.valueOf(clickArray.get("event", t).val().toUpperCase());
					} catch (IllegalArgumentException e) {
						throw new ConfigRuntimeException("Click event must be one of: open_url, open_file, run_command, suggest_command", ExceptionType.FormatException, t);
					}
					builder.click(click, clickArray.get("value", t).val());
				}
				
				//Hover Event Checks
				
				if(element.containsKey("onHover")) {
					CArray hoverArray = Static.getArray(element.get("onHover", t), t);
					try{
						hover = HoverEvent.valueOf(hoverArray.get("event", t).val().toUpperCase());
					} catch (IllegalArgumentException e) {
						throw new ConfigRuntimeException("Hover event must be one of: show_text, show_achievement, show_item", ExceptionType.FormatException, t);
					}
					builder.hover(hover, hoverArray.get("value", t).val());
				}

				continue;
			}
			
			throw new ConfigRuntimeException("Only strings and associative arrays are allowed in a format array", ExceptionType.FormatException, t);
			
		}
		
		return builder;
		
	}

}
