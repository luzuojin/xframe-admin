package dev.xframe.admin.view;

import java.util.Collections;
import java.util.List;

/**
 * 自动填充的Chapter
 * 多层结构
 * -Chapter
 * 	 -PaddedSegment
 *     -Segment(view as Tab)
 * @author luzj
 */
@FunctionalInterface
public interface Padding {
	
	Padding NIL = Collections::emptyList;

	List<Navi> get();

}
