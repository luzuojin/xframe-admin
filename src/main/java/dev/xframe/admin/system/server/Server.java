package dev.xframe.admin.system.server;

import dev.xframe.admin.view.XColumn;

/**
 * 服务器数据
 * @author songlei
 *
 */
public class Server {
	@XColumn(value="服务器ID", primary=true,show = XColumn.edel)
    private int id;
	
	@XColumn(value="服务器名称")
	private String name;
	
	@XColumn(value="服务器地址")
	private String url;
	
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode())+ id;
        return result;
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Server other = (Server) obj;
        return this.id == other.getId();
	}
}
