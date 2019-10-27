package dev.xframe.admin.centra.cell;

import java.util.Collections;

import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpMethods;

@Rest("centra/cell")
@XSegment(name="节点列表", model=Cell.class)
public class CellService {
	
	@HttpMethods.GET
	public Object get() {
		return Collections.EMPTY_LIST;
	}

}
