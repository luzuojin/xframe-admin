package dev.xframe.admin.sample;

import dev.xframe.boot.Bootstrap;
import dev.xframe.utils.XProperties;

public class TBootstrap {

    public static void main(String[] args) {
        new Bootstrap()
            .withName("xframe-admin")
            .include("dev.xframe.*")
            .exclude("dev.xframe.test.*;dev.xframe.jdbc.*")
            .withHttp(XProperties.getAsInt("port", 8003))
            .startup();
    }

}
