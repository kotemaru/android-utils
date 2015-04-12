package org.kotemaru.android.delegatehandler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kotemaru.android.fw.thread.ThreadManager;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Documented
public @interface Handle {
	String thread() default ThreadManager.WORKER;
	int delay() default 0;
	int retry() default 0;
	int interval() default 1000;
	float intervalRate() default 2.0F;
}
