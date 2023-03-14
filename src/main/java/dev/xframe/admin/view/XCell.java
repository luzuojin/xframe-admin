package dev.xframe.admin.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(XCells.class)
public @interface XCell {

    EChart type() default EChart.Line;

    String title() default "";
    /**
     * 0 per cell per row
     */
    int row() default 0;
    /**
     * 0 avg
     * 1~6
     */
    int col() default 0;

}
