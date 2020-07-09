package dev.xframe.admin.system.protocolTest;

import java.util.ArrayList;
import java.util.List;

import dev.xframe.admin.system.SystemContext;
import dev.xframe.admin.system.SystemRepo;
import dev.xframe.admin.system.XEnumKeys;
import dev.xframe.admin.system.server.Server;
import dev.xframe.admin.util.HttpRequest;
import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;
import io.netty.util.internal.StringUtil;

@Rest("clientest/protocoltest")
@XSegment(name="协议测试", model=ProtocolInfo.class)
public class ProtocolTestService {
	@Inject
	private SystemRepo sysRepo;
	@Inject
	private SystemContext sysCtx;
	
	@HttpMethods.GET
	public Object queryProtocol(@HttpArgs.Param @XColumn(value = "服务器名称",enumKey=XEnumKeys.SERVER_LIST) String name) {
		if (StringUtil.isNullOrEmpty(name)) {
			return null;
		}
		
		String[] arrays = name.split("#");
		int serverId = Integer.valueOf(arrays[0]);
		Server server = sysCtx.getServer(serverId);
		if (server == null) {
			return null ;
		}
		String url = server.getUrl();
		String result = HttpRequest.sendGet(url+"gm_protocols", "user=hawk");
		return buildProtocolInfoList(result,server.getId(),server.getName());
	}
	
	@HttpMethods.PUT
	public Object edit(@HttpArgs.Body ProtocolInfo protocolInfo) {
		return protocolInfo;
	}
	
	/**
	 * 构建protocolinfo
	 * @param json
	 * @param serverId
	 * @return
	 */
	private static List<ProtocolInfo> buildProtocolInfoList(String json, int serverId,String serverName) {
		List<ProtocolInfo> lists = new ArrayList<ProtocolInfo>();
		String[] codeArray = json.split(";");
		for (String codeString : codeArray) {
			String[] params = codeString.split(",");
			if (params.length < 1) {
				continue;
			}
			ProtocolInfo info = new ProtocolInfo();
			int code = Integer.valueOf(params[0]);
			info.setCode(code);
			info.setServerName(serverName);
			info.setServerId(serverId);
			StringBuffer sb = new StringBuffer();
			if (params.length == 2) {
				String param = params[1];
				String[] paramItems = param.split("#");
				int length = paramItems.length;
				for (int i = 0; i < paramItems.length; i++) {
					String paramItem = paramItems[i];
					sb.append(paramItem).append(":$");
					if (i < length - 1) {
						sb.append(",");
					}
				}
				info.setParams(sb.toString());
			}else {
				info.setParams("no need param！");
			}
			lists.add(info);
		}
		return lists;
	}
}
