package dev.xframe.admin.system.server;

import java.util.List;

import dev.xframe.admin.conf.LogicException;
import dev.xframe.admin.system.SystemContext;
import dev.xframe.admin.system.SystemRepo;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;

@Rest("system/server")
@XSegment(name="服务器列表", model=Server.class)
public class ServerService {
    @Inject
    private SystemRepo sysRepo;
    @Inject
    private SystemContext sysCtx;
	/**
	 * 点击侧边栏时调用该方法 可以返回空数组
	 */
	@HttpMethods.GET("list")
	public Object get() {
		List<Server> servers = sysRepo.fetchServers();
        return servers;
	}
	
	@HttpMethods.POST
	public Object add(@HttpArgs.Body Server server) {
		sysRepo.addServer(server);
		return server;
	}

	@HttpMethods.PUT
	public Object edit(@HttpArgs.Body Server server) {
		Server ex = sysRepo.fetchServer(server.getId());
		if(ex != null) {
			ex.setName(server.getName());
			ex.setUrl(server.getUrl());
			sysRepo.saveServer(server);
			return ex;
		}
		throw new LogicException("服务器不存在	");
	}
}
