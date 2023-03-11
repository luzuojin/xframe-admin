package dev.xframe.admin.sample.nested;

import dev.xframe.admin.system.XRegistrator;
import dev.xframe.admin.view.values.VEnum;
import dev.xframe.admin.view.XChapter;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;

import java.util.Arrays;

@Bean
@XChapter(name="嵌套测试", path="nested")
public class NestedTestChapter implements Loadable {

	@Inject
	private XRegistrator xReg;

	@Override
	public void load() {
		xReg.registEnumValue(NestedTest.NESTED_TYPE, ()->Arrays.asList(new VEnum("001"), new VEnum("002")));
	}

}
