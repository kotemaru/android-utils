package org.kotemaru.android.fw.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented

/**
 * ハンドラへの移譲クラスを自動生成するクラスで有ることを宣言する注釈。
 * <li>生成される移譲クラスは "{元のクラス名}Handler" である。
 * <li>生成されるパッケージは元のクラスと同じである。
 */
public @interface GenerateDelegateHandler {
	/**
	 * 実装インターフェースの一覧。
	 */
	Class<?>[] implement() default {};
}
