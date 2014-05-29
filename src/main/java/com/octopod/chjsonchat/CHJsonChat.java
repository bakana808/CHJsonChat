package com.octopod.chjsonchat;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;

@MSExtension("CHJsonChat")
public class CHJsonChat extends AbstractExtension {

	public Version getVersion() {
		return new SimpleVersion(0, 0, 1);
	}

}
