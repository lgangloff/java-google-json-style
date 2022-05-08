package org.lgangloff.web.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE })
public @interface JSONData {
    String kind();

    String fields();

    int itemsPerPage();

}
