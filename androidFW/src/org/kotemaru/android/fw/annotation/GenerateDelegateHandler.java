package org.kotemaru.android.fw.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface GenerateDelegateHandler {
	/**
	 * 実装インターフェースの一覧。
	 */
	Class<?>[] implement() default {};
}
