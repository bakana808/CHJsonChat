package com.octopod.chjsonchat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.PermissionsResolver;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.environments.GlobalEnv;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.functions.Exceptions.ExceptionType;
import com.octopod.utils.bukkit.ChatBuilder;
import com.octopod.utils.bukkit.ChatElement.ChatClickEvent;
import com.octopod.utils.bukkit.ChatElement.ChatHoverEvent;

public class Functions {

	@api
	public static abstract class func extends AbstractFunction {

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Version since() {
			return CHVersion.V3_3_1;
		}
		
	}
	
	@api
	public static class chjc_msg extends func {

		public ExceptionType[] thrown() {
			return new ExceptionType[]{};
		}

		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			
			MCCommandSender sender = environment.getEnv(CommandHelperEnvironment.class).GetCommandSender();
			MCPlayer target = null;
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
			cb.send((Player)target.getHandle());
			
			return new CVoid(t);

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
	public static class chjc_broadcast extends func {

		public ExceptionType[] thrown() {
			return new ExceptionType[]{};
		}

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
					cb.send((Player)target.getHandle());
			
			return new CVoid(t);

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

	private static ChatBuilder fromArray(CArray format, Target t) throws ConfigRuntimeException{
		
		ChatBuilder builder = new ChatBuilder();
		
		for(Construct c: format.asList()) {
			
			if(c instanceof CString) {
				builder.push(((CString)c).val());
				continue;
			} 
			
			if(c instanceof CArray) {
				
				CArray element = (CArray)c;
				ChatColor color = ChatColor.WHITE;
				ChatClickEvent click = null;
				ChatHoverEvent hover = null;
				String text = element.get("text", t).getValue();
				
				builder.push(text);
				
				//Color Checks		
				
				try{
					color = ChatColor.valueOf(element.get("color").val().toUpperCase());
				} catch (IllegalArgumentException e) {
					throw new ConfigRuntimeException("\"" + element.get("color") + "\" is not a valid color", ExceptionType.FormatException, t);
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
					CArray clickArray = Static.getArray(element.get("onClick"), t);
					try{
						click = ChatClickEvent.valueOf(clickArray.get("event", t).val().toUpperCase());
					} catch (IllegalArgumentException e) {
						throw new ConfigRuntimeException("Click event must be one of: open_url, open_file, run_command, suggest_command", ExceptionType.FormatException, t);
					}
					builder.click(click, clickArray.get("value", t).val());
				}
				
				//Hover Event Checks
				
				if(element.containsKey("onHover")) {
					CArray hoverArray = Static.getArray(element.get("onHover"), t);
					try{
						hover = ChatHoverEvent.valueOf(hoverArray.get("event", t).val().toUpperCase());
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
