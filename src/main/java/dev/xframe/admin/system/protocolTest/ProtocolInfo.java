package dev.xframe.admin.system.protocolTest;

import dev.xframe.admin.view.XColumn;

public class ProtocolInfo {
	@XColumn(value="服务器Id",show = XColumn.edel)
	private int serverId;
	@XColumn(value="服务器名称",show = XColumn.edel)
	private String serverName;
	@XColumn(value = "协议号",show = XColumn.xor_edit)
	private int code;
	@XColumn(value="playerId",show = XColumn.xor_list)
	private int playerId;
	@XColumn(value="参数",show = XColumn.xor_list)
	private String params;
	
	public ProtocolInfo(int code, String params,int serverId,String serverName) {
		this.code = code;
		this.params = params;
		this.serverId  = serverId;
		this.serverName = serverName;
		this.playerId = 0;
	}
	
	public ProtocolInfo() {
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((params == null) ? 0 : params.hashCode());
        return result;
    }

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProtocolInfo other = (ProtocolInfo) obj;
        if (params == null) {
            if (other.params != null)
                return false;
        } else if (!params.equals(other.params))
            return false;
        return true;
    }
}
