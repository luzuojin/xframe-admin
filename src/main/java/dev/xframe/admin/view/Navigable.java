package dev.xframe.admin.view;

import java.util.Collections;
import java.util.List;

/**
 * 自动填充的Chapter
 * 多层结构
 * -Chapter
 * 	 -NavSegment
 *     -Segment(view as Tab)
 * @author luzj
 */
@FunctionalInterface
public interface Navigable {
	
	Navigable NIL = Collections::emptyList;

	List<Navi> get();

}
