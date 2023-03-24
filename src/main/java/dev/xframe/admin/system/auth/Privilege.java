package dev.xframe.admin.system.auth;

public class Privilege {

    public static final Privilege Admin = new Privilege("_", "全部") {
        @Override
        public boolean match(String path) {
            return true;
        }
    };
	
	protected String path;
    protected String name;

	public Privilege(String path, String name) {
		this.name = name;
		this.path = path;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    public boolean match(String path) {
        return this.path.equals(path) || this.path.startsWith(path + "/") || path.startsWith(this.path + "/");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Privilege other = (Privilege) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        return true;
    }
	
}
