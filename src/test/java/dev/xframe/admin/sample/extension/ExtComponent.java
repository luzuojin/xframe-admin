package dev.xframe.admin.sample.extension;

import dev.xframe.admin.system.XRegistrator;
import dev.xframe.inject.Component;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;

@Component
public class ExtComponent implements Loadable {

    @Inject
    XRegistrator xReg;

    @Override
    public void load() {
        xReg.registExtension("welcome.js");
    }
}
