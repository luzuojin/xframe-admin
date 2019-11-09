package dev.xframe.admin.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import dev.xframe.admin.conf.WebFileHandler;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.jdbc.tools.SQLScript;
import dev.xframe.utils.XStrings;

@Bean
public class VersionContext  {
	
	@Inject
	private VersionRepo repo;
	
	public void update(StoreKey key, Version version) {
	    int v = repo.fetch(key).stream().mapToInt(Version::getVersion).max().orElse(-1);
	    if(v == -1) {
	    	throw new IllegalArgumentException("History versions error");
	    }
	    
	    if(version.getVersion() > v) {
	    	readScripts(version.getSqlPath()).forEach(script->repo.runScript(key, script));
	    }
	}

	private List<String> readScripts(String path) {
		try {
            InputStream in = WebFileHandler.class.getClassLoader().getResourceAsStream(path);
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            return SQLScript.parse(XStrings.newStringUtf8(bytes));
        } catch (IOException e) {
        	throw new IllegalArgumentException(e);
        }
	}

}
