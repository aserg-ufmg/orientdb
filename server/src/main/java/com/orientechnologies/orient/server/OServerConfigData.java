package com.orientechnologies.orient.server;

import com.orientechnologies.orient.core.config.OContextConfiguration;
import com.orientechnologies.orient.server.config.OServerConfiguration;
import com.orientechnologies.orient.server.config.OServerConfigurationLoaderXml;

public class OServerConfigData {
	public OServerConfigurationLoaderXml configurationLoader;
	public OServerConfiguration configuration;
	public OContextConfiguration contextConfiguration;

	public OServerConfigData() {
	}
}