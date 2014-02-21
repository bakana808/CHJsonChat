package com.octopod.chjsonchat;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;

@MSExtension("CHJsonChat")
public class CHJsonChat extends AbstractExtension{

	@Override
	public Version getVersion() {
		return new Version() {

			@Override
			public int getMajor() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getMinor() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getSupplemental() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean lt(Version other) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean lte(Version other) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean gt(Version other) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean gte(Version other) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
	}

}
