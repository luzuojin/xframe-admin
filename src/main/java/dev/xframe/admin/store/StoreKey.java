package dev.xframe.admin.store;

public enum StoreKey {
	
	DAT("dat_init.sql"),
	
	LOG("log_init.sql");
	
	//classpath:resources
	public final String script;
	private StoreKey(String script) {
		this.script = script;
	}

}
