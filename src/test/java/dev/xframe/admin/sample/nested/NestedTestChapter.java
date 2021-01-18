package dev.xframe.admin.sample.nested;

import java.util.Arrays;

import dev.xframe.admin.system.BasicContext;
import dev.xframe.admin.view.VEnum;
import dev.xframe.admin.view.XChapter;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;

@Bean
@XChapter(name="嵌套测试", path="nested")
public class NestedTestChapter implements Loadable {

	@Inject
	private BasicContext basicCtx;
	
	@Override
	public void load() {
		basicCtx.registEnumValue(NestedTest.NESTED_TYPE, ()->Arrays.asList(new VEnum("001"), new VEnum("002")));
	}

}
