package com.octopod.chjsonchat;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.functions.AbstractFunction;

public abstract class Function extends AbstractFunction {

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
