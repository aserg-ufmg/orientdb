package com.orientechnologies.orient.server.plugin.mail;

import java.util.Map;

public class OMailPluginData {
	public Map<String, OMailProfile> profiles;

	public OMailPluginData(Map<String, OMailProfile> profiles) {
		this.profiles = profiles;
	}
}