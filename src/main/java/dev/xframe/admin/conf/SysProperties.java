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
		String val = System.getProperty("conf.file", 
						System.getProperty("conf.dir", 
						System.getProperty("apphome", 
						System.getProperty("user.dir"))));
		File file = new File(val);
		if(file.isDirectory()) {
			file = new File(file, "conf.properties");
		}
		if(file.exists()) {
			try {
				Properties _properties = new Properties();
				_properties.load(new FileInputStream(file));
				_properties.stringPropertyNames().forEach(k->{
					properties.put(k, _properties.getProperty(k));
				});
			} catch (IOException e) {
				//ignore
			}
		}
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
	
}
