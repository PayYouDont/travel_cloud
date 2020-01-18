package com.gospell.travel.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* @Author peiyongdong
* @Description ( 自定义annotation )
* @Date 14:01 2019/10/30
* @Param
* @return
**/
@Target (ElementType.FIELD)
@Retention (RetentionPolicy.RUNTIME)
public @interface ViewById {
    int value() default -1;
}
