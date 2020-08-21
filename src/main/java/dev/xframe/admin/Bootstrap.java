package dev.xframe.admin;

import dev.xframe.admin.conf.SysProperties;

public class Bootstrap {
	
	public static void main(String[] args) {
		new dev.xframe.boot.Bootstrap()
			.withName("xframe-admin")
			.include("dev.xframe.*")
			.exclude("dev.xframe.test.*;dev.xframe.jdbc.*")
			.withHttp(SysProperties.get("port", 8001))
			.startup();
	}

}
