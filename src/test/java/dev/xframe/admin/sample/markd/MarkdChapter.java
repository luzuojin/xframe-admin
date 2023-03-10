package dev.xframe.admin.sample.markd;

import dev.xframe.admin.system.XRegistrator;
import dev.xframe.admin.view.Navi;
import dev.xframe.admin.view.XChapter;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;

import java.util.Arrays;

@Bean
@XChapter(name="wildcard&markd",path="markd")
public class MarkdChapter implements Loadable {

    @Inject
    private XRegistrator xReg;

    @Override
    public void load() {
        xReg.registNaviValue("markd", ()-> Arrays.asList(new Navi("userops", "userops"), new Navi("sysops", "sysops")));
    }
    
}
