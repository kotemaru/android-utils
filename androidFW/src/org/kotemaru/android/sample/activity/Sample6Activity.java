package org.kotemaru.android.sample.activity;

import java.util.ArrayList;
import java.util.List;

import org.kotemaru.android.fw.FwActivity;
import org.kotemaru.android.fw.R;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Sample6Activity extends Activity implements FwActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample6_activity);
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SensorManager sencorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		List<TextView> views = getButtons();
		List<Sensor> sensors = sencorManager.getSensorList(Sensor.TYPE_ALL);

		for (int i=0; i<views.size(); i++) {
			TextView view = views.get(i);
			if (i < sensors.size()) {
				view.setVisibility(View.VISIBLE);
				setListener(view, sensors.get(i));
			} else {
				view.setVisibility(View.INVISIBLE);
			}
		}
	}
	protected void onPause() {
		//SensorManager sencorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//sencorManager.unregisterListener(listener);
	}
	private void setListener(final TextView view, final Sensor sensor) {
		final SensorManager sencorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sencorManager.registerListener(new SensorEventListener(){
			@Override
			public void onSensorChanged(SensorEvent event) {
				String val = sensor.getName()+"\n";
				for (int i=0;i<event.values.length; i++) {
					val += (event.values[i]+"0000").substring(0,5)+",  ";
				}
				view.setText(val+"\n");
			}
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// nop.
			}
		}, sensor, SensorManager.SENSOR_DELAY_UI);
	}
	

	private List<TextView> getButtons() {
		return getButtons((ViewGroup)findViewById(R.id.rootView), new ArrayList<TextView>());
	}

	private List<TextView> getButtons(ViewGroup parent, List<TextView> buttons) {
		for (int i=0;i<parent.getChildCount();i++) {
			View child = parent.getChildAt(i);
			if (child instanceof TextView) {
				buttons.add((TextView) child);
			} else if (child instanceof ViewGroup) {
				getButtons((ViewGroup) child, buttons);
			}
		}
		return buttons;
	}

	@Override
	public void update() {
	}

}
