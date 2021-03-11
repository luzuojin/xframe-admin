package dev.xframe.admin.view.details;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import dev.xframe.admin.view.Detail;
import dev.xframe.admin.view.Option;
import dev.xframe.admin.view.XSegment;
import dev.xframe.utils.XCaught;

public class Markd extends Classic {
    public Markd() {
        super(type_markd);
    }
    @Override
    public Detail parseFrom(XSegment xseg, Class<?> declaring) {
        options = Arrays.asList(Option.ini);
        return this;
    }
    public static String read(String file) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = Markd.class.getClassLoader().getResourceAsStream(file);
            if(in == null) {
                in = new FileInputStream(new File(file));
            }
            int b;
            while((b = in.read()) != -1) out.write(b);
            return out.toString("utf8");
        } catch (IOException e) {
            return XCaught.throwException(e);
        }
    }
}
