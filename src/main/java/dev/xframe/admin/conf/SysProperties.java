package dev.xframe.admin.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * -Dconf.file
 * -Dconf.dir	/conf.properties
 * -Dapphome	/conf.properties
 * -Duser.dir	/conf.properties
 * -Dproperty
 * @author luzj
 */
public class SysProperties {

	static Map<String, String> properties = new TreeMap<>();
	static {
		File conf = new File(getConfFile());
		if(conf.exists()) {
			try {
				Properties _properties = new Properties();
				_properties.load(new FileInputStream(conf));
				_properties.stringPropertyNames().forEach(k->{
					properties.put(k, _properties.getProperty(k));
				});
			} catch (IOException e) {
				//ignore
			}
		}
	}

	public static String get(String key) {
		return System.getProperty(key, properties.get(key));
	}
	public static String get(String key, String def) {
		return System.getProperty(key, properties.getOrDefault(key, def));
	}
	public static int get(String key, int def) {
		return Integer.parseInt(get(key, String.valueOf(def)));
	}
	public static long get(String key, long def) {
		return Long.parseLong(get(key, String.valueOf(def)));
	}
	
	public static String getConfFile() {
		return get("conf.file", new File(getConfDir(), "conf.properties").getPath());
	}
	public static String getConfDir() {
		return get("conf.dir", get("work.dir", get("user.dir")));
	}
	public static String getStoreDir() {
		return get("store.dir", get("work.dir", get("user.dir")));
	}
	
}
