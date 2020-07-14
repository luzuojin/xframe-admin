package dev.xframe.admin.system.protocolTest;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import dev.xframe.admin.conf.LogicException;
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
	private static final String PARAM_NOT_NEED = "no need param！";
	
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
		List<ProtocolInfo> protocols = this.getProtocols(serverId);
		return protocols;
	}
	
	private List<ProtocolInfo> getProtocols(int serverId){
		Server server = sysCtx.getServer(serverId);
		if (server == null) {
			return null;
		}
		
		String url = server.getUrl();
		String result = HttpRequest.sendGet(url+"gm_protocols", "user=hawk");
		List<ProtocolInfo> protocols = this.buildProtocolInfoList(result,server.getId(),server.getName());
		return protocols;
	}

	@HttpMethods.PUT
	public Object queryToServer(@HttpArgs.Body ProtocolInfo protocolInfo) {
		int serverId = protocolInfo.getServerId();
		Server server = sysCtx.getServer(serverId);
		if (server == null ) {
			throw new LogicException("服务器不存在！");
		}
		
		String reqParam = this.buildReqParam(protocolInfo.getParams());
		String url = server.getUrl();
		StringBuffer sb = new StringBuffer()
				.append("params=")
				.append("code:").append(protocolInfo.getCode()).append(";")
				.append("playerId:").append(protocolInfo.getPlayerId()).append(";")
				.append("param:").append(reqParam)
				.append("&user=hawk");
		String result = HttpRequest.sendGet(url+"gm_protocolreq", sb.toString());
		List<ProtocolInfo> results = this.getProtocols(serverId);
		results.stream().filter(a -> a.getCode() == protocolInfo.getCode()).forEach(a -> a.setStatus(result));
		return results;
	}

	/**
	 * 解析封装参数
	 * @param params
	 * @return
	 */
	private String buildReqParam(String params) {
		if (params.equals(PARAM_NOT_NEED)) {
			return "";
		}
		String[] paramArrays = params.split(";");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < paramArrays.length; i++) {
			String param = paramArrays[i];
			String[] split = param.split(":");
			if (split.length != 2) {
				throw new LogicException("参数错误！");
			}
			
			if (split[1].equals("$")) {
				continue;
			}
			sb.append(param);
			if (i < paramArrays.length -1) {
				sb.append(";");
			}
		}
		return new String(Base64.getEncoder().encodeToString(sb.toString().getBytes()));
	}
	/**
	 * 构建protocolinfo
	 * @param json
	 * @param serverId
	 * @return
	 */
	private List<ProtocolInfo> buildProtocolInfoList(String json, int serverId,String serverName) {
		if (StringUtil.isNullOrEmpty(json)) {
			throw new LogicException("server not open or no avaliable protocoltesthandler");
		}
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
						sb.append(";");
					}
				}
				info.setParams(sb.toString());
			}else {
				info.setParams(PARAM_NOT_NEED);
			}
			lists.add(info);
		}
		return lists;
	}
}
