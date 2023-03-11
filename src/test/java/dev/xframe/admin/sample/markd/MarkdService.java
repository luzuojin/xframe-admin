package dev.xframe.admin.sample.markd;

import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.structs.Markd;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;

@Rest("markd/{name}")
@XSegment(name = "unused", type = EContent.Markd)
public class MarkdService {

    @HttpMethods.GET
    public Object get(@HttpArgs.Path String name) {
        return Markd.read(name + ".md");
    }

}
