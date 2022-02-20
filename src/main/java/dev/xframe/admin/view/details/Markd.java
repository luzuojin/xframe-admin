package dev.xframe.admin.view.details;

import dev.xframe.admin.view.Detail;
import dev.xframe.admin.view.Option;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;
import dev.xframe.utils.XReflection;
import dev.xframe.utils.XStrings;

import java.util.Arrays;

public class Markd extends Classic {
    public Markd() {
        super(XSegment.type_markd);
    }
    @Override
    public Detail parseFrom(XSegment xseg, Class<?> declaring) {
        options = Arrays.asList(new Option("加载", XOption.type_ini));
        return this;
    }
    public static String read(String file) {
        return XStrings.readFrom(XReflection.getResourceAsStream(file));
    }
}
