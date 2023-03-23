package dev.xframe.admin.view.structs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Navigate extends Navi {

	protected List<Navi> navis = new ArrayList<>();//children

	public Navigate(String path) {
		super(path, path, 10);
	}
	public Navigate(String path, String name, int order) {
		super(path, name, order);
	}

	protected Navigate(Navi navi, Navigate navigate) {
		super(navi.path, navi.name, navi.order);
		this.navis = navigate.navis;
	}

	public Navi findOrAdd(String path, Navi _setting) {
		Optional<Navi> any = navis.stream().filter(_navi -> _navi.path.equals(path)).findAny();
		if(any.isPresent()) {
			return any.get();
		}
		this.navis.add(_setting);
		return _setting;
	}

	@Override
	protected Navi duplicateBy(Navi navi) {
		return new Navigate(navi, this);
	}

	public List<Navi> getNavis() {
		return navis;
	}
	public void setNavis(List<Navi> navis) {
		this.navis = navis;
	}

}
