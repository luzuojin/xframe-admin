package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.EOption;
import dev.xframe.admin.view.XSegment;
import dev.xframe.utils.XReflection;
import dev.xframe.utils.XStrings;

import java.util.Arrays;

public class Markd extends Classic {
    public Markd() {
        super(EContent.Markd);
    }
    @Override
    public Content parseFrom(XSegment xseg, Class<?> declaring) {
        options = Arrays.asList(new Option("加载", EOption.Ini));
        return this;
    }
    public static String read(String file) {
        return XStrings.readFrom(XReflection.getResourceAsStream(file));
    }
}
