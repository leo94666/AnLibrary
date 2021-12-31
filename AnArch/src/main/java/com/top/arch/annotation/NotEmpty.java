package com.top.arch.annotation;

import androidx.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotEmpty {
    @StringRes int emptyTextResId() default -1;
    String emptyText()              default "";
    boolean trim()                  default false;
    @StringRes int messageResId()   default -1;
    String message()                default "This field is required";
    int sequence()                  default -1;
}
