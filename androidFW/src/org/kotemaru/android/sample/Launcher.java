package org.kotemaru.android.sample;

import org.kotemaru.android.sample.activity.Sample2Activity;
import org.kotemaru.android.sample.activity.Sample3Activity;
import org.kotemaru.android.sample.activity.Sample4Activity;
import org.kotemaru.android.sample.activity.Sample5Activity;
import org.kotemaru.android.sample.activity.Sample6Activity;

import android.content.Context;
import android.content.Intent;

public class Launcher {
	public static void startSample2(Context context) {
		Intent intent = new Intent(context, Sample2Activity.class);
		context.startActivity(intent);
	}

	public static void startSample3(Context context) {
		Intent intent = new Intent(context, Sample3Activity.class);
		context.startActivity(intent);
	}

	public static void startSample4(Context context) {
		Intent intent = new Intent(context, Sample4Activity.class);
		context.startActivity(intent);
	}
	public static void startSample5(Context context) {
		Intent intent = new Intent(context, Sample5Activity.class);
		context.startActivity(intent);
	}

	public static void startSample6(Context context) {
		Intent intent = new Intent(context, Sample6Activity.class);
		context.startActivity(intent);
	}
}
