package org.kotemaru.android.fw.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kotemaru.android.fw.thread.ThreadManager;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Documented
/**
 * ハンドラへの移譲メソッドで有ることを宣言する。
 * <li>GenerateDelegateHandler が指定されたクラス以外では意味を持たない。
 * <li>メソッドの戻り値はvoidでなければならない。
 * <li>メソッドは任意の例外を発生させることができる。
 */
public @interface Handle {
	/**
	 * ThreadManagerへ定義済の実行スレッド名。
	 * <li>デフォルトは"WORKER"。
	 */
	String thread() default ThreadManager.WORKER;
	/**
	 * 実行遅延時間(ミリ秒)。
	 * <li>デフォルトは遅延無し。
	 */
	int delay() default 0;
	/**
	 * リトライ回数。
	 * <li>メソッドが例外を上げた場合に再実行する回数。
	 * <li>デフォルトは0。
	 */
	int retry() default 0;
	/**
	 * リトライ間隔(ミリ秒)。
	 * <li>デフォルトは1000ms。
	 */
	int interval() default 1000;
	/**
	 * リトライ間隔の延長率(ミリ秒)。
	 * <li>リトライがが２回目、３回目の場合にリトライ間隔を延長する率。
	 * <li>デフォルトは2.0。
	 */
	float intervalRate() default 2.0F;
}
