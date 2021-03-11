package dev.xframe.admin.sample.markd;

import java.util.Arrays;
import java.util.List;

import dev.xframe.admin.view.Navi;
import dev.xframe.admin.view.Navigable;
import dev.xframe.admin.view.XChapter;

@XChapter(name="wildcard&markd",path="markd")
public class MarkdChapter implements Navigable {
    @Override
    public List<Navi> get() {
        return Arrays.asList(new Navi("userops", "userops"), new Navi("sysops", "sysops"), new Navi("readme", "readme"));
    }
}
