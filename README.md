`formatArray` Example
------
```commandhelper
	array(
		#The text to show. This key is required.
		text: 'hello',
		#The color of the text. Defaults to white.
		#Valid color names:
		# - black
		# - dark_blue
		# - dark_green
		# - dark_aqua
		# - dark_red
		# - dark_purple
		# - gold
		# - gray
		# - dark_gray
		# - blue
		# - green
		# - aqua
		# - red
		# - light_purple
		# - yellow
		# - white
		# - obfuscated
		# - bold
		# - strikethrough
		# - underline
		# - italic
		# - reset
		color: blue,
		#Is the text bolded? Defaults to false.
		bold: false,
		#Is the text in italics? Defaults to false.
		italic: false,
		#Is the text underlined? Defaults to false.
		underlined: false,
		#Is the text have strikethrough? Defaults to false.
		strikethrough: false,
		#Is the text obfuscated? Defaults to false.
		obfuscated: false,
		#The onHover array is [optional]. Does something if the player's mouse hovers over the message.
		onHover: array(
			#Event name. This is [required]. Can be one of:
			# - show_text: Shows the 'value' key when hovered over.
			# - show_achievement: Shows the achievement specified in 'value' when hovered over. ex: "achievement.openInventory"
			# - show_item: Shows an item when hovered over. 'value' is the JSON representation of that item.
			event: 'show_text',
			#The value. This is required. What the value does depends on the event.
			value: 'tooltip'
		),
		#The onClick array is [optional]. Does something if the player clicks this section of the message.
		onClick: array(
			#Event name. This is [required]. Can be one of:
			# - run_command: Runs 'value' as if the player typed it into their chat.
			# - suggest_command: Copies 'value' into the player's chat box.
			# - open_url: Opens the URL specificed in 'value'.
			# - open_file: Opens the file specified in 'value'.
			event: 'run_command',
			#The value. This is required. What the value does depends on the event.
			value: '/spawn'
		)
	)
```

Now, onto the functions.

`chjc_msg`: {[`player`], `array`}
------
`array` is an array of `formatArrays` and/or strings. Strings will always be white.
The resultant message will be sent to the player, or yourself if player isn't included.
You cannot send a message to the console.

`chjc_broadcast`: {`array`, [`permission`]}
------
`array` is an array of `formatArrays` and/or strings. Strings will always be white.
The resultant message will be sent to all players on the server, or only players who have the permission `permission` if it's included.

`chjc_convert`: {`mixed`}
------
if `mixed` is a formatArray, it will return a legacy (old chat) string.
if `mixed` is a legacy string, it will return a formatArray.

`chjc_raw_msg`: {`json`}
------
Sends a JSON Chat packet to the player containing the string `json`.
Bukkit will make no attempt to check the validity of `json` and will kick the player off the server if `json` is malformed.

